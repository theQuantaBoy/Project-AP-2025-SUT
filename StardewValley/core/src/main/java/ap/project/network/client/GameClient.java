package ap.project.network.client;

import ap.project.model.game.AbstractCharacter;
import ap.project.network.shared.messages.PlayerPositionMessage;
import ap.project.network.shared.messages.TestMessage;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ap.project.network.shared.messages.Message;
import ap.project.network.shared.enums.MessageType;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GameClient
{
    private static GameClient instance;
    private Client kryoClient;
    private final ConcurrentLinkedQueue<Message> incomingQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> outgoingQueue = new ConcurrentLinkedQueue<>();
    private boolean connected;

    private GameClient()
    {
        kryoClient = new Client();
        kryoClient.start();
        registerClasses(kryoClient.getKryo());

        kryoClient.addListener(new Listener()
        {
            @Override
            public void received(Connection connection, Object object)
            {
                if (object instanceof Message) {
                    incomingQueue.add((Message) object);
                }
            }
        });
    }

    public static synchronized GameClient getInstance()
    {
        if (instance == null)
        {
            instance = new GameClient();
        }

        return instance;
    }

    private void registerClasses(com.esotericsoftware.kryo.Kryo kryo)
    {
        kryo.register(MessageType.class);
        kryo.register(PlayerPositionMessage.class);
        kryo.register(TestMessage.class);

        // Register all other message classes ...
    }

    public void connect(String ip)
    {
        new Thread(() ->
        {
            try
            {
                kryoClient.connect(5000, ip, 54555, 54777);
                connected = true;
                System.out.println("Connected to server!");
            } catch (Exception e)
            {
                System.err.println("Connection failed: " + e.getMessage());
            }
        }).start();
    }

    public void send(Message message)
    {
        if (connected)
        {
            outgoingQueue.add(message);
        }
    }

    public void processMessages()
    {
        // Handle incoming
        while (!incomingQueue.isEmpty())
        {
            Message msg = incomingQueue.poll();
            ClientMessageHandler.handle(msg);
        }

        // Send outgoing
        while (!outgoingQueue.isEmpty())
        {
            kryoClient.sendTCP(outgoingQueue.poll());
        }
    }

    public boolean isConnected()
    {
        return connected;
    }
}
