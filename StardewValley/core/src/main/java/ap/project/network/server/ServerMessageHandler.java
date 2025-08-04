package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.game.Lobby;
import ap.project.network.shared.messages.*;

import static ap.project.network.server.GameServer.MAX_PLAYERS_FOR_GAME;

public class ServerMessageHandler
{
    public static void handle(ClientConnection client, Message message)
    {
        switch (message.getType())
        {
            case TEST:
                handleTestMessage((TestMessage) message);
                break;
            case USER_PROFILE:
                handleUserProfile(client, (UserProfileMessage) message);
                break;
            case PRE_LOBBY_PRESENCE:
                handlePreLobbyPresence(client, (PreLobbyPresenceMessage) message);
                break;
            case USER_CHOICE:
                handleUserChoice(client, (UserChoicesMessage) message);
                break;
            case LOBBY_CREATION_MESSAGE:
                handleLobbyCreation(client, (LobbyCreationPermissionMessage)  message);
                break;
            case JOIN_LOBBY_REQUEST:
                handleJoinLobbyRequestMessage(client, (LobbyJoinRequestMessage) message);
                break;
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
        System.out.println("Test Received: " + msg.getText());
    }

    private static void handleUserProfile(ClientConnection connection, UserProfileMessage msg)
    {
        String username = msg.username;
        String nickname = msg.nickname;
        Gender gender = Gender.getGender(msg.gender);
        int id = msg.userId;

        User user = new User(username, nickname, gender, id);

        System.out.println("Received profile: " + user.getUsername());

        // Check if user already exists in server's connection list
        GameServer server = GameServer.getInstance();
        boolean userExists = server.getConnectedClients().stream()
            .anyMatch(c -> c.getUserId() == user.getHashId());

        if (userExists) {
            System.out.println("User already connected: " + user.getUsername());
            connection.send(new ConnectionFailedMessage("User already connected"));
        } else {
            // Register user
            connection.setUserId(user);
            System.out.println("Registered user: " + user.getUsername());
            connection.send(new ConnectionConfirmedMessage());

            // Add to server's user list
            server.addUser(user);
        }
    }

    private static void handlePreLobbyPresence(ClientConnection client, PreLobbyPresenceMessage msg)
    {
        // Update online status
        client.setOnline(true);
        client.lastOnlineCheckTime = 0;

        // Send confirmation
        client.send(new PreLobbyConfirmationMessage());
    }

    private static void handleUserChoice(ClientConnection client, UserChoicesMessage msg)
    {
        // Update client preferences
        client.characterChoice = msg.characterChoice;
        client.mapChoice = msg.mapChoice;

        // Send confirmation
        client.send(new PreLobbyConfirmationMessage());
    }

    private static void handleLobbyCreation(ClientConnection client, LobbyCreationPermissionMessage msg)
    {
        GameServer server = GameServer.getInstance();

        String name = msg.name;
        String password = msg.password;
        boolean isPrivate =  msg.isPrivate;
        boolean isVisible = msg.isVisible;

        if (!client.isInLobby())
        {
            Lobby lobby;
            User user = server.getUser(client);

            if (user != null)
            {
                if (isPrivate)
                {
                    lobby = new Lobby(name, user, isVisible);
                } else
                {
                    lobby = new Lobby(name, password, user, isVisible);
                }

                String result = server.addLobby(lobby);
                if (result != null)
                {
                    System.out.println("lobby created " + result);
                    client.send(new LobbyCreatedMessage(name, result, isPrivate, isVisible));
                    return;
                }
            }
        }

        client.send(new LobbyCreationFailedMessage());
    }

    private static void handleJoinLobbyRequestMessage(ClientConnection client, LobbyJoinRequestMessage msg)
    {
        GameServer server = GameServer.getInstance();

        String id = msg.id;
        String password = msg.password;

        if (id != null)
        {
            Lobby lobby = server.findLobby(id);

            if (lobby == null)
            {
                client.send(new JoinLobbyErrorMessage("lobby not found"));
                return;
            }

            if (lobby.isPrivate())
            {
                if (!lobby.getPassword().equals(password))
                {
                    client.send(new JoinLobbyErrorMessage("invalid password"));
                    return;
                }
            }

            int joinedUsers = lobby.getUsers().size();
            if (joinedUsers >= MAX_PLAYERS_FOR_GAME)
            {
                client.send(new JoinLobbyErrorMessage("lobby is full :("));
                return;
            }

            User user = server.getUser(client);
            if (user == null)
            {
                client.send(new JoinLobbyErrorMessage("user not found"));
                return;
            }

            lobby.getUsers().add(user);
            client.setInLobby(true);
            client.send(new JoinLobbySuccessMessage(lobby.getName(), lobby.getId()));
        } else
        {
            client.send(new JoinLobbyErrorMessage("invalid id"));
        }
    }
}
