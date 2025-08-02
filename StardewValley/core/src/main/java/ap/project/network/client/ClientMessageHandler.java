package ap.project.network.client;

import ap.project.network.shared.messages.Message;
import ap.project.network.shared.messages.PlayerPositionMessage;
import ap.project.network.shared.messages.TestMessage;
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
            // Add other cases
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
        System.out.println("received: " + msg.getText());
    }
}
