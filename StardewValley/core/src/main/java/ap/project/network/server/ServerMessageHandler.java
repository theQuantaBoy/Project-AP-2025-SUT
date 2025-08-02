package ap.project.network.server;

import ap.project.network.shared.messages.Message;
import ap.project.network.shared.messages.PlayerPositionMessage;
import ap.project.network.shared.messages.TestMessage;

public class ServerMessageHandler
{
    public static void handle(ClientConnection client, Message message)
    {
        switch (message.getType())
        {
            case TEST:
                handleTestMessage((TestMessage) message);
            // Add other cases
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
        System.out.println("received: " + msg.getText());
    }
}
