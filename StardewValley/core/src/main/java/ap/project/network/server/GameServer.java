package ap.project.network.server;

import ap.project.network.shared.enums.MessageType;
import ap.project.network.shared.messages.Message;
import ap.project.network.shared.messages.PlayerPositionMessage;
import ap.project.network.shared.messages.TestMessage;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameServer
{
    private static GameServer instance;
    private final Server kryoServer;
    private final Map<String, ClientConnection> players = new HashMap<>();

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
                players.put(connection.toString(), new ClientConnection(connection));
            }

            @Override
            public void received(Connection connection, Object object)
            {
                if (object instanceof Message)
                {
                    ServerMessageHandler.handle(
                        players.get(connection.toString()),
                        (Message) object
                    );
                }
            }
        });
    }

    private void registerClasses(com.esotericsoftware.kryo.Kryo kryo)
    {
        kryo.register(MessageType.class);
        kryo.register(PlayerPositionMessage.class);
        kryo.register(TestMessage.class);

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
