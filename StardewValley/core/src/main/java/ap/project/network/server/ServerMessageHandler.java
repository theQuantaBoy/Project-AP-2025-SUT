package ap.project.network.server;

import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.game.DummyGame;
import ap.project.model.game.Game;
import ap.project.model.game.Lobby;
import ap.project.model.game.Player;
import ap.project.network.client.ClientMessageHandler;
import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.DTO.UserDTO;
import ap.project.network.shared.Mapper.Mapper;
import ap.project.network.shared.messages.*;
import ap.project.util.SQLiteUtil;
import com.esotericsoftware.kryonet.Server;

import java.util.*;

import static ap.project.network.server.GameServer.MAX_PLAYERS_FOR_GAME;
import static ap.project.network.server.GameServer.MIN_PLAYERS_FOR_GAME;
import static java.lang.Thread.sleep;

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
                handleGamePresenceMessage(client, (GamePresenceMessage) message);
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
            case SAVE_AND_LEAVE:
                handleSaveAndLeaveMessage(client, (SaveAndLeaveMessage) message);
                break;
            case USER_SAVED_GAME_REQUEST:
                handleUserSavedGamesRequest(client, (UserSavedGameRequestMessage) message);
                break;
            case LOAD_GAME_REQUEST:
                handleLoadSavedGameRequest(client, (LoadGameRequestMessage) message);
                break;
            case LEAVE:
                handleLeaveMessage(client, (LeaveMessage) message);
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

            for (Map.Entry<Integer, PlayerPositionUpdateMessage> entry : lobby.getPlayerPositionCache().entrySet())
            {
                if (entry.getKey() != user.getHashId())
                {
                    client.send(entry.getValue());
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

        PlayerPositionUpdateMessage message = new PlayerPositionUpdateMessage(
            user.getHashId(),
            msg.x,
            msg.y,
            msg.direction,
            msg.isMoving
        );

        if (lobby != null)
        {
            lobby.updatePlayerPosition(user.getHashId(), message);
        }

        if (Math.abs(client.lastX - msg.x) > 0.1 || Math.abs(client.lastY - msg.y) > 0.1)
        {
            server.broadcastToLobby(lobby, message);

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

            GameWrapper wrapper;

            if (lobby.isLoadedGame())
            {
                if (!lobby.allOriginalPlayersPresent())
                {
                    client.send(new GameCreationFailedMessage("all original players must be in the lobby"));
                    return;
                }

                Game game = new Game(lobby.getGameId());

                ArrayList<Player> players = new ArrayList<>();
                for (User u : lobby.getUsers())
                {
                    players.add(new Player(u));
                }

                game.setPlayers(players);
                game.setCurrentTime(SQLiteUtil.loadGameTime(lobby.getGameId()));

                App.setCurrentGame(game);
                game.setCurrentPlayer(players.get(0));

                wrapper = new GameWrapper(game);
                server.getGameWrappers().add(wrapper);

                server.broadcastToLobby(lobby, new LoadedGameStartedMessage(game.getId(), game.getCurrentTime()));
                server.getActiveLobbies().remove(lobby);
            } else
            {
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

                wrapper = new GameWrapper(game);
                server.getGameWrappers().add(wrapper);

                server.broadcastToLobby(lobby, new GameCreationSuccessMessage(id, playerIDs[0], playerIDs[1], playerIDs[2], playerIDs[3]));
                server.getActiveLobbies().remove(lobby);
            }

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

        if (wrapper != null)
        {
            PlayerDTO dto = msg.playerDTO;
            wrapper.updatePlayerState(dto.userDTO.hashID, dto);
            System.out.println("added " + dto.userDTO.username);
            wrapper.activate();
        }
    }

    private static void handleGamePresenceMessage(ClientConnection client, GamePresenceMessage msg)
    {
        GameServer server = GameServer.getInstance();

        String gameID = msg.gameID;
        GameWrapper wrapper = server.findGameWrapper(gameID);

        if (wrapper != null)
        {
            for (ClientConnection c : wrapper.getClientConnections())
            {
                if (c.getUserId() != client.getUserId())
                {
                    c.send(msg);
                }
            }

            wrapper.updatePlayerPresence(msg.userID);
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
            GameServer server = GameServer.getInstance();
            GameWrapper wrapper = server.findGameWrapper(msg.gameId);

            if (wrapper != null)
            {
                // Update cache instead of saving immediately
                wrapper.updatePlayerState(msg.playerDTO.userDTO.hashID, msg.playerDTO);
            }

            // Periodic save handled by GameWrapper
        } catch (Exception e)
        {
            System.err.println("Error handling player data: " + e.getMessage());
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

    private static void handleSaveAndLeaveMessage(ClientConnection client, SaveAndLeaveMessage msg)
    {
        GameServer server = GameServer.getInstance();
        GameWrapper wrapper = server.findGameWrapper(msg.gameId);

        if (wrapper != null)
        {
            wrapper.handleSaveAndLeave(msg.playerId);

            // Update connection state
            client.setInGame(false);
            client.wrapper = null;
        }
    }

    private static void handleUserSavedGamesRequest(ClientConnection client, UserSavedGameRequestMessage msg)
    {
        GameServer server = GameServer.getInstance();
        User user = server.getUser(client);

        ArrayList<DummyGame> savedGames = App.getGamesForPlayer(user.getHashId());
        client.send(new UserSavedGameResponseMessage(savedGames));
    }

    private static void handleLoadSavedGameRequest(ClientConnection client, LoadGameRequestMessage msg)
    {
        GameServer server = GameServer.getInstance();
        User user = server.getUser(client);

        for (GameWrapper game : server.getGameWrappers())
        {
            if (game.isActive() && game.getId().equals(msg.gameId))
            {
                client.send(new JoinActiveGameMessage(msg.gameId));

                Iterator<ClientConnection> iterator = (game.getClientConnections()).iterator();
                while (iterator.hasNext())
                {
                    ClientConnection cc = iterator.next();
                    if (cc.getUserId() == user.getHashId())
                    {
                        iterator.remove();
                    }
                }

                client.setInGame(true);
                client.wrapper = game;

                game.getClientConnections().add(client);
                return;
            }
        }

        Set<Integer> playerIDs = App.loadGamePlayers(msg.gameId);
        Lobby lobby = new Lobby("Loaded Game", user, true, msg.gameId, playerIDs, msg.gameId);

        if (server.addLobby(lobby))
        {
            client.send(new LobbyCreatedMessage(lobby.getName(), lobby.getId(), false, true));
            client.setInLobby(true);
            client.lobby = lobby;
            client.send(new LoadGameSuccessMessage(lobby.getName(), lobby.getId()));
        } else
        {
            client.send(new LoadGameFailedMessage("couldn't add lobby"));
        }
    }

    private static void handleLeaveMessage(ClientConnection client, LeaveMessage msg)
    {
        GameServer server = GameServer.getInstance();
        server.getConnectedClients().remove(client);
        client.getConnection().close();
    }
}
