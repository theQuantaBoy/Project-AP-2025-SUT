package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.game.Game;
import ap.project.model.game.Lobby;
import ap.project.model.game.Point;
import ap.project.network.client.ClientMessageHandler;
import ap.project.network.shared.messages.*;

import static ap.project.network.server.GameServer.MAX_PLAYERS_FOR_GAME;
import static ap.project.network.server.GameServer.MIN_PLAYERS_FOR_GAME;

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
            case LOBBY_PRESENCE:
                handleLobbyPresenceMessage(client, (LobbyPresenceMessage) message);
                break;
            case CREATE_GAME_REQUEST:
                handleCreateGameRequestMessage(client, (CreateGameRequestMessage) message);
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

        // Add validation
        if (name == null || name.trim().isEmpty())
        {
            client.send(new LobbyCreationFailedMessage());
            return;
        }

        User user = server.getUser(client);
        if (user == null)
        {
            client.send(new LobbyCreationFailedMessage());
            return;
        }

        Lobby lobby = isPrivate ?
            new Lobby(name, password, user, isVisible) :
            new Lobby(name, user, isVisible);

        // Add to server
        if (server.addLobby(lobby))
        {
            client.send(new LobbyCreatedMessage(lobby.getName(), lobby.getId(), isPrivate, isVisible));
            client.setInLobby(true);
            client.lobby = lobby;
            client.send(new JoinLobbySuccessMessage(lobby.getName(), lobby.getId()));
        } else
        {
            client.send(new LobbyCreationFailedMessage());
        }
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

            if (client.isInLobby() && client.lobby != null)
            {
                client.send(new JoinLobbyErrorMessage("you are already in a lobby"));
                return;
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
            client.setInLobby(true);
            client.lobby = lobby;

            for (User u : lobby.getUsers())
            {
                if (u.getHashId() != user.getHashId())
                {
                    ClientConnection c = server.findClient(u.getHashId());
                    client.send(new PlayerJoinedLobbyMessage(u.getHashId(), u.getUsername(), lobby.getId(), u.getNickname(),
                        c.characterChoice, c.mapChoice));
                }
            }

            server.broadcastToLobby(lobby, new PlayerJoinedLobbyMessage(user.getHashId(), user.getUsername(),
                lobby.getId(), user.getNickname(), client.characterChoice, client.mapChoice));
        } else
        {
            client.send(new JoinLobbyErrorMessage("invalid id"));
        }
    }

    private static void handleLobbyPresenceMessage(ClientConnection client, LobbyPresenceMessage msg)
    {
        GameServer server = GameServer.getInstance();

        client.setOnline(true);
        client.lastOnlineCheckTime = 0;

        float x = msg.x;
        float y = msg.y;
        byte direction = msg.direction;
        boolean isMoving = msg.isMoving;

        User user = server.getUser(client);

        if (user != null)
        {
            Lobby lobby = server.getActiveLobby(user);
            if (lobby != null)
            {
                server.broadcastToLobby(lobby, new PlayerPositionUpdateMessage(user.getHashId(), x, y, direction, isMoving));
            }
        }
    }

    private static void handleCreateGameRequestMessage(ClientConnection client,  CreateGameRequestMessage msg)
    {
        GameServer server = GameServer.getInstance();

        User user = server.getUser(client);
        Lobby lobby = server.getActiveLobby(user);

        if (lobby != null)
        {
            User admin = lobby.getAdmin();
            if (user.getHashId() != admin.getHashId())
            {
                client.send(new GameCreationFailedMessage("only admin can start the game"));
                return;
            }

            int count = lobby.getUsers().size();
            if (count < MIN_PLAYERS_FOR_GAME || count > MAX_PLAYERS_FOR_GAME)
            {
                client.send(new GameCreationFailedMessage("out of range number of players"));
                return;
            }

            int[] playerIDs = new int[4];
            for (int i = 0; i < lobby.getUsers().size(); i++)
            {
                playerIDs[i] = lobby.getUsers().get(i).getHashId();
            }

            server.broadcastToLobby(lobby, new GameCreationSuccessMessage(playerIDs[0], playerIDs[1], playerIDs[2], playerIDs[3]));
        }
    }
}
