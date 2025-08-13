package ap.project.network.client;

import ap.project.Main;
import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.game.AbstractCharacter;
import ap.project.network.shared.DTO.UserDTO;
import ap.project.network.shared.KryoRegistry;
import ap.project.network.shared.messages.*;
import ap.project.screen.PreGameScreen;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ap.project.network.shared.enums.MessageType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ap.project.network.shared.KryoRegistry.BUFFER_LIMIT;

public class GameClient
{
    private final String ip = "localhost";

    private static GameClient instance;
    private Client kryoClient;
    private final ConcurrentLinkedQueue<Message> incomingQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> outgoingQueue = new ConcurrentLinkedQueue<>();

    private boolean connected = false;
    private boolean registered =  false;
    private volatile boolean userSyncComplete = false;

    private GameClient()
    {
        kryoClient = new Client(BUFFER_LIMIT, BUFFER_LIMIT);
        kryoClient.start();
        registerClasses(kryoClient.getKryo());

        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Message) {
                    incomingQueue.add((Message) object);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                connected = false;
                System.out.println("Disconnected from server");
            }
        });

        instance = this;
    }

    public static synchronized GameClient getInstance()
    {
        if (instance == null)
        {
            synchronized (GameClient.class)
            {
                if (instance == null)
                {
                    instance = new GameClient();
                }
            }
        }
        return instance;
    }

    private void registerClasses(com.esotericsoftware.kryo.Kryo kryo)
    {
        KryoRegistry.registerClasses(kryo);
    }

    public void connect()
    {
        if (connected) return;

        new Thread(() ->
        {
            try
            {
                // Try to reconnect if possible
                if (kryoClient.isConnected())
                {
                    try
                    {
                        kryoClient.reconnect();
                        connected = true;
                        System.out.println("Reconnected to server!");
                        return;
                    } catch (IOException ex) {
                        System.out.println("Reconnect failed, trying fresh connection");
                    }
                }

                // Establish new connection
                kryoClient.connect(5000, ip, 55555, 55777);
                connected = true;
                System.out.println("Connected to server!");
            } catch (Exception e) {
                System.err.println("Connection failed: " + e.getMessage());
                Gdx.app.postRunnable(() -> {
                    if (Main.getApp().getScreen() instanceof PreGameScreen)
                    {
                        System.out.println("error");
                    }
                });
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

    public boolean isRegistered()
    {
        return registered;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public void requestUserSync()
    {
        send(new UserSyncRequestMessage());
    }

    public void sendUserUpdate(User user)
    {
        send(new UserUpdateMessage(new UserDTO(user)));
    }

    public void setUserSyncComplete(boolean userSyncComplete)
    {
        this.userSyncComplete = userSyncComplete;
    }

    public boolean isUserSyncComplete()
    {
        return userSyncComplete;
    }

    public Client getKryoClient()
    {
        return kryoClient;
    }

    public void checkAndRequestMissingFiles(ArrayList<String> serverFiles)
    {
        File localDir = new File("music");
        if (!localDir.exists()) localDir.mkdir();

        Set<String> localFiles = new HashSet<>();
        for (File file : localDir.listFiles())
        {
            if (file.isFile()) localFiles.add(file.getName());
        }

        for (String serverFile : serverFiles)
        {
            if (!localFiles.contains(serverFile))
            {
                send(new MusicFileRequestMessage(serverFile));
            }
        }
    }
}
