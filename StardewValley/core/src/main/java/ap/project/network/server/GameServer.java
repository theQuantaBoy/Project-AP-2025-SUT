package ap.project.network.server;

import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.game.Lobby;
import ap.project.network.shared.KryoRegistry;
import ap.project.network.shared.messages.*;
import ap.project.util.SQLiteUtil;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static ap.project.network.shared.KryoRegistry.BUFFER_LIMIT;

public class GameServer
{
    private static GameServer instance;
    private final Server kryoServer;

    private final ArrayList<User> users = new ArrayList<>();
    private final ConcurrentMap<String, ClientConnection> connections = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Lobby> activeLobbies = new CopyOnWriteArrayList<>();
    private final ArrayList<GameWrapper> gameWrappers = new ArrayList<>();

    private volatile boolean running = true;
    private Thread gameThread;

    private float periodicMessageTimer = 0;
    private static final float PERIODIC_MESSAGE_INTERVAL = 0.016f;

    public static final int MIN_PLAYERS_FOR_GAME = 1;
    public static final int MAX_PLAYERS_FOR_GAME = 4;

    private static Map<String, List<byte[]>> fileChunks = new ConcurrentHashMap<>();

    public void update(float delta)
    {
        periodicMessageTimer += delta;

        if (periodicMessageTimer >= PERIODIC_MESSAGE_INTERVAL)
        {
            sendPeriodicMessages(delta);
            handleLobbies(delta);
            handleGames(delta);

            periodicMessageTimer = 0;
        }
    }

    private void handleGames(float delta)
    {
        for (GameWrapper gameWrapper : gameWrappers)
        {
            if (gameWrapper.isActive())
            {
                gameWrapper.update(delta);
            }
        }
    }

    private void handleLobbies(float delta)
    {
        List<Lobby> toRemove = new ArrayList<>();

        for (Lobby lobby : activeLobbies)
        {
            long elapsed = System.currentTimeMillis() - lobby.getCreatedAt();
            int remaining = (int) (300 - (elapsed / 1000));

            if (remaining <= 0 || lobby.getUsers().isEmpty())
            {
                closeLobby(lobby);
                toRemove.add(lobby);
            } else
            {
                broadcastToLobby(lobby, new LobbyTimeUpdateMessage(remaining, lobby.getAdminName(), lobby.getUsersList()));
            }
        }

        activeLobbies.removeAll(toRemove);
    }

    public void closeLobby(Lobby lobby)
    {
        for (User user : new ArrayList<>(lobby.getUsers()))
        {
            ClientConnection client = findClient(user.getHashId());
            if (client != null)
            {
                client.setInLobby(false);
                client.lobby = null;
                client.send(new CloseLobbyMessage());
            }
        }
    }

    private String getActiveLobbiesList()
    {
        StringBuilder sb = new StringBuilder();
        for (Lobby lobby : activeLobbies)
        {
            if (lobby.isVisible())
            {
                sb.append("\'").append(lobby.getName()).append("\'").append("\n");
                sb.append("  -" + (lobby.isPrivate() ? "private lobby" : "public lobby") + "\n");
                sb.append("  -code: " + lobby.getId()).append("\n");
                sb.append("  -present: " + lobby.getUsers().size()).append("\n");
                sb.append("  -admin: " + (lobby.getAdmin() == null ? "none" : lobby.getAdmin().getUsername())).append("\n");
                sb.append("\n").append("------------").append("\n");
            }
        }
        return sb.toString();
    }

