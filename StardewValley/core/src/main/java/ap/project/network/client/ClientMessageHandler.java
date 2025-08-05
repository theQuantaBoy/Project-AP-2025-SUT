package ap.project.network.client;

import ap.project.Main;
import ap.project.model.App.App;
import ap.project.network.shared.messages.*;
import ap.project.screen.LobbyScreen;
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
            case PLAYER_JOINED_LOBBY:
                handlePlayerJoinedLobbyMessage((PlayerJoinedLobbyMessage) message);
                break;
            case PLAYER_POSITION_UPDATE:
                handleUpdatePlayerPositionMessage((PlayerPositionUpdateMessage) message);
                break;
            case PLAYER_LEFT_LOBBY:
                handlePlayerLeftLobbyMessage((PlayerLeftLobbyMessage) message);
                break;
            case GAME_CREATION_FAILED:
                handleGameCreationFailedMessage((GameCreationFailedMessage) message);
                break;
            case GAME_CREATED_SUCCESSFULLY:
                handleGameCreationSuccessMessage((GameCreationSuccessMessage) message);
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

    private static void handlePlayerJoinedLobbyMessage(PlayerJoinedLobbyMessage message)
    {
        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();

            int userId = message.userId;
            String username = message.username;
            String nickname = message.nickname;
            int avatarChoice = message.avatarChoice;
            int mapChoice = message.mapChoice;

            if (!ls.getLobbyId().equals(message.lobbyId))
            {
                ls.showText("Error: Lobby ID mismatch");
                return;
            }

            if (ls.addOtherPlayer(userId, username, nickname, avatarChoice, mapChoice))
            {
                ls.showText(username + " joined the lobby");
            }
        }
    }

    private static void handleUpdatePlayerPositionMessage(PlayerPositionUpdateMessage message)
    {
        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();

            int userId = message.userId;
            float x = message.x;
            float y = message.y;
            byte direction = message.direction;
            boolean isMoving = message.isMoving;

            ls.updatePlayerPosition(userId, x, y, direction, isMoving);
        }
    }

    private static void handlePlayerLeftLobbyMessage(PlayerLeftLobbyMessage message)
    {
        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();

            String lobbyId = message.lobbyID;

            if (!ls.getLobbyId().equals(lobbyId))
            {
                ls.showText("Error: Lobby ID mismatch");
                return;
            }

            int userId = message.userID;
            ls.removeOtherPlayer(userId);
        }
    }

    private static void handleGameCreationFailedMessage(GameCreationFailedMessage message)
    {
        String text = message.reason;

        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            ((LobbyScreen) Main.getApp().getScreen()).showText("Failed to Start Game: " + text);
        }
    }

    private static void handleGameCreationSuccessMessage(GameCreationSuccessMessage message)
    {
        int[] userIDs = {message.player_1_id, message.player_2_id, message.player_3_id, message.player_4_id};

        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();
            ls.createGame(userIDs);
        }
    }
}
