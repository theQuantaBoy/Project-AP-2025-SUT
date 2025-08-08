package ap.project.network.server;

import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.game.Game;
import ap.project.model.game.Lobby;
import ap.project.model.game.Map;
import ap.project.model.game.Player;
import ap.project.network.shared.DTO.UserDTO;
import ap.project.network.shared.Mapper.Mapper;
import ap.project.network.shared.messages.*;
import ap.project.util.SQLiteUtil;

import java.util.ArrayList;

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
            case GAME_PRESENCE:
                handleGamePresenceMessage(client, (PlayerGamePresenceMessage) message);
                break;
            case GAME_STARTED:
                handleGameStartedMessage(client, (GameStartedMessage) message);
                break;
            case LEAVE_LOBBY_REQUEST:
                handleLeaveLobbyRequestMessage(client, (LeaveLobbyMessage) message);
                break;
            case CLOSE_LOBBY_REQUEST:
                handleCloseLobbyRequestMessage(client, (CloseLobbyRequestMessage) message);
                break;
            case PLAYER_DATA:
                handlePlayerDataMessage(client, (PlayerDataMessage) message);
                break;
            case USER_SYNC_REQUEST:
                handleUserSyncRequest(client);
                break;
            case USER_UPDATE:
                handleUserUpdate(client, (UserUpdateMessage) message);
                break;
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
        System.out.println("Test Received: " + msg.getText());
    }

    private static void handleUserProfile(ClientConnection connection, UserProfileMessage msg)
    {
        int userId = msg.userId;
        GameServer server = GameServer.getInstance();

        // 1. Find user in server's list
        User user = server.getUser(userId);

        if (user == null)
        {
            System.out.println("User not found: " + userId);
            connection.send(new ConnectionFailedMessage("User not registered on server"));
            return;
        }

        System.out.println("Received profile for: " + user.getUsername());

        // 2. Check if user is already connected
        boolean userExists = server.getConnectedClients().stream()
            .anyMatch(c -> c.getUserId() == userId);

        if (userExists)
        {
            System.out.println("User already connected: " + user.getUsername());
            connection.send(new ConnectionFailedMessage("User already connected"));
        } else
        {
            // 3. Register connection
            connection.setUserId(user);
            System.out.println("Registered user: " + user.getUsername());
            connection.send(new ConnectionConfirmedMessage());
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
        GameServer server = GameServer.getInstance();
        User user = server.getUser(client);

        if (user != null)
        {
            user.setMapChoice(msg.mapChoice);
            user.setCharacterChoice(msg.characterChoice);

            SQLiteUtil.saveUserList("saves/app/users_server.db", server.getUsers());

            UserUpdateMessage update = new UserUpdateMessage(new UserDTO(user));
            server.broadcast(update);
        }

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
            client.lobby = lobby;

            client.send(new JoinLobbySuccessMessage(lobby.getName(), lobby.getId()));

            PlayerJoinedLobbyMessage joinMessage = new PlayerJoinedLobbyMessage(
                user.getHashId(),
                lobby.getId()
            );
            server.broadcastToLobby(lobby, joinMessage);

            // Send existing players to new client
            for (User existingUser : lobby.getUsers())
            {
                if (existingUser.getHashId() != user.getHashId())
                {
                    client.send(new PlayerJoinedLobbyMessage(
                        existingUser.getHashId(),
                        lobby.getId()
                    ));
                }
            }
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

        User user = server.getUser(client);
        Lobby lobby = server.getActiveLobby(user);

        if (Math.abs(client.lastX - msg.x) > 0.1 || Math.abs(client.lastY - msg.y) > 0.1)
        {
            server.broadcastToLobby(lobby, new PlayerPositionUpdateMessage(
                user.getHashId(),
                msg.x,
                msg.y,
                msg.direction,
                msg.isMoving
            ));

            client.lastX = msg.x;
            client.lastY = msg.y;
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

            ArrayList<Player> players = new ArrayList<>();
            for (User u : lobby.getUsers())
            {
                players.add(new Player(u));
            }

            Game game = new Game(players);
            String id = game.getId();

            App.setCurrentGame(game);
            game.setCurrentPlayer(players.get(0));

            GameWrapper wrapper = new GameWrapper(game);
            server.getGameWrappers().add(wrapper);

            server.broadcastToLobby(lobby, new GameCreationSuccessMessage(id, playerIDs[0], playerIDs[1], playerIDs[2], playerIDs[3]));
            server.getActiveLobbies().remove(lobby);

            for (User u : lobby.getUsers())
            {
                ClientConnection c = server.findClient(u.getHashId());
                if (c != null)
                {
                    c.setInLobby(false);
                    c.setInGame(true);
                    c.lobby = null;
                    c.wrapper = wrapper;
                }
            }
        }
    }

    private static void handleGameStartedMessage(ClientConnection client, GameStartedMessage msg)
    {
        GameServer server = GameServer.getInstance();

        String gameID = msg.gameID;
        GameWrapper wrapper = server.findGameWrapper(gameID);

        if (wrapper != null && !wrapper.isActive())
        {
            wrapper.activate();
        }
    }

    private static void handleGamePresenceMessage(ClientConnection client, PlayerGamePresenceMessage msg)
    {
        GameServer server = GameServer.getInstance();

        String gameID = msg.gameID;
        GameWrapper wrapper = server.findGameWrapper(gameID);

        for (ClientConnection c : wrapper.getClientConnections())
        {
            if (c.getUserId() != client.getUserId())
            {
                c.send(msg);
            }
        }
    }

    private static void handleLeaveLobbyRequestMessage(ClientConnection client, LeaveLobbyMessage msg)
    {
        GameServer server = GameServer.getInstance();

        User user = server.getUser(client);
        Lobby lobby = server.getActiveLobby(user);

        if (lobby != null)
        {
            lobby.getUsers().remove(user);
            client.setInLobby(false);
            client.lobby = null;
            client.send(new CloseLobbyMessage());
            server.broadcastToLobby(lobby, new PlayerLeftLobbyMessage(lobby.getId(), user.getHashId()));
        }
    }

    private static void handleCloseLobbyRequestMessage(ClientConnection client, CloseLobbyRequestMessage msg)
    {
        GameServer server = GameServer.getInstance();

        User user = server.getUser(client);
        Lobby lobby = server.getActiveLobby(user);

        if (lobby != null)
        {
            User admin = lobby.getAdmin();
            if (user.getHashId() != admin.getHashId())
            {
                client.send(new CloseLobbyErrorMessage("only admin can close lobby"));
                return;
            }

            server.closeLobby(lobby);
            server.getActiveLobbies().remove(lobby);
        }
    }

    private static void handlePlayerDataMessage(ClientConnection client, PlayerDataMessage msg)
    {
        try
        {
            SQLiteUtil.savePlayerState(msg.gameId, String.valueOf(msg.playerDTO.userDTO.hashID), msg.playerDTO);
        } catch (Exception e)
        {
            System.err.println("Error saving player data: " + e.getMessage());
        }
    }

    private static void handleUserSyncRequest(ClientConnection client)
    {
        GameServer server = GameServer.getInstance();
        ArrayList<User> users = server.getUsers();
        ArrayList<UserDTO> dtos = new ArrayList<>();

        for (User user : users)
        {
            dtos.add(new UserDTO(user));
        }

        server.broadcast(new UserSyncResponseMessage(dtos));
    }

    private static void handleUserUpdate(ClientConnection client, UserUpdateMessage msg)
    {
        User user = Mapper.fromDTO(msg.userDTO);
        GameServer.getInstance().createOrUpdateUser(user);
    }
}
