package ap.project.network.server;

import ap.project.model.game.Farm;
import ap.project.model.game.Game;
import ap.project.model.game.Player;
import ap.project.model.game.Time;
import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.messages.GameShutdownMessage;
import ap.project.network.shared.messages.GameTimeSyncMessage;
import ap.project.network.shared.messages.Message;
import ap.project.network.shared.messages.UpdateGameMinuteMessage;
import ap.project.util.SQLiteUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameWrapper
{
    private final GameServer server;
    private final Game game;
    private final String id;

    private boolean isActive = false;
    private final ArrayList<ClientConnection> clientConnections;

    private long lastTimeSync;
    private float secondAccumulator = 0;
    private static final float MINUTE_UPDATE_INTERVAL = 1.0f;
    private long lastMinuteAdvance;

    private boolean gameStarted = false;

    private float saveTimer = 0;
    private static final float AUTO_SAVE_INTERVAL = 120; // 2 minutes
    private final Map<Integer, Long> lastPlayerPresence = new ConcurrentHashMap<>();

    private final Map<Integer, PlayerDTO> playerStateCache = new ConcurrentHashMap<>();

    public String getId()
    {
        return id;
    }

    public GameWrapper(Game game)
    {
        this.game = game;
        server = GameServer.getInstance();
        id = game.getId();

        lastTimeSync = System.currentTimeMillis();
        lastMinuteAdvance = 0;

        clientConnections = new ArrayList<>();
        for (Player player : game.getPlayers())
        {
            ClientConnection c = server.findClient(player.getUser().getHashId());
            if (c != null)
            {
                clientConnections.add(c);
            }
            lastPlayerPresence.put(player.getUser().getHashId(), System.currentTimeMillis());
        }

        gameStarted = true;
    }

    public void updatePlayerState(int playerId, PlayerDTO dto)
    {
        playerStateCache.put(playerId, dto);
    }

    public void update(float delta)
    {
        if (!isActive) return;

        secondAccumulator += delta;

        if (secondAccumulator >= MINUTE_UPDATE_INTERVAL)
        {
            game.getCurrentTime().updateMinute(1);
            broadcastMessage(new UpdateGameMinuteMessage());
            secondAccumulator = 0;

            if (game.getCurrentTime().getMinute() == 0)
            {
                syncGameTime();
            }
        }

        // Auto-save logic
        saveTimer += delta;
        if (saveTimer >= AUTO_SAVE_INTERVAL)
        {
            saveGameState();
            saveTimer = 0;
        }

        // Check for disconnected players
        checkPlayerPresence();
    }

    private void syncGameTime()
    {
        if (game.getCurrentTime().getMinute() == 0)
        {
            Time time = game.getCurrentTime();

            int minute = time.getMinute();
            int hour = time.getHour();
            int day = time.getDay();
            int totalHours = time.getTotalHoursPassed();
            int totalDays = time.getTotalDaysPassed();
            String currentWeather = time.getCurrentWeather().toString();
            String tomorrowWeather = time.getTomorrowWeather().toString();

            GameTimeSyncMessage msg = new GameTimeSyncMessage(minute, hour, day, totalHours, totalDays,  currentWeather, tomorrowWeather);
            broadcastMessage(msg);
        }
    }

    public void activate()
    {
        isActive = true;
    }

    public void pause()
    {
        isActive = false;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public Game getGame()
    {
        return game;
    }

    public ArrayList<ClientConnection> getClientConnections()
    {
        return clientConnections;
    }

    public void broadcastMessage(Message message)
    {
        for (ClientConnection c : clientConnections)
        {
            c.send(message);
        }
    }

    private void saveGameState()
    {
        // Save game time
        SQLiteUtil.saveGameTime(game.getId(), game.getCurrentTime());

        // Save all players from cache
        for (Map.Entry<Integer, PlayerDTO> entry : playerStateCache.entrySet())
        {
            try
            {
                SQLiteUtil.savePlayerState(
                    game.getId(),
                    String.valueOf(entry.getKey()),
                    entry.getValue()
                );
            } catch (Exception e)
            {
                System.err.println("Error saving player state: " + e.getMessage());
            }
        }
    }

    public void updatePlayerPresence(int playerId)
    {
        lastPlayerPresence.put(playerId, System.currentTimeMillis());
    }

    private void checkPlayerPresence()
    {
        long currentTime = System.currentTimeMillis();
        ArrayList<Integer> disconnectedPlayers = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : lastPlayerPresence.entrySet())
        {
            if (currentTime - entry.getValue() > 120_000) // 2-minute DC
            {
                disconnectedPlayers.add(entry.getKey());
            }
        }

        if (!disconnectedPlayers.isEmpty())
        {
            handleDisconnectedPlayers(disconnectedPlayers);
        }
    }

    private void handleDisconnectedPlayers(ArrayList<Integer> playerIds)
    {
        // Save game state before shutdown
        saveGameState();

        // Notify all players
        GameShutdownMessage msg = new GameShutdownMessage(
            "Game closed due to player disconnection",
            new ArrayList<>(playerIds)
        );
        broadcastMessage(msg);

        // Deactivate wrapper
        pause();
        GameServer.getInstance().getGameWrappers().remove(this);
    }

    public void handleSaveAndLeave(int playerId)
    {
        // Save the leaving player's state if available
        saveGameState();

        // Notify other players
        List<Integer> disconnectedList = new ArrayList<>();
        disconnectedList.add(playerId);

        GameShutdownMessage msg = new GameShutdownMessage(
            "Game closed by player request",
            disconnectedList
        );
        broadcastMessage(msg);

        // Deactivate wrapper
        pause();
    }

    private Player findPlayerById(int playerId)
    {
        for (Player player : game.getPlayers())
        {
            if (player.getUser().getHashId() == playerId)
            {
                return player;
            }
        }
        return null;
    }
}
