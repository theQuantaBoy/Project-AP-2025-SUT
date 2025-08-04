package ap.project.network.client;

import ap.project.Main;
import ap.project.network.shared.messages.*;
import ap.project.screen.PreLobbyScreen;
import com.badlogic.gdx.Screen;

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
            case PRE_LOBBY_CONFIRMATION:
                handlePreLobbyConfirmationMessage((PreLobbyConfirmationMessage) message);
                break;
            case PRE_LOBBY_ERROR:
                handlePreLobbyErrorMessage((PreLobbyErrorMessage) message);
                break;
            case LOBBY_CREATED:
                handleLobbyCreatedMessage((LobbyCreatedMessage) message);
                break;
            case LOBBY_CREATION_FAILED:
                handleLobbyCreationFailedMessage();
                break;
            case ACTIVE_LOBBIES_LIST:
                handleActiveLobbiesListMessage((ActiveLobbiesListMessage) message);
                break;
            case ONLINE_PLAYERS_LIST:
                handleOnlinePlayersListMessage((OnlinePlayersListMessage) message);
                break;
            case JOIN_LOBBY_SUCCESS:
                handleJoinedLobbySuccessfullyMessage((JoinLobbySuccessMessage) message);
                break;
            case JOIN_LOBBY_ERROR:
                handleFailedToJoinLobbyMessage((JoinLobbyErrorMessage) message);
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

    private static void handlePreLobbyConfirmationMessage(PreLobbyConfirmationMessage msg)
    {
        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).handlePreLobbyConfirmation(msg);
        }
    }

    private static void handlePreLobbyErrorMessage(PreLobbyErrorMessage msg)
    {
        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).handlePreLobbyError(msg);
        }
    }

    private static void handleLobbyCreatedMessage(LobbyCreatedMessage message)
    {
        String name = message.lobbyName;
        String id =  message.lobbyId;

        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).showTextBox("Lobby Created Successfully");
            ((PreLobbyScreen) currentScreen).joinLobby(name, id);
        }
    }

    private static void handleLobbyCreationFailedMessage()
    {
        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).showTextBox("Lobby Creation Failed");
        }
    }

    private static void handleActiveLobbiesListMessage(ActiveLobbiesListMessage msg)
    {
        String list = msg.list;

        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).setActiveLobbiesText(list);
        }
    }

    private static void handleOnlinePlayersListMessage(OnlinePlayersListMessage msg)
    {
        String list = msg.list;

        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).setOnlineUsersText(list);
        }
    }

    private static void handleJoinedLobbySuccessfullyMessage(JoinLobbySuccessMessage message)
    {
        String name = message.name;
        String id = message.id;

        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).joinLobby(name, id);
        }
    }

    private static void handleFailedToJoinLobbyMessage(JoinLobbyErrorMessage message)
    {
        String reason = message.reason;

        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).showTextBox("Failed to Join Lobby: " + reason);
        }
    }
}
