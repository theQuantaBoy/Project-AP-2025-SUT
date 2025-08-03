package ap.project.network.client;

import ap.project.Main;
import ap.project.network.shared.messages.*;
import ap.project.screen.WorldScreen;

public class ClientMessageHandler
{
    public static void handle(Message message)
    {
        switch (message.getType())
        {
            case TEST:
                handleTestMessage((TestMessage) message);
                break;
            case CONNECTION_CONFIRMED:
                handleConnectionConfirmed((ConnectionConfirmedMessage) message);
                break;
            case CONNECTION_FAILED:
                handleConnectionFailed((ConnectionFailedMessage) message);
                break;
            // Add other cases
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
        System.out.println("Test Received: " + msg.getText());
    }


    private static void handleConnectionConfirmed(ConnectionConfirmedMessage msg)
    {
        System.out.println("Server confirmed user registration");
        GameClient.getInstance().setRegistered(true);
    }

    private static void handleConnectionFailed(ConnectionFailedMessage msg)
    {
        System.out.println("Connection failed: " + msg.getReason());
        GameClient.getInstance().setRegistered(false);
    }
}
