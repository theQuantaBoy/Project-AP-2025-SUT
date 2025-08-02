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
            case GAME_CONFIG:
                handleGameConfig((GameConfigMessage) message);
                break;
            case ACK:
                System.out.println("ACK: " + ((AckMessage) message).message);
                break;
            case PLAYER_POSITION:
                handlePlayerPosition((PlayerPositionMessage) message);
                break;
            // Add other cases
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
//        System.out.println("received: " + msg.getText());
    }

    private static void handleGameConfig(GameConfigMessage config)
    {
        Main.getApp().onGameConfigReceived(config);
    }

    private static void handlePlayerPosition(PlayerPositionMessage msg)
    {
        WorldScreen worldScreen = WorldScreen.getInstance();
        if (worldScreen != null)
        {
            worldScreen.handlePlayerPosition(msg);
        }
    }
}
