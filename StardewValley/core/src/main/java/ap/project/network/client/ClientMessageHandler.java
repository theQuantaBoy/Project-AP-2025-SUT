package ap.project.network.client;

import ap.project.Main;
import ap.project.control.game.activities.TradeController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.game.Game;
import ap.project.model.game.GameObject;
import ap.project.model.game.Gift;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipData;
import ap.project.network.server.ClientConnection;
import ap.project.network.server.GameServer;
import ap.project.network.server.GameWrapper;
import ap.project.model.App.User;
import ap.project.network.shared.DTO.UserDTO;
import ap.project.network.shared.Mapper.Mapper;
import ap.project.network.shared.messages.*;
import ap.project.screen.LobbyScreen;
import ap.project.screen.PreLobbyScreen;
import ap.project.screen.TradeWindow;
import ap.project.screen.WorldScreen;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
            case CLOSE_LOBBY:
                handleCLoseLobbyMessage();
                break;
            case LOBBY_TIME_UPDATE:
                handleLobbyTimeUpdateMessage((LobbyTimeUpdateMessage) message);
                break;
            case UPDATE_GAME_MINUTE:
                handleUpdateGameTimeMinute((UpdateGameMinuteMessage) message);
                break;
            case GAME_TIME_SYNC:
                handleGameTimeSyncMessage((GameTimeSyncMessage) message);
                break;
            case GAME_PRESENCE:
                handleGamePlayerPresenceMessage((GamePresenceMessage) message);
                break;
            case CLOSE_LOBBY_ERROR:
                handleCLoseLobbyErrorMessage((CloseLobbyErrorMessage) message);
                break;
            case INCOMING_TRADE_REQUEST:
                handleIncomingTradeRequest((IncomingTradeRequestMessage) message);
                break;
            case INCOMING_TRADE_RESPONSE:
                handleIncomingTradeResponse((IncomingTradeResponseMessage) message);
                break;
            case MOVING_ITEM_TO_TRADE:
                handleMovingItemToTrade((MovingItemToTadeMessage) message);
                break;
            case MOVING_ITEM_TO_INVENTORY:
                handleMovingItemToInventory((MovingItemToInventoryMessage) message);
                break;
            case TRADE_CONFIRM:
                handleTradeConfirm((TradeConfirmMessage) message);
                break;
            case TRADE_CANCELLED:
                handleTradeCancelled((TradeCancelMessage)  message);
                break;
            case USER_SYNC_RESPONSE:
                handleUserSyncResponse((UserSyncResponseMessage) message);
                break;
            case USER_UPDATE:
                handleUserUpdate((UserUpdateMessage) message);
                break;
            case GAME_SHUTDOWN:
                handleGameShutDownMessage((GameShutdownMessage) message);
                break;
            case LOAD_GAME_SUCCESS:
                handleLoadGameSuccessMessage((LoadGameSuccessMessage) message);
                break;
            case LOAD_GAME_FAILED:
                handleLoadGameFailedMessage((LoadGameFailedMessage) message);
                break;
            case LOADED_GAME_START:
                handleLoadedGameStartMessage((LoadedGameStartedMessage) message);
                break;
            case PLAYERS_DTO_UPDATE:
                handleOtherPlayersDTOMessage((UpdatePlayerDTOsMessage) message);
                break;
            case JOIN_GAME:
                handleJoinActiveGameMessage((JoinActiveGameMessage) message);
                break;
            case BACKPACK_DTO:
                handleBackPackDTOMessage((BackPackDTOMessage) message);
                break;
            case NEW_MESSAGE:
                handleNewChatMessage((NewChatMessage) message);
                break;
            case NEW_GIFT:
                handleNewGift((NewGiftMessage) message);
                break;
            // Add other cases
        }
    }

    private static void handleTestMessage(TestMessage msg) {
        System.out.println("Test Received: " + msg.getText());
    }


    private static void handleConnectionConfirmed(ConnectionConfirmedMessage msg) {
        System.out.println("Server confirmed user registration");
        GameClient.getInstance().setRegistered(true);
    }

    private static void handleConnectionFailed(ConnectionFailedMessage msg) {
        System.out.println("Connection failed: " + msg.getReason());
        GameClient.getInstance().setRegistered(false);
    }

    private static void handlePreLobbyConfirmationMessage(PreLobbyConfirmationMessage msg) {
        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen) {
            ((PreLobbyScreen) currentScreen).handlePreLobbyConfirmation(msg);
        }
    }

    private static void handlePreLobbyErrorMessage(PreLobbyErrorMessage msg) {
        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen) {
            ((PreLobbyScreen) currentScreen).handlePreLobbyError(msg);
        }
    }

    private static void handleLobbyCreatedMessage(LobbyCreatedMessage message) {
        String name = message.lobbyName;
        String id = message.lobbyId;

        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen) {
            ((PreLobbyScreen) currentScreen).showTextBox("Lobby Created Successfully");
            ((PreLobbyScreen) currentScreen).joinLobby(name, id);
        }
    }

    private static void handleLobbyCreationFailedMessage() {
        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen) {
            ((PreLobbyScreen) currentScreen).showTextBox("Lobby Creation Failed");
        }
    }

    private static void handleActiveLobbiesListMessage(ActiveLobbiesListMessage msg) {
        String list = msg.list;

        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            PreLobbyScreen pls = (PreLobbyScreen) currentScreen;
            if (!pls.getActiveLobbiesText().equals(list))
            {
                ((PreLobbyScreen) currentScreen).setActiveLobbiesText(list);
                pls.refreshActiveLobbiesList();
            }
        }
    }

    private static void handleOnlinePlayersListMessage(OnlinePlayersListMessage msg) {
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

            if (!ls.getLobbyId().equals(message.lobbyId))
            {
                ls.showText("Error: Lobby ID mismatch");
                return;
            }

            if (ls.addOtherPlayer(message.userId))
            {
                User user = App.getUserByHashId(message.userId);
                if (user != null)
                {
                    ls.showText(user.getUsername() + " joined the lobby");
                }
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
        String id = message.gameID;

        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();
            ls.createGame(id, userIDs);
        }
    }

    private static void handleCLoseLobbyMessage()
    {
        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            Main.getApp().setScreen(new PreLobbyScreen());
        }
    }

    private static void handleLobbyTimeUpdateMessage(LobbyTimeUpdateMessage message)
    {
        int remainingSeconds = message.remainingSeconds;
        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();
            ls.setRemainingTime(remainingSeconds);
        }
    }

    private static void handleUpdateGameTimeMinute(UpdateGameMinuteMessage message)
    {
        if (Main.getApp().getScreen() instanceof WorldScreen)
        {
            WorldScreen ws = (WorldScreen) Main.getApp().getScreen();
            ws.updateTimeMinute();
        }
    }

    private static void handleGameTimeSyncMessage(GameTimeSyncMessage message)
    {
        int minute = message.minute;
        int hour = message.hour;
        int day = message.day;
        int totalHours = message.totalHours;
        int totalDays = message.totalDays;
        String currentWeather = message.currentWeather;
        String tomorrowWeather = message.tomorrowWeather;

        if (Main.getApp().getScreen() instanceof WorldScreen)
        {
            WorldScreen ws = (WorldScreen) Main.getApp().getScreen();
            ws.syncTime(minute, hour, day, totalHours, totalDays, currentWeather, tomorrowWeather);
        }
    }

    private static void handleGamePlayerPresenceMessage(GamePresenceMessage message)
    {
        String gameID = message.gameID;
        int userID = message.userID;

        float x = message.x;
        float y = message.y;
        byte direction = message.direction;
        boolean isMoving = message.isMoving;

        boolean isInFarm = message.isInFarm;
        boolean isInCity = message.isInCity;
        boolean isInGreenHouse = message.isInGreenHouse;
        boolean isInHome = message.isInHome;
        boolean isInZeidiesFarm = message.isInZeidiesFarm;
        boolean isInZeidiesHome = message.isInZeidiesHome;
        boolean isInShop = message.isInShop;

        String currentShop = message.currentShop;

        if (Main.getApp().getScreen() instanceof WorldScreen)
        {
            WorldScreen ws = (WorldScreen) Main.getApp().getScreen();

            if (App.getCurrentGame().getId().equals(gameID))
            {
                ws.updatePlayerPosition(userID, x, y, direction, isMoving, isInFarm, isInCity, isInGreenHouse, isInHome,
                    isInZeidiesFarm, isInZeidiesHome, isInShop, currentShop);
            }
        }
    }

    private static void handleCLoseLobbyErrorMessage(CloseLobbyErrorMessage message)
    {
        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();
            ls.showText(message.message);
        }
    }

    private static void handleIncomingTradeRequest(IncomingTradeRequestMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();

            int senderId = message.senderId;
            Player senderPlayer = App.getCurrentGame().getPlayerByUserID(senderId);

            if (senderPlayer == null) {
                System.out.println("Sender player not found in game.");
                return;
            }

            TradeWindow tradeWindow = worldScreen.getTradeWindow();
            if (tradeWindow != null) {
                // ✅ Schedule UI update on render thread
                Gdx.app.postRunnable(() -> {
                    tradeWindow.getPopup().show(worldScreen.getUiStage());
                    tradeWindow.showRequestFrom(senderPlayer);
                });
            } else {
                System.out.println("TradeWindow is null.");
            }
        }
    }

    private static void handleIncomingTradeResponse(IncomingTradeResponseMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();
            TradeWindow tw = worldScreen.getTradeWindow();

            int senderId = message.senderId;
            Player senderPlayer = App.getCurrentGame().getPlayerByUserID(senderId);

            // Run on the render thread
            Gdx.app.postRunnable(() -> {
                // This will clear the old request dialog and open the full trade screen
                if (message.accepted) tw.showMainTradeScreen();
                else tw.hide();
                System.out.println("message sent");
            });
        }
    }

    private static void handleMovingItemToTrade(MovingItemToTadeMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();
            TradeWindow tw = worldScreen.getTradeWindow();

            int receiver = message.getReceiverID();
            Player player = App.getCurrentGame().getPlayerByUserID(receiver);

            // Run on the render thread
            Gdx.app.postRunnable(() -> {
                // This will clear the old request dialog and open the full trade screen
                tw.moveOneItemFromInventoryToTrade(player, message.item);
                tw.refreshTradeScreen();
            });
        }
    }

    private static void handleMovingItemToInventory(MovingItemToInventoryMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();
            TradeWindow tw = worldScreen.getTradeWindow();

            int receiver = message.receiverID;
            Player player = App.getCurrentGame().getPlayerByUserID(receiver);

            // Run on the render thread
            Gdx.app.postRunnable(() -> {
                // This will clear the old request dialog and open the full trade screen
                tw.moveOneItemFromTradeToInventory(player, message.object);
                tw.refreshTradeScreen();
            });
        }
    }

    private static void handleTradeConfirm(TradeConfirmMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();
            TradeWindow tw = worldScreen.getTradeWindow();

            // Run on the render thread
            Gdx.app.postRunnable(() -> {
                tw.setFriendConfirmed(true);
            });
        }
    }

    private static void handleTradeCancelled(TradeCancelMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();
            TradeWindow tw = worldScreen.getTradeWindow();

            // Run on the render thread
            Gdx.app.postRunnable(() -> {
                tw.cancelTrade();
            });
        }
    }


    private static void handleUserSyncResponse(UserSyncResponseMessage msg)
    {
        ArrayList<User> serverUsers = new ArrayList<>();
        for (UserDTO dto : msg.userDTOs)
        {
            serverUsers.add(Mapper.fromDTO(dto));
        }

        App.syncUsersWithServer(serverUsers);
        GameClient.getInstance().setUserSyncComplete(true);
    }

    private static void handleUserUpdate(UserUpdateMessage msg)
    {
        User user = Mapper.fromDTO(msg.userDTO);
        App.createOrUpdateUser(user);
    }

    private static void handleGameShutDownMessage(GameShutdownMessage message)
    {
        if (Main.getApp().getScreen() instanceof WorldScreen)
        {
            WorldScreen ws = (WorldScreen) Main.getApp().getScreen();
            ws.handleGameShutdownMessage(message);
        }
    }

    private static void handleLoadGameSuccessMessage(LoadGameSuccessMessage message)
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

    private static void handleLoadGameFailedMessage(LoadGameFailedMessage message)
    {
        Screen currentScreen = Main.getApp().getScreen();
        if (currentScreen instanceof PreLobbyScreen)
        {
            ((PreLobbyScreen) currentScreen).showTextBox(message.reason);
        }
    }

    private static void handleLoadedGameStartMessage(LoadedGameStartedMessage message)
    {
        if (Main.getApp().getScreen() instanceof LobbyScreen)
        {
            LobbyScreen ls = (LobbyScreen) Main.getApp().getScreen();
            ls.createGame(message.gameId, message.gameTime);
        }
    }

    private static void handleOtherPlayersDTOMessage(UpdatePlayerDTOsMessage message)
    {
        if (Main.getApp().getScreen() instanceof WorldScreen)
        {
            WorldScreen ws = (WorldScreen) Main.getApp().getScreen();
            ws.updatePlayersStateCache(message.playerStateCache);
        }
    }

    private static void handleBackPackDTOMessage(BackPackDTOMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen)
        {
            WorldScreen ws = (WorldScreen) Main.getApp().getScreen();
            ws.updateOtherBackPack(message);
        }
    }

    private static void handleJoinActiveGameMessage(JoinActiveGameMessage message)
    {
        if (Main.getApp().getScreen() instanceof PreLobbyScreen)
        {
            PreLobbyScreen ps = (PreLobbyScreen) Main.getApp().getScreen();
            ps.rejoinLoadedGame(message.gameID);
        }
    }

    private static void handleNewChatMessage(NewChatMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();
            Player sender = App.getCurrentGame().getPlayerByUserID(message.senderID);
            Player receiver = App.getCurrentGame().getPlayerByUserID(message.receiverID);

            if (sender == null || receiver == null) {
                System.out.println("Player not found in current game");
                return;
            }

            // Update UI on render thread
            Gdx.app.postRunnable(() -> {
                // Add notification only if chat isn't open
                if (!worldScreen.getCommunicationWindow().getChatScreen().isVisible()) {
                    UIRenderer.showTextBox("New message from " + sender.getNickName() + "!");
                }

                // Add message to local storage
                FriendshipData friendship = receiver.getFriendships().get(sender);
                if (friendship != null) {
                    String stamp = new SimpleDateFormat("HH:mm").format(new Date());
                    String line = "[" + stamp + "] " + sender.getNickName() + ": " + message.message;
                    friendship.getMessageHistory().add(line);

                    // Refresh chat if open
                    if (worldScreen.getCommunicationWindow().getChatScreen().isVisible()) {
                        worldScreen.getCommunicationWindow().getChatScreen().loadChatHistory();
                    }
                }
            });
        }
    }

    private static void handleNewGift(NewGiftMessage message) {
        if (Main.getApp().getScreen() instanceof WorldScreen) {
            WorldScreen worldScreen = (WorldScreen) Main.getApp().getScreen();

            Player sender = App.getCurrentGame().getPlayerByUserID(message.senderID);
            Player receiver = App.getCurrentGame().getPlayerByUserID(message.receiverID);
            if (sender == null || receiver == null) {
                System.out.println("Player not found in current game");
            }
            // Run on the render thread
            Gdx.app.postRunnable(() -> {
                sender.removeAmountFromInventory(message.gift.getObjectType(), message.gift.getNumber());

                Gift newGift = new Gift(message.gift.getObjectType(), sender, receiver, message.gift.getNumber());

                receiver.addToInventory(message.gift);

                receiver.getArchiveGifts().add(newGift);
                sender.getGivenGifts().add(newGift);
                UIRenderer.showTextBox("You received a new gift from " + sender.getNickName() + "!");
            });
        }
    }
}
