package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.model.enums.MapTypes;
import ap.project.model.game.Game;
import ap.project.model.game.Lobby;
import ap.project.model.game.Time;
import ap.project.network.shared.KryoRegistry;
import ap.project.network.shared.messages.*;
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

    private final int MIN_PLAYERS_FOR_GAME = 2;
    private final int MAX_PLAYERS_FOR_GAME = 4;

    public synchronized void addUser(User user)
    {
        // Prevent duplicate users
        if (!users.stream().anyMatch(u -> u.getHashId() == user.getHashId())) {
            users.add(user);
        }
    }

    private ClientConnection findClient(User user)
    {
        for (ClientConnection c : connections.values())
        {
            if (c.getUserId() == user.getHashId())
            {
                return c;
            }
        }

        return null;
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
            new GameServer();
            System.out.println("Server started!");
        } catch (IOException e)
        {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }
}
