package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.model.enums.MapTypes;
import ap.project.model.game.Game;
import ap.project.model.game.Lobby;
import ap.project.model.game.Time;
import ap.project.network.shared.KryoRegistry;
import ap.project.network.shared.messages.*;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.*;

public class GameServer
{
    private static GameServer instance;
    private final Server kryoServer;

    private final ArrayList<User> users = new ArrayList<>();
    private final HashMap<String, ClientConnection> connections = new HashMap<>();
    private final ArrayList<GameWrapper> games = new ArrayList<>();
    private final ArrayList<Lobby> activeLobbies = new ArrayList<>();

    private volatile boolean running = true;
    private Thread gameThread;

    private float periodicMessageTimer = 0;
    private static final float PERIODIC_MESSAGE_INTERVAL = 0.1f;

    private final int MIN_PLAYERS_FOR_GAME = 2;
    private final int MAX_PLAYERS_FOR_GAME = 4;

    public void update(float delta)
    {
        periodicMessageTimer += delta;

        // Send periodic messages every 100ms
        if (periodicMessageTimer >= PERIODIC_MESSAGE_INTERVAL)
        {
            sendPeriodicMessages(delta);

            periodicMessageTimer = 0;
        }

        // Add other server update logic here
        // (game state updates, lobby management, etc.)
    }

    private void sendPeriodicMessages(float delta)
    {
        for (ClientConnection client : connections.values())
        {
            if (!client.isOnline())
            {
                client.lastOnlineCheckTime += delta;
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

    public String addLobby(Lobby lobby)
    {
        for (Lobby l : activeLobbies)
        {
            if (l.getId() == lobby.getId())
            {
                return null;
            }
        }

        activeLobbies.add(lobby);
        return lobby.getId();
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
        return connections.values().stream()
            .filter(c -> c.getUserId() == userId)
            .findFirst()
            .orElse(null);
    }

    public GameServer() throws IOException
    {
        kryoServer = new Server();
        kryoServer.start();
        registerClasses(kryoServer.getKryo());
        kryoServer.bind(54555, 54777);

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

    public static void main(String[] args)
    {
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
}