    private String getOnlinePlayersList()
    {
        StringBuilder sb = new StringBuilder();
        for (ClientConnection connection : connections.values())
        {
            if (connection.isOnline())
            {
                User user = getUser(connection);
                if (user != null)
                {
                    sb.append(user.getUsername()).append("\n");
                    Lobby lobby = getActiveLobby(user);
                    if (lobby != null)
                    {
                        sb.append(" -lobby: ").append(lobby.getName()).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    public Lobby getActiveLobby(User user)
    {
        for (Lobby lobby : activeLobbies)
        {
            for (User u : lobby.getUsers())
            {
                if (u.getHashId() == user.getHashId())
                {
                    return lobby;
                }
            }
        }

        return null;
    }

    public Lobby findLobby(String hashId)
    {
        for (Lobby lobby : activeLobbies)
        {
            if (lobby.getId().equals(hashId))
            {
                return lobby;
            }
        }

        return null;
    }

    private void sendPeriodicMessages(float delta)
    {
        // Create a copy of the values to avoid ConcurrentModificationException
        List<ClientConnection> clients = new ArrayList<>(connections.values());

        List<ClientConnection> toDisconnect = new ArrayList<>();

        for (ClientConnection client : clients)
        {
            if (!client.isOnline())
            {
                client.lastOnlineCheckTime += delta;
                if (client.lastOnlineCheckTime > 120)
                {
                    toDisconnect.add(client);
                }
            } else
            {
                client.send(new ActiveLobbiesListMessage(getActiveLobbiesList()));
                client.send(new OnlinePlayersListMessage(getOnlinePlayersList()));
            }
        }

        // Process disconnects after iteration
        for (ClientConnection client : toDisconnect)
        {
            User u = getUser(client);
            connections.remove(client);
            users.remove(u);
        }
    }

    public void broadcastToLobby(Lobby lobby, Message message)
    {
        for (User user : lobby.getUsers())
        {
            if (user != null)
            {
                ClientConnection connection = findClient(user.getHashId());
                if (connection != null)
                {
                    connection.send(message);
                }
            }
        }
    }

    private void startGameLoop()
    {
        gameThread = new Thread(() -> {
            final float UPDATE_RATE = 1/60f; // 60 FPS
            float accumulator = 0f;
            long currentTime = System.nanoTime();

            while (running)
            {
                long newTime = System.nanoTime();
                float frameTime = (newTime - currentTime) / 1_000_000_000.0f;
                currentTime = newTime;

                // Prevent spiral of death on slow updates
                if (frameTime > 0.25f) frameTime = 0.25f;

                accumulator += frameTime;

                while (accumulator >= UPDATE_RATE) {
                    update(UPDATE_RATE);
                    accumulator -= UPDATE_RATE;
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        gameThread.setName("Server-GameLoop");
        gameThread.setDaemon(true); // Allow JVM to exit even if thread is running
        gameThread.start();
    }

    public void stop()
    {
        SQLiteUtil.saveUserList("saves/app/users_server.db", new ArrayList<>(users));

        running = false;
        if (gameThread != null)
        {
            gameThread.interrupt();
            try {
                gameThread.join(1000); // Wait 1 second for thread to stop
            } catch (InterruptedException e) {}
        }
        kryoServer.stop();
    }

    public synchronized void addUser(User user)
    {
        // Prevent duplicate users
        if (!users.stream().anyMatch(u -> u.getHashId() == user.getHashId())) {
            users.add(user);
        }
    }

    public boolean addLobby(Lobby lobby)
    {
        for (Lobby l : activeLobbies)
        {
            if (l.getId() == lobby.getId())
            {
                return false;
            }
        }

        activeLobbies.add(lobby);
        return true;
    }

    public User getUser(int id)
    {
        for (User user : users)
        {
            if (user.getHashId() == id)
            {
                return user;
            }
        }

        return null;
    }

    public User getUser(ClientConnection client)
    {
        for (User user : users)
        {
            if (user.getHashId() == client.getUserId())
            {
                return user;
            }
        }

        return null;
    }

    public ClientConnection findClient(int userId)
    {
        for (ClientConnection c : connections.values())
        {
            if (c.getUserId() == userId)
            {
                return c;
            }
        }

        return null;
    }

    public void createOrUpdateUser(User user)
    {
        boolean found = false;
        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).getHashId() == user.getHashId())
            {
                users.set(i, user);
                found = true;
                break;
            }
        }

        if (!found)
        {
            users.add(user);
        }

        SQLiteUtil.saveUserList("saves/app/users_server.db", new ArrayList<>(users));
    }

    public GameServer() throws IOException
    {
        users.addAll(SQLiteUtil.loadUserList("saves/app/users_server.db"));

        kryoServer = new Server(BUFFER_LIMIT, BUFFER_LIMIT);
        kryoServer.start();
        registerClasses(kryoServer.getKryo());
        kryoServer.bind(55555, 55777);

        kryoServer.addListener(new Listener()
        {
            @Override
            public void connected(Connection connection)
            {
                String connId = connection.toString();
                ClientConnection client = new ClientConnection(connection);
                connections.put(connId, client);
                System.out.println("Client connected: " + connId);
            }

            @Override
            public void disconnected(Connection connection)
            {
                String connId = connection.toString();
                connections.remove(connId);
                System.out.println("Client disconnected: " + connId);
            }

            @Override
            public void received(Connection connection, Object object)
            {
                ClientConnection client = connections.get(connection.toString());
                if (client == null) return;

                if (object instanceof Message)
                {
                    ServerMessageHandler.handle(client, (Message) object);
                }
            }
        });

        instance = this;
        startGameLoop();
    }

    public Collection<ClientConnection> getConnectedClients()
    {
        return connections.values();
    }

    private void registerClasses(com.esotericsoftware.kryo.Kryo kryo)
    {
        KryoRegistry.registerClasses(kryo);
    }

    public void broadcast(Message message)
    {
        kryoServer.sendToAllTCP(message);
    }

    public static synchronized GameServer getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new GameServer();
            } catch (IOException e)
            {
            }
        }

        return instance;
    }

    public GameWrapper findGameWrapper(String id)
    {
        for (GameWrapper g : gameWrappers)
        {
            if (g.getId().equals(id))
            {
                return g;
            }
        }

        return null;
    }

    public static void main(String[] args)
    {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new DummyAppListener(), config); // Dummy listener
        App.initialize();

        try
        {
            GameServer server = new GameServer();
            System.out.println("Server started!");

            // Add shutdown hook for clean termination
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                server.stop();
            }));
        } catch (IOException e)
        {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }

