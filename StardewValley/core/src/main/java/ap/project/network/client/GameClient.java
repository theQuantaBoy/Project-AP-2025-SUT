package ap.project.network.client;

import ap.project.Main;
import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.game.AbstractCharacter;
import ap.project.network.shared.KryoRegistry;
import ap.project.network.shared.messages.*;
import ap.project.screen.PreGameScreen;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ap.project.network.shared.enums.MessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameClient
{
    private static GameClient instance;
    private Client kryoClient;
    private final ConcurrentLinkedQueue<Message> incomingQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> outgoingQueue = new ConcurrentLinkedQueue<>();

    private boolean connected = false;
    private boolean registered =  false;

    private GameClient()
    {
        kryoClient = new Client();
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

    public void connect(String ip)
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
                kryoClient.connect(5000, ip, 54555, 54777);
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
}
