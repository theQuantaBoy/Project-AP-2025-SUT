package ap.project.model.game;

import ap.project.model.App.User;
import ap.project.network.shared.messages.PlayerPositionUpdateMessage;
import ap.project.util.StringToNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Lobby
{
    private ArrayList<User> users = new ArrayList<>();
    private final String name;
    private final boolean isPrivate;
    private String password;
    private final String id;
    private final boolean isVisible;

    private final long createdAt;

    private boolean isLoadedGame = false;
    private String gameId;

    private String originalGameId;
    private Set<Integer> originalPlayerIds = new HashSet<>();
    private java.util.Map<Integer, PlayerPositionUpdateMessage> playerPositionCache = new ConcurrentHashMap<>();

    public Lobby(String name, String password, User user, boolean isVisible)
    {
        this.name = name;
        this.password = password;
        this.isPrivate = true;
        users.add(user);
        this.id = StringToNumber.generateId(6);
        this.isVisible = isVisible;
        this.createdAt = System.currentTimeMillis();
    }

    public Lobby(String name, User user, boolean isVisible)
    {
        this.name = name;
        this.isPrivate = false;
        users.add(user);
        this.id = StringToNumber.generateId(6);
        this.isVisible = isVisible;
        this.createdAt = System.currentTimeMillis();
    }

    public Lobby(String name, User admin, boolean isVisible, String originalGameId, Set<Integer> originalPlayerIds, String gameId)
    {
        this(name, admin, isVisible);
        this.originalGameId = originalGameId;
        this.originalPlayerIds = originalPlayerIds;
        this.isLoadedGame = true;
        this.gameId = gameId;
    }

    public void updatePlayerPosition(Integer playerID, PlayerPositionUpdateMessage dto)
    {
        playerPositionCache.put(playerID, dto);
    }

    public Map<Integer, PlayerPositionUpdateMessage> getPlayerPositionCache()
    {
        return playerPositionCache;
    }

    public boolean isLoadedGame()
    {
        return isLoadedGame;
    }

    public boolean allOriginalPlayersPresent()
    {
        Set<Integer> currentPlayerIds = users.stream()
            .map(User::getHashId)
            .collect(Collectors.toSet());

        return currentPlayerIds.containsAll(originalPlayerIds);
    }

    public User getAdmin()
    {
        if (!users.isEmpty())
        {
            return users.get(0);
        }

        return null;
    }

    public String getGameId()
    {
        return gameId;
    }

    public ArrayList<User> getUsers()
    {
        return users;
    }

    public String getName()
    {
        return name;
    }

    public boolean isPrivate()
    {
        return isPrivate;
    }

    public String getPassword()
    {
        return password;
    }

    public String getId()
    {
        return id;
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public long getCreatedAt()
    {
        return createdAt;
    }
}
