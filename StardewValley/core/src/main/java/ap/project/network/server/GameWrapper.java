package ap.project.network.server;

import ap.project.model.game.Game;
import ap.project.model.game.Player;
import ap.project.model.game.Time;
import ap.project.network.shared.messages.GameTimeSyncMessage;
import ap.project.network.shared.messages.Message;
import ap.project.network.shared.messages.UpdateGameMinuteMessage;

import java.util.ArrayList;

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
        }

        gameStarted = true;
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

            syncGameTime();
        }
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
}
