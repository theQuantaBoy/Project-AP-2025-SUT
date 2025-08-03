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
                break;
            case PLAYER_POSITION:
                handlePlayerPosition(client, (PlayerPositionMessage) message);
                break;
            // Add other cases
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
//        System.out.println("received: " + msg.getText());
    }

    private static void handlePlayerPosition(ClientConnection sender, PlayerPositionMessage msg)
    {
        // Broadcast to all other clients
        GameServer server = GameServer.getInstance();
        for (ClientConnection client : server.getConnectedClients())
        {
            if (client != sender)
            {
                client.send(msg);
            }
        }
    }
}