    private static class DummyAppListener implements ApplicationListener
    {
        @Override public void create() {}
        @Override public void resize(int width, int height) {}
        @Override public void render() {}
        @Override public void pause() {}
        @Override public void resume() {}
        @Override public void dispose() {}
    }

    public ArrayList<GameWrapper> getGameWrappers()
    {
        return gameWrappers;
    }

    public CopyOnWriteArrayList<Lobby> getActiveLobbies()
    {
        return activeLobbies;
    }

    public ArrayList<User> getUsers()
    {
        return users;
    }

    public void sendMusicList(ClientConnection connection)
    {
        File musicDir = new File("music");
        if (!musicDir.exists()) musicDir.mkdir();

        File[] files = musicDir.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".ogg"));

        ArrayList<String> filenames = new ArrayList<>();
        for (File file : files) {
            filenames.add(file.getName());
        }

        connection.send(new MusicFileListMessage(filenames));
    }

    public static Map<String, List<byte[]>> getFileChunks()
    {
        return fileChunks;
    }

    public void scanAndSyncClientFiles(ClientConnection connection, List<String> clientFiles)
    {
        File musicDir = new File("music");
        if (!musicDir.exists()) musicDir.mkdirs();

        Set<String> serverFiles = new HashSet<>();
        for (File file : musicDir.listFiles())
        {
            if (file.isFile()) serverFiles.add(file.getName());
        }

        for (String clientFile : clientFiles)
        {
            if (!serverFiles.contains(clientFile))
            {
                connection.send(new MusicFileRequestMessage(clientFile));
            }
        }
    }
}
