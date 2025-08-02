package ap.project.network.server;

import ap.project.model.enums.MapTypes;
import ap.project.network.shared.enums.MessageType;
import ap.project.network.shared.messages.*;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameServer
{
    private static GameServer instance;
    private final Server kryoServer;
    private final Map<String, ClientConnection> players = new HashMap<>();

    private final Map<String, ClientConnection> waitingPlayers = new HashMap<>();
    private final int MIN_PLAYERS = 2;

    private void handleUserProfile(ClientConnection client, UserProfileMessage msg)
    {
        System.out.println("Received profile: " + msg.username);
        client.setUserProfile(msg);
        client.setPlayerId(msg.username);

        // Add to waiting players
        waitingPlayers.put(msg.username, client);

        // Send ACK
        client.send(new AckMessage("USER_PROFILE_RECEIVED"));
        System.out.println("Waiting players: " + waitingPlayers.size());

        // Start game if enough players
        if (waitingPlayers.size() >= MIN_PLAYERS)
        {
            startGame();
        }
    }

    private void startGame() {
        GameConfigMessage config = new GameConfigMessage();
        int idx = 0;
        MapTypes[] mapTypes = {MapTypes.MINING, MapTypes.FISHING,
            MapTypes.FORAGING, MapTypes.COMBAT};

        for (ClientConnection client : waitingPlayers.values()) {
            GameConfigMessage.PlayerConfig pc = new GameConfigMessage.PlayerConfig();
            pc.username = client.getUserProfile().username;
            pc.nickname = client.getUserProfile().nickname;
            pc.mapType = mapTypes[idx % mapTypes.length];
            pc.playerIndex = idx;
            pc.avatarId = client.getUserProfile().avatarId;

            config.players.add(pc);
            client.setPlayerConfig(pc);
            idx++;
        }

        // Send config to each client
        for (ClientConnection client : waitingPlayers.values()) {
            // Find this client's index
            int playerIndex = config.players.indexOf(client.getPlayerConfig());
            config.yourPlayerIndex = playerIndex;

            client.send(config);
        }

        waitingPlayers.clear();
    }

    public GameServer() throws IOException
    {
        kryoServer = new Server();
        kryoServer.start();
        registerClasses(kryoServer.getKryo());
        kryoServer.bind(54555, 54777);

        kryoServer.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                String connId = connection.toString();
                ClientConnection client = new ClientConnection(connection);
                players.put(connId, client);
                System.out.println("Client connected: " + connId);
            }

            @Override
            public void disconnected(Connection connection) {
                String connId = connection.toString();
                players.remove(connId);
                waitingPlayers.values().removeIf(c -> c.getConnection().toString().equals(connId));
                System.out.println("Client disconnected: " + connId);
            }

            @Override
            public void received(Connection connection, Object object) {
                ClientConnection client = players.get(connection.toString());
                if (client == null) return;

                if (object instanceof UserProfileMessage) {
                    handleUserProfile(client, (UserProfileMessage) object);
                } else if (object instanceof Message) {
                    ServerMessageHandler.handle(client, (Message) object);
                }
            }
        });
    }

    private void registerClasses(com.esotericsoftware.kryo.Kryo kryo)
    {
        kryo.register(MessageType.class);
        kryo.register(PlayerPositionMessage.class);
        kryo.register(TestMessage.class);

        kryo.register(UserProfileMessage.class);
        kryo.register(GameConfigMessage.class);
        kryo.register(GameConfigMessage.PlayerConfig.class);
        kryo.register(AckMessage.class);
        kryo.register(ArrayList.class);

        kryo.register(ap.project.model.enums.Gender.class);
        kryo.register(ap.project.model.enums.MapTypes.class);

        // Register all other message classes ...
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
            } catch (IOException e) {}
        }

        return instance;
    }

    public static void main(String[] args)
    {
        try
        {
            new GameServer();
            System.out.println("Server started!");
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }
}
