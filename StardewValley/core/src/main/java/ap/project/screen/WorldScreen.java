package ap.project.screen;

import ap.project.Main;
import ap.project.control.CharacterController;
import ap.project.control.OnlineWorldController;
import ap.project.control.PreGameController;
import ap.project.control.WorldController;
import ap.project.control.game.activities.TradeController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.enums.Season;
import ap.project.model.building.CraftingItem;
import ap.project.model.enums.*;
import ap.project.model.game.Game;
import ap.project.model.shops.Shop;
import ap.project.model.tools.BackPack;
import ap.project.model.tools.Tool;
import ap.project.network.client.GameClient;
import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.messages.*;
import ap.project.screen.input.WorldScreenInputProcessor;
import ap.project.util.MapAssetLoader;
import ap.project.util.SQLiteUtil;
import ap.project.visual.CharacterRenderer;
import ap.project.model.game.*;
import ap.project.visual.MapVisual;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class WorldScreen implements Screen
{

//    private final com.badlogic.gdx.Game miniGame;

//    private final Stage gameStage;
//    private final Array<AnimalActor> animalActors;
//    private final AnimalInteractionScreen animalInteractionScreen;
//    private float animalMoveTimer = 0f;

    private static WorldScreen INSTANCE;

    private final GameClient client;

    public static final float MAP_SCALE = 1.0f;
    private static final float CHAR_SCALE = 1f;
    private static final float TILE_SIZE = 24f * MAP_SCALE;
    private static final float PLAYER_SPEED = 50f * MAP_SCALE;

    private Game game;

    private final OrthographicCamera cam;
    private final OrthographicCamera uiCam = new OrthographicCamera();

    private Player player;
    private PlayerCharacter character;
    private CharacterController characterController;
    private CharacterRenderer characterRenderer;

    private Map map;
    private final UIRenderer uiRenderer;

    private Point hoveredTile = null;
    private final ShapeRenderer shapeRenderer;

    private final boolean SECOND_PLAYER = false;
    private PlayerCharacter player2;
    private CharacterController controller2;

    private final Time time;
    private Season currentSeason;

    private final Stage uiStage = new Stage();
    private final Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

    private WorldScreenInputProcessor gameInputProcessor;
    private Dialog terminalDialog;
    private TextField userInputField;
    private static StringBuilder dialogTextBuffer = new StringBuilder();
    private static Label dialogContentLabel;
    private InventoryWindow inventoryWindow;
    private TradeWindow tradeWindow;
    private CookBookWindow cookBookWindow;
    private RefrigeratorWindow refrigeratorWindow;
    private CraftingItemWindow craftingItemWindow;
    private FriendsWindow friendsWindow;
    private GreenHouseBuildWindow greenHouseBuildWindow;
    private CommunicationWindow communicationWindow;
    private WorldController worldController;
    private ReactionWindow reactionWindow;
    private TextButton reactButton;
    private InputMultiplexer inputMultiplexer;
    private boolean inputMultiplexerHadSetUp = false;

    private Table hotbarUI = new Table();
    private static final int HOTBAR_SLOTS = 8;
    private static final int HOTBAR_SLOT_SIZE = 48; // Smaller than inventory slots
    private Drawable hotbarSlotBackground;
    private Drawable hotbarSlotHighlight;
    private int selectedHotbarSlot = -1;

    private boolean cameraFixed = false;
    private final boolean DEBUG_MODE = false;

    private float localTimeKeeper = 0f;

    private OnlineWorldController onlineWorldController = new OnlineWorldController();
    public final boolean ONLINE_MODE;

    private float periodicNetworkUpdate = 0;
    private float periodicPlayerDataUpdate = 0;
    private static final float PERIODIC_NETWORK_INTERVAL = 0.016f; // ~ 60 Hz
    private static final float PERIODIC_PLAYER_DATA_INTERVAL = 5.0f; // 0.2 Hz

    private float saveTimer = 0;
    private static final float SAVE_INTERVAL = 30; // 2 minutes

    private java.util.Map<Integer, PlayerDTO> playerStateCache = new ConcurrentHashMap<>();

    public WorldScreen(Player currentPlayer, boolean onlineMode, boolean newGame)
    {
        INSTANCE = this;
        ONLINE_MODE = onlineMode;

        this.shapeRenderer = new ShapeRenderer();
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        this.game = App.getCurrentGame();

        App.setCurrentMenu(Menu.HomeMenu);

        if (newGame)
        {
            for (Player p : game.getPlayers())
            {
                Farm f = new Farm(p.getMapType());
                p.setFarm(f);
                p.setCurrentMap(f);
            }

            for (Player p : game.getPlayers())
            {
                p.spawn();
            }
        } else
        {
            for (Player p : game.getPlayers())
            {
                restorePlayerState(p);
            }

            restorePlayerState(currentPlayer);
            game.setCurrentPlayer(currentPlayer);
        }

        // Set up current player
        this.map = game.getCurrentPlayer().getCurrentMap();
        time = game.getCurrentTime();
        currentSeason = time.getSeason();

        this.characterRenderer = new CharacterRenderer(shapeRenderer);

        ShaderProgram.pedantic = false;
        uiRenderer = new UIRenderer(time);

        // Initialize UI components
        inventoryWindow = new InventoryWindow(uiStage, this);
        friendsWindow = new FriendsWindow(uiStage, this);
        cookBookWindow = new CookBookWindow(uiStage);
        refrigeratorWindow = new RefrigeratorWindow(uiStage);
        craftingItemWindow = new CraftingItemWindow(uiStage);
        communicationWindow = new CommunicationWindow(uiStage, this);
        tradeWindow = friendsWindow.getTradeWindow();
        tradeWindow.setDependencies(inventoryWindow, new TradeController());
        greenHouseBuildWindow = new GreenHouseBuildWindow(uiStage);
        createTerminalDialog();
        worldController = new WorldController();
        worldController.setCommunicationWindow(communicationWindow);
        reactionWindow = new ReactionWindow(uiStage);
        createReactButton();
        inputMultiplexer = new InputMultiplexer();
        checkGameInfo();
        initializeHotbar();

        if (ONLINE_MODE)
        {
            client = GameClient.getInstance();
            client.send(new GameStartedMessage(game.getId(), new PlayerDTO(player)));
            playerStateCache.put(player.getUser().getHashId(), new PlayerDTO(player));
            handleLocalSaveForOnlineGame();
        } else
        {
            client = null;
        }
    }

    private void restorePlayerState(Player player)
    {
        // Restore map state
        if (player.isInFarm())
        {
            player.setCurrentMap(player.getFarm());
            App.setCurrentMenu(Menu.GameMenu);
        } else if (player.isInHome())
        {
            player.setCurrentMap(player.getCabin());
            App.setCurrentMenu(Menu.HomeMenu);
        } else if (player.isInGreenHouse())
        {
            player.setCurrentMap(player.getGreenHouse());
            App.setCurrentMenu(Menu.GameMenu);
        } else if (player.isInCity())
        {
            player.setCurrentMap(game.getCity());
            App.setCurrentMenu(Menu.CityMenu);
        } else if (player.isInShop() && player.getCurrentShop() != null)
        {
            ShopType type = ShopType.getShopType(player.getCurrentShop());
            if (type != null)
            {
                for (java.util.Map.Entry<Point, Shop> entry : game.getCity().getShopDoors().entrySet())
                {
                    if (entry.getValue().getType() == type)
                    {
                        player.setCurrentMap(entry.getValue());
                        App.setCurrentMenu(Menu.CityMenu);
                        break;
                    }
                }
            }
        }
    }

    private void createReactButton()
    {
        reactButton = new TextButton("React", skin);
        reactButton.setSize(200, 70);
        reactButton.setPosition(20, 20); // Bottom left

        reactButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // TODO: change this
//                if (ONLINE_MODE)
//                {
                    reactionWindow.toggleVisibility();
//                }
            }
        });

        uiStage.addActor(reactButton);
    }

    private void initializeHotbar() {
        // Create hotbar slot backgrounds
        hotbarSlotBackground = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(HOTBAR_SLOT_SIZE, HOTBAR_SLOT_SIZE, new Color(0.2f, 0.2f, 0.2f, 0.8f));
        hotbarSlotHighlight = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(HOTBAR_SLOT_SIZE, HOTBAR_SLOT_SIZE, new Color(0.4f, 0.4f, 0.4f, 0.9f));

        // Create hotbar table
        hotbarUI = new Table();
        hotbarUI.defaults().size(HOTBAR_SLOT_SIZE).pad(2);

        // Position hotbar at bottom center of screen
        hotbarUI.setFillParent(false);
        hotbarUI.bottom();

        // Add hotbar to UI stage
        uiStage.addActor(hotbarUI);

        // Build initial hotbar
        refreshHotbarUI();
    }

    public void refreshHotbarUI() {
        hotbarUI.clear();

        if (player == null || player.getCurrentBackPack() == null) {
            return;
        }

        java.util.List<GameObject> hotbarSlots = player.getCurrentBackPack().getHotbarSlots();

        for (int slot = 0; slot < HOTBAR_SLOTS; slot++)
        {
            final int slotIndex = slot;
            GameObject obj = slot < hotbarSlots.size() ? hotbarSlots.get(slot) : null;

            // Create slot container
            Table slotContainer = new Table();
            slotContainer.setBackground(slotIndex == selectedHotbarSlot ? hotbarSlotHighlight : hotbarSlotBackground);
            slotContainer.setSize(HOTBAR_SLOT_SIZE, HOTBAR_SLOT_SIZE);

            if (obj != null) {
                try {
                    // Create item icon
                    Drawable icon = inventoryWindow.getSafeIcon(obj);
                    Stack itemStack = new Stack();
                    Image iconImage = new Image(icon);
                    itemStack.add(iconImage);

                    // Add quantity label if more than 1
                    if (obj.getNumber() > 1) {
                        Label countLabel = new Label(String.valueOf(obj.getNumber()), skin);
                        countLabel.setFontScale(0.7f); // Smaller font for hotbar
                        countLabel.setAlignment(Align.bottomRight);
                        countLabel.setColor(Color.WHITE);

                        // Create container for padding
                        Table countContainer = new Table();
                        //slotContainer.setFillParent(true);
                        itemStack.setFillParent(true);
                        countContainer.bottom().right();
                        countContainer.add(countLabel).pad(0, 0, 2, 2);

                        slotContainer.add(itemStack).expand().fill();
                        slotContainer.add(countContainer);
                    } else {
                        slotContainer.add(itemStack).expand().fill();
                    }

                    if (obj.getNumber() <= 0)
                    {
                        BackPack backpack = player.getCurrentBackPack();

                        GameObjectType type = obj.getObjectType();
                        player.removeAmountFromInventory(type, 1);

                        if (player.getCurrentObject() != null && player.getCurrentObject().getObjectType() == type)
                        {
                            selectedHotbarSlot = -1;
                            player.setCurrentObject(null);
                        }

                        if (player.getCurrentTool() != null && player.getCurrentTool().getObjectType() == type)
                        {
                            selectedHotbarSlot = -1;
                            player.setCurrentTool(null);
                        }

                        for (int i = 0; i < backpack.getHotbarSlots().size(); i++)
                        {
                            GameObject temp = backpack.getHotbarSlots().get(i);
                            if (temp != null && temp.getObjectType() == type)
                            {
                                backpack.removeFromHotbar(i);
                            }
                        }
                    }
                } catch (Exception e)
                {
                    // Handle missing textures gracefully
                    System.err.println("Failed to load icon for: " + obj.getObjectType());
                }
            }

            // Add slot number label
            Label slotNumberLabel = new Label(String.valueOf(slot + 1), skin);
            slotNumberLabel.setFontScale(0.5f);
            slotNumberLabel.setColor(Color.LIGHT_GRAY);
            Table slotNumberContainer = new Table();
            slotContainer.setFillParent(true);
            slotNumberContainer.top().left();
            slotNumberContainer.add(slotNumberLabel).pad(1, 1, 0, 0);

            // Combine slot content and number
            Stack slotStack = new Stack();
            slotStack.add(slotContainer);
            slotStack.add(slotNumberContainer);

            hotbarUI.add(slotStack).size(HOTBAR_SLOT_SIZE, HOTBAR_SLOT_SIZE).pad(1);
        }

        // Position the hotbar at bottom center
        hotbarUI.pack();
        hotbarUI.setPosition(
            (uiStage.getWidth() - hotbarUI.getWidth()) / 2f,
            20f // 20 pixels from bottom
        );
    }

    private Drawable getIconForGameObject(GameObject obj) {
        try {
            String path = obj.getObjectType().getPath();
            Texture texture = new Texture(Gdx.files.internal(path));
            return new TextureRegionDrawable(new TextureRegion(texture));
        } catch (Exception e) {
            // Return a default texture or create a colored rectangle
            String path = GameObjectType.COOKIE.getPath();
            Texture texture = new Texture(Gdx.files.internal(path));
            return new TextureRegionDrawable(new TextureRegion(texture));
        }
    }

    public void checkGameInfo()
    {
        if (player == null || !App.getCurrentGame().getCurrentPlayer().equals(player))
        {
            updateGameInfo();
            refreshHotbarUI(); // Add this line
        }
    }

    public void updateGameInfo() {
        this.player = App.getCurrentGame().getCurrentPlayer();
        this.character = player.getCharacter();
        this.map = player.getCurrentMap();

        updateMap();

        characterController = new CharacterController(character, map, PLAYER_SPEED, TILE_SIZE);

        gameInputProcessor = new WorldScreenInputProcessor(map, character, characterController, cam, this,
            inventoryWindow, communicationWindow);

        if (inputMultiplexer != null && inputMultiplexerHadSetUp) {
            inputMultiplexer.removeProcessor(1);
            inputMultiplexer.addProcessor(1, gameInputProcessor);
        }

        uiRenderer.updatePlayer();
        refreshHotbarUI();
    }

    @Override
    public void show()
    {
        int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
        resize(w, h);

        TextButton friends = uiRenderer.getFriends();
        uiStage.addActor(friends);
        friends.setPosition(
            uiStage.getWidth() - 250f,
            uiStage.getHeight() - 320f
        );
        friends.setSize(220f, 60f);

        // Add it to the stage if not already added
        if (friends.getStage() == null) {
            uiStage.addActor(friends);
        }

        friends.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleFriendsWindow();
            }
        });

        inputMultiplexer.clear();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(gameInputProcessor);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Handle chat input first
                if (inventoryWindow.isVisible() &&
                    keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_8) {
                    inventoryWindow.handleHotbarAssignment(keycode);
                    // Refresh the main screen hotbar after assignment
                    refreshHotbarUI();
                    return true;
                }

                // Handle hotbar selection (when inventory is closed)
                if (!inventoryWindow.isVisible() &&
                    keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_8) {
                    handleHotbarSelection(keycode);
                    return true;
                }

                // Handle chat input first
                if (communicationWindow.getChatScreen().handleKeyDown(keycode)) {
                    return true;
                }

                if (isCraftingWindowVisible()) {
                    return false;
                }

                if (keycode == Input.Keys.E || keycode == Input.Keys.ESCAPE) {
                    boolean nowVisible = !inventoryWindow.isVisible();
                    inventoryWindow.toggleVisibility();

                    if (nowVisible)
                        inventoryWindow.getToolsTab().setChecked(false);

                    return true;
                } else if (keycode == Input.Keys.TAB) {
                    boolean nowVisible = !inventoryWindow.isVisible();
                    inventoryWindow.toggleVisibility();

                    if (nowVisible) {
                        if (inventoryWindow.getLastTabOpenedByTabKey() == InventoryWindow.TabType.TOOLS) {
                            inventoryWindow.getToolsTab().toggle();
                        } else {
                            inventoryWindow.getToolsTab().setChecked(false);
                        }
                    }
                    return true;
                } else if (keycode == Input.Keys.M) {
                    openMapTab();
                    return true;
                }

                return false;
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexerHadSetUp = true;
        centerVisibleWindows();

        if (tradeWindow == null) {
            tradeWindow = new TradeWindow(uiStage, skin);
            tradeWindow.setDependencies(inventoryWindow, tradeWindow.getController());
        }
    }

    private void sendPositionUpdate()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (GameClient.getInstance().isConnected())
        {
            GameClient.getInstance().send(new TestMessage(player.getUser().getUsername() + " " +
                (player.isInFarm() ? "in farm " : "not in farm ") + " at " + player.getLocation().getX() +
                ", " + player.getLocation().getY()));
        }
    }

    private void sendPlayerPresenceMessage()
    {
        String gameID = game.getId();
        int userID = player.getUser().getHashId();

        float x = character.getPosition().x;
        float y = character.getPosition().y;
        byte direction = (byte) character.getDirection().ordinal();
        boolean isMoving = character.isMoving();

        boolean isInFarm = player.isInFarm();
        boolean isInCity = player.isInCity();
        boolean isInGreenHouse = player.isInGreenHouse();
        boolean isInHome = player.isInHome();
        boolean isInZeidiesFarm = player.isInZeidiesFarm();
        boolean isInZeidiesHome = player.isInZeidiesHome();
        boolean isInShop = player.isInShop();

        String currentShop = player.getCurrentShop();

        GamePresenceMessage msg = new GamePresenceMessage(gameID, userID, x, y, direction, isMoving, isInFarm,
            isInCity, isInGreenHouse, isInHome, isInZeidiesFarm, isInZeidiesHome, isInShop, currentShop);
        client.send(msg);
    }

    private void sendPlayerData()
    {
        PlayerDTO playerDTO = new PlayerDTO(player);
        client.send(new PlayerDataMessage(playerDTO, game.getId()));
    }

    public void saveAndExitOnline()
    {
        sendPlayerData();
        handleLocalSaveForOnlineGame();
        client.send(new SaveAndLeaveMessage(game.getId(), player.getUser().getHashId()));
        Main.getApp().setScreen(new PreLobbyScreen());
    }

    public void handleGameShutdownMessage(GameShutdownMessage message)
    {
        if (Main.getApp().getScreen() instanceof WorldScreen)
        {
            WorldScreen ws = (WorldScreen) Main.getApp().getScreen();

            // Check if current player is affected
            int currentPlayerId = ws.player.getUser().getHashId();
            if (message.disconnectedPlayers.contains(currentPlayerId))
            {
                UIRenderer.showTextBox("You were disconnected: " + message.reason);
            } else
            {
                UIRenderer.showTextBox("Game closed: " + message.reason);
            }

            System.out.println("shutting down");
            ws.saveAndExitOnline();
        }
    }

    private void doNetworkStuff(float delta)
    {
        periodicNetworkUpdate += delta;
        periodicPlayerDataUpdate += delta;
        saveTimer += delta;

        client.processMessages();

        updateOtherPlayers(delta);

        if (periodicNetworkUpdate >= PERIODIC_NETWORK_INTERVAL)
        {
            sendPlayerPresenceMessage();
            periodicNetworkUpdate = 0;
        }

        if (periodicPlayerDataUpdate >= PERIODIC_PLAYER_DATA_INTERVAL)
        {
            sendPlayerData();
            periodicPlayerDataUpdate = 0;
        }

        if (saveTimer >= SAVE_INTERVAL)
        {
            handleLocalSaveForOnlineGame();
            saveTimer = 0;
        }
    }

    @Override
    public void render(float dt)
    {
        if (ONLINE_MODE)
        {
            doNetworkStuff(dt);
        } else
        {
            updateLocally(dt);
            handleOfflineSaving(dt);
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!terminalDialog.isVisible() && !isInventoryVisible() && !isCookBookVisible() && !isRefrigeratorVisible()
        && !greenHouseBuildWindow.isVisible())
        {
            update(dt);
            refreshHotbarUI();
            character.updateAnimation(dt);
            cam.update();
        }

        for (Player p : game.getPlayers())
        {
            p.updateReactionTimer(dt);
        }

        UIRenderer.updateTextBoxes(dt);
        map.getMapVisual().render(cam);

        Batch batch = map.getMapVisual().getRenderer().getBatch();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);
        batch.begin();

        Vector3 mouseWorldPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouseWorldPos);
        float worldMouseX = mouseWorldPos.x;
        float worldMouseY = mouseWorldPos.y;

        renderCharacters(batch);

        if (!isDialogVisible() && !isInventoryVisible() && !isCookBookVisible() && !isRefrigeratorVisible()
            && !greenHouseBuildWindow.isVisible())
        {
            characterRenderer.renderToolOrObjectAtMouse(batch, character, worldMouseX, worldMouseY);
        }

        batch.end();

//        gameStage.getViewport().apply();
//        gameStage.draw();

        map.getMapVisual().renderInFrontOfCharacter(this);

        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        uiRenderer.renderUI(batch, uiCam);
        uiRenderer.renderWeatherOverlay(batch, uiCam);
        batch.end();

        uiRenderer.renderDarkOverlay(uiCam);

        map.getMapVisual().showAvailableTilesForArtisanEquipment(this);
        map.getMapVisual().drawCraftingProgressBars();

        if (hoveredTile != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 0f, 0f, 0.3f);

            Tile tile = map.getTile(hoveredTile.getX(), hoveredTile.getY() - 1);
            Vector2 location = map.tileToWorld(tile);

            if (location != null && DEBUG_MODE)
            {
                shapeRenderer.rect(location.x, location.y - (16f * MAP_SCALE), (16f * MAP_SCALE), (16f * MAP_SCALE));
            }

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        uiStage.act(dt);
        uiStage.draw();

        map.getMapVisual().renderLightningEffect();
    }

    private void update(float dt)
    {
        characterController.update(dt);

        updateSeason();
        checkGameInfo();
        checkMapSeason();
        map.getMapVisual().update(dt);

        if (map.getMapType() == MapTypes.GREEN_HOUSE || map.getMapType() == MapTypes.CARPENTER_SHOP ||
            map.getMapType() == MapTypes.MARNIE_RANCH) {
            cameraFixed = false;
        }

        if (!cameraFixed) {
            float playerX = character.getPosition().x + TILE_SIZE / 2;
            float playerY = character.getPosition().y + TILE_SIZE / 2;

            float camX = playerX;
            float camY = playerY;

            float halfViewportW = cam.viewportWidth / 2;
            float halfViewportH = cam.viewportHeight / 2;

            float rightEdgeSize = (map.getWidth() * MAP_SCALE * 16) - (cam.viewportWidth / 2);
            float upEdgeSize = (map.getHeight() * MAP_SCALE * 16) - (cam.viewportHeight / 2);

            if (playerX < halfViewportW)
            {
                camX = halfViewportW;
            } else if (playerX >= rightEdgeSize)
            {
                camX = rightEdgeSize;
            }

            if (playerY <  halfViewportH)
            {
                camY = halfViewportH;
            } else if (playerY >= upEdgeSize)
            {
                camY = upEdgeSize;
            }

            if (map.getMapType() == MapTypes.JOJA_MART)
            {
                camX = (map.getWidth() * MAP_SCALE * 16) / 2;
            }

            if (map.getMapType() == MapTypes.GREEN_HOUSE)
            {
                camX = (map.getWidth() * MAP_SCALE * 16) / 2;
            }

            if (map.getMapType() == MapTypes.CARPENTER_SHOP)
            {
                camX = (map.getWidth() * MAP_SCALE * 16) / 2;
            }

            if (map.getMapType() == MapTypes.MARNIE_RANCH)
            {
                camX = (map.getWidth() * MAP_SCALE * 16) / 2;
                camY = (map.getHeight() * MAP_SCALE * 16) / 2;
            }

            cam.position.set(camX, camY, 0);
        }
    }

    @Override
    public void resize(int w, int h) {
        float targetRatio = 4f / 3f;
        float currentRatio = (float) w / h;

        if (currentRatio > targetRatio) {
            cam.viewportHeight = 15 * TILE_SIZE;
            cam.viewportWidth  = cam.viewportHeight * currentRatio;
        } else {
            cam.viewportWidth  = 20 * TILE_SIZE;
            cam.viewportHeight = cam.viewportWidth / currentRatio;
        }

        uiCam.setToOrtho(false, w, h);
        uiCam.update();
        uiStage.getViewport().update(w, h, true);

        // Reposition hotbar after resize
        if (hotbarUI != null) {
            refreshHotbarUI();  // Ensure hotbar contents are updated
            hotbarUI.setPosition(
                (uiStage.getWidth() - hotbarUI.getWidth()) / 2f,
                20f
            );
        }

        // Center all visible windows after resize
        centerVisibleWindows();

        reactButton.setPosition(20, 20);
    }

    // Helper method to center all visible windows
    private void centerVisibleWindows() {
        if (terminalDialog != null && terminalDialog.isVisible()) {
            terminalDialog.setPosition(
                (uiStage.getWidth() - terminalDialog.getWidth()) / 2,
                (uiStage.getHeight() - terminalDialog.getHeight()) / 2
            );
        }
        if (inventoryWindow != null && inventoryWindow.isVisible()) {
            inventoryWindow.getPopup().setPosition(
                (uiStage.getWidth() - inventoryWindow.getPopup().getWidth()) / 2,
                (uiStage.getHeight() - inventoryWindow.getPopup().getHeight()) / 2
            );
        }
        if (cookBookWindow != null && cookBookWindow.isVisible()) {
            cookBookWindow.getWindow().setPosition(
                (uiStage.getWidth() - cookBookWindow.getWindow().getWidth()) / 2,
                (uiStage.getHeight() - cookBookWindow.getWindow().getHeight()) / 2
            );
        }
        if (refrigeratorWindow != null && refrigeratorWindow.isVisible()) {
            refrigeratorWindow.getWindow().setPosition(
                (uiStage.getWidth() - refrigeratorWindow.getWindow().getWidth()) / 2,
                (uiStage.getHeight() - refrigeratorWindow.getWindow().getHeight()) / 2
            );
        }
        if (craftingItemWindow != null && craftingItemWindow.isVisible()) {
            craftingItemWindow.getWindow().setPosition(
                (uiStage.getWidth() - craftingItemWindow.getWindow().getWidth()) / 2,
                (uiStage.getHeight() - craftingItemWindow.getWindow().getHeight()) / 2
            );
        }
        if (friendsWindow != null && friendsWindow.isVisible()) {
            friendsWindow.getPopup().setPosition(
                (uiStage.getWidth() - friendsWindow.getPopup().getWidth()) / 2,
                (uiStage.getHeight() - friendsWindow.getPopup().getHeight()) / 2
            );
        }
        if (greenHouseBuildWindow != null && greenHouseBuildWindow.isVisible()) {
            greenHouseBuildWindow.getWindow().setPosition(
                (uiStage.getWidth() - greenHouseBuildWindow.getWindow().getWidth()) / 2,
                (uiStage.getHeight() - greenHouseBuildWindow.getWindow().getHeight()) / 2
            );
        }
        if (communicationWindow != null && communicationWindow.isVisible()) {
            communicationWindow.getPopup().setPosition(
                (uiStage.getWidth() - communicationWindow.getPopup().getWidth()) / 2,
                (uiStage.getHeight() - communicationWindow.getPopup().getHeight()) / 2
            );
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.getMapVisual().dispose();
        uiRenderer.dispose();
        shapeRenderer.dispose();
        cookBookWindow.dispose();
        refrigeratorWindow.dispose();
//        gameStage.dispose();
        communicationWindow.dispose();
        reactionWindow.dispose();
    }

    private void handleHotbarSelection(int keycode) {
        if (player == null || player.getCurrentBackPack() == null) {
            return;
        }

        int hotbarSlot = -1;

        // Map keycodes to hotbar slots (1-8 keys map to slots 0-7)
        switch (keycode) {
            case Input.Keys.NUM_1: hotbarSlot = 0; break;
            case Input.Keys.NUM_2: hotbarSlot = 1; break;
            case Input.Keys.NUM_3: hotbarSlot = 2; break;
            case Input.Keys.NUM_4: hotbarSlot = 3; break;
            case Input.Keys.NUM_5: hotbarSlot = 4; break;
            case Input.Keys.NUM_6: hotbarSlot = 5; break;
            case Input.Keys.NUM_7: hotbarSlot = 6; break;
            case Input.Keys.NUM_8: hotbarSlot = 7; break;
            default: return;
        }

        java.util.List<GameObject> hotbarSlots = player.getCurrentBackPack().getHotbarSlots();

        if (hotbarSlot < hotbarSlots.size()) {
            GameObject selectedItem = hotbarSlots.get(hotbarSlot);

            // Toggle selection - if same slot is pressed again, deselect
            if (selectedHotbarSlot == hotbarSlot) {
                selectedHotbarSlot = -1;
                player.setCurrentObject(null);
            } else if (selectedItem != null)
            {
                selectedHotbarSlot = hotbarSlot;

                if (selectedItem instanceof Tool)
                {
                    player.setCurrentTool((Tool) selectedItem);
                } else
                {
                    player.setCurrentObject(selectedItem);
                }
            }

            // Clear inventory window selection to avoid conflicts
            inventoryWindow.clearSelection();

            // Refresh the hotbar display
            refreshHotbarUI();
        }
    }



    private void checkMapSeason()
    {
        if (time.getSeason() != currentSeason)
        {
            updateMap();
        }
        currentSeason = time.getSeason();
    }

    private void updateMap()
    {
        MapAssetLoader.LoadedMap loaded = MapAssetLoader.loadFromTmx(map.getMapType().getName(), time.getSeason(), map.getMapType().getMapKind());
        TiledMap tiledMap = loaded.tiledMap;
        map.setVisual(new MapVisual(map, tiledMap));

        float mapPixelWidth = map.getWidth() * TILE_SIZE;
        float mapPixelHeight = map.getHeight() * TILE_SIZE;

        if (mapPixelWidth <= cam.viewportWidth || mapPixelHeight <= cam.viewportHeight)
        {
            setCameraFixed(true);

            float centerX = (map.getWidth() * MAP_SCALE * 16) / 2;
            float centerY = (map.getHeight() * MAP_SCALE * 16) / 2;

            cam.position.set(centerX, centerY, 0);
            cam.update();

            map.getMapVisual().getRenderer().setView(cam);
        } else
        {
            setCameraFixed(false);
        }
    }

    public Tile cursorToTile()
    {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY() + (60); // Hard-Coded

        hoveredTile = map.screenToTile(mouseX, mouseY, cam);
        Tile tile = map.getTile(hoveredTile.getX(), hoveredTile.getY() - 1);

        return tile;
    }

    private void createTerminalDialog() {
        terminalDialog = new Dialog("Game Console", skin) {
            @Override
            protected void result(Object object)
            {
                if (object != null && object.equals("CLOSE"))
                {
                    closeDialog();
                }
            }
        };

        dialogContentLabel = new Label("", skin);
        dialogContentLabel.setWrap(true);

        ScrollPane scrollPane = new ScrollPane(dialogContentLabel, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setFlickScroll(false);
        scrollPane.setOverscroll(false, false);

        userInputField = new TextField("", skin);
        userInputField.setMessageText("Enter command here...");

        Table contentTable = terminalDialog.getContentTable();
        contentTable.add(scrollPane).grow().width(1000).height(350).pad(10);
        contentTable.row();
        contentTable.add(userInputField).growX().width(1000).pad(10);

        Table buttonTable = new Table();
        buttonTable.defaults().pad(3).minWidth(40);

        TextButton submitButton = new TextButton("Submit", skin);
        TextButton closeButton = new TextButton("Close", skin);

        Label submitLabel = (Label) submitButton.getLabel();
        submitLabel.setFontScale(0.4f);

        Label closeLabel = (Label) closeButton.getLabel();
        closeLabel.setFontScale(0.4f);

        submitButton.pad(3, 8, 3, 8);
        closeButton.pad(3, 8, 3, 8);

        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                processUserInput();
                userInputField.setText("");
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeDialog();
            }
        });

        buttonTable.add(submitButton).padRight(10);
        buttonTable.add(closeButton);

        contentTable.row();
        contentTable.add(buttonTable).padTop(10).right();

        userInputField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    processUserInput();
                    userInputField.setText("");
                    return true;
                }
                return false;
            }
        });

        terminalDialog.key(Input.Keys.ESCAPE, "CLOSE");

        terminalDialog.setModal(true);
        terminalDialog.setMovable(true);
        terminalDialog.setResizable(true);
        terminalDialog.setSize(700, 500);
        terminalDialog.setVisible(false);
    }

    private void processUserInput() {
        String input = userInputField.getText().trim();

        if (!input.isEmpty()) {
            appendToDialog("> " + input);
            App.getCurrentMenu().check(input);
        }
    }

    public void toggleTerminalDialog() {
        if (terminalDialog.isVisible()) return;

        userInputField.setText("");
        terminalDialog.show(uiStage);
        terminalDialog.setVisible(true);

        terminalDialog.setPosition(
            (uiStage.getWidth() - terminalDialog.getWidth()) / 2,
            (uiStage.getHeight() - terminalDialog.getHeight()) / 2
        );

        updateDialogDisplay();
        uiStage.setKeyboardFocus(userInputField);
        userInputField.setCursorPosition(0);
        Gdx.input.setInputProcessor(uiStage);
    }

    public static void appendToDialog(String text)
    {
        dialogTextBuffer.append(text).append("\n");
        updateDialogDisplay();
    }

    private static void updateDialogDisplay() {
        if (dialogContentLabel != null) {
            dialogContentLabel.setText(dialogTextBuffer.toString());

            ScrollPane scrollPane = (ScrollPane) dialogContentLabel.getParent();
            if (scrollPane != null) {
                scrollPane.setScrollY(Float.MAX_VALUE);
            }
        }
    }

    public void closeDialog() {
        terminalDialog.hide();
        terminalDialog.setVisible(false);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(gameInputProcessor);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public boolean isDialogVisible() {
        return terminalDialog.isVisible();
    }

    public boolean isInventoryVisible() {
        return inventoryWindow.isVisible();
    }

    public void toggleInventoryWindow()
    {
        inventoryWindow.toggleVisibility();
    }

//    private void updateAnimalMovement(float dt) {
//        animalMoveTimer += dt;
//        if (animalMoveTimer > 3f) {
//            animalMoveTimer = 0;
//            for (AnimalActor actor : animalActors) {
//                Animal animal = actor.getAnimal();
//                if (!animal.isIn() && animal.getCurrentState() == Animal.State.IDLE && MathUtils.randomBoolean(0.5f)) {
//                    float moveDist = 50f;
//                    float targetX = actor.getX() + MathUtils.random(-moveDist, moveDist);
//                    float targetY = actor.getY() + MathUtils.random(-moveDist, moveDist);
//
//                    targetX = MathUtils.clamp(targetX, 0, map.getWidth() * TILE_SIZE - actor.getWidth());
//                    targetY = MathUtils.clamp(targetY, 0, map.getHeight() * TILE_SIZE - actor.getHeight());
//
//                    actor.moveTo(targetX, targetY);
//                }
//            }
//        }
//    }

    public void toggleFriendsWindow() {
        friendsWindow.toggleVisibility();
    }

    public static WorldScreen getInstance()
    {
        return INSTANCE;
    }

    public void setCameraFixed(boolean cameraFixed)
    {
        this.cameraFixed = cameraFixed;

        if (cameraFixed)
        {
            cam.position.set(character.getPosition().x + TILE_SIZE/2, character.getPosition().y + TILE_SIZE/2, 0);
            cam.update();
        }
    }

    private void updateSeason()
    {
        // Implement season update logic if needed
    }

    public Player getCurrentPlayer() {
        return this.player;
    }
    public void toggleCookBookWindow() {
        cookBookWindow.toggleVisibility();
    }

    public boolean isCookBookVisible() {
        return cookBookWindow.isVisible();
    }

    public void restoreInputFocus() {
        // Restore input focus to the world screen
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public boolean isChatVisible() {
        return communicationWindow.getChatScreen().isVisible();
    }
    public void toggleRefrigeratorWindow() {
        refrigeratorWindow.toggleVisibility();
    }

    public boolean isRefrigeratorVisible()
    {
        return refrigeratorWindow.isVisible();
    }

    public void showSelectionOverTile(Tile tile)
    {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // ← add this

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 1f, 0.3f);

        Vector2 location = map.tileToWorld(tile);

        shapeRenderer.rect(location.x, location.y - (16f * MAP_SCALE), (16f * MAP_SCALE), (16f * MAP_SCALE));

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawProgressBar(Vector2 location, float progress, boolean isTall)
    {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float barWidth = 14 * MAP_SCALE;
        float barHeight = 3 * MAP_SCALE;
        float barX = location.x + 1 * MAP_SCALE;
        float barY;

        // Position based on item height
        if (isTall)
        {
            barY = location.y - (16 * MAP_SCALE) + (30 * MAP_SCALE);
        } else
        {
            barY = location.y - (16 * MAP_SCALE) + (13 * MAP_SCALE);
        }

        // Draw background (dark gray)
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Draw progress (bright green)
        shapeRenderer.setColor(0, 1, 0, 1);
        float progressWidth = barWidth * progress;
        shapeRenderer.rect(barX, barY, progressWidth, barHeight);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void toggleCraftingWindow(CraftingItem item)
    {
        craftingItemWindow.setItem(item);
        craftingItemWindow.toggleVisibility();
    }

    public boolean isCraftingWindowVisible()
    {
        return craftingItemWindow.isVisible();
    }

    public OrthographicCamera getCamera()
    {
        return cam;
    }

    public void openMapTab()
    {
        inventoryWindow.toggleVisibility();
        inventoryWindow.getMapTab().setChecked(true);
    }

    public boolean isGreenHouseBuildWindowVisible()
    {
        return greenHouseBuildWindow != null && greenHouseBuildWindow.isVisible();
    }

    public void toggleGreenHouseBuildWindow()
    {
        if (greenHouseBuildWindow != null)
        {
            greenHouseBuildWindow.toggleVisibility();
        }
    }

    private void updateLocally(float dt)
    {
        localTimeKeeper += dt;

        if (localTimeKeeper >= 1f)
        {
            game.getCurrentTime().updateMinute(1);
            localTimeKeeper = 0;

            // Auto-save every game day
            if (game.getCurrentTime().getMinute() == 0 && game.getCurrentTime().getHour() == 9)
            {
                saveOfflineGameState();
            }
        }

        if (player.getEnergy() <= 0)
        {
            game.nextTurn();
            saveOfflineGameState();
        }
    }

    private void handleOfflineSaving(float delta)
    {
        saveTimer += delta;

        // Periodic auto-save
        if (saveTimer >= SAVE_INTERVAL)
        {
            saveOfflineGameState();
            saveTimer = 0;
        }
    }

    public void saveAndExitOffline()
    {
        saveOfflineGameState();
        Main.getApp().setScreen(new OfflinePreGameScreen(new PreGameController()));
        App.setCurrentGame(null);
    }

    private void saveOfflineGameState()
    {
        try
        {
            // Save game time
            SQLiteUtil.saveGameTime(game.getId(), game.getCurrentTime());

            // Save all players
            for (Player player : game.getPlayers())
            {
                PlayerDTO dto = new PlayerDTO(player);
                SQLiteUtil.savePlayerState(
                    game.getId(),
                    String.valueOf(player.getUser().getHashId()),
                    dto
                );
            }

            System.out.println("Offline game saved successfully");
        } catch (Exception e)
        {
            System.err.println("Error saving offline game: " + e.getMessage());
        }
    }

    private void renderCharacters(Batch batch)
    {
        characterRenderer.render(batch, character, CHAR_SCALE);

        if (player.isInCity())
        {
            for (Player p : game.getPlayers())
            {
                if (p.getUser().getUsername().equals(player.getUser().getUsername())) continue;

                PlayerCharacter pc = p.getCharacter();
                if (System.currentTimeMillis() - pc.getLastUpdateTime() < 2000)
                {
                    characterRenderer.render(batch, pc, CHAR_SCALE);
                }
            }
        }

        if (player.isInShop())
        {
            Shop shop = (Shop) player.getCurrentMap();
            ShopType shopType = shop.getType();

            for (Player p : game.getPlayers())
            {
                if (p.getUser().getUsername().equals(player.getUser().getUsername())) continue;

                if (p.isInShop())
                {
                    ShopType type = ShopType.getShopType(p.getCurrentShop());
                    if (type != null && type == shopType)
                    {
                        PlayerCharacter pc = p.getCharacter();
                        if (System.currentTimeMillis() - pc.getLastUpdateTime() < 2000)
                        {
                            characterRenderer.render(batch, pc, CHAR_SCALE);
                        }
                    }
                }
            }
        }
    }

    private void updateOtherPlayers(float delta)
    {
        long currentTime = System.currentTimeMillis();

        for (Player p : game.getPlayers())
        {
            if (p.getUser().getHashId() != player.getUser().getHashId())
            {
                PlayerCharacter pc = p.getCharacter();
                if (currentTime - pc.getLastUpdateTime() > 50) continue;

                if (pc.isMoving())
                {
                    pc.updateAnimation(delta);
                } else
                {
                    pc.resetAnimation();
                }
            }
        }
    }

    public void updatePlayerPosition(int userID, float x, float y, byte direction, boolean isMoving, boolean isInFarm,
                                     boolean isInCity, boolean isInGreenHouse, boolean isInHome, boolean isInZeidiesFarm,
                                     boolean isInZeidiesHome, boolean isInShop, String currentShop)
    {
        for (Player p : game.getPlayers())
        {
            if (p.getUser().getHashId() != player.getUser().getHashId())
            {
                if (p.getUser().getHashId() == userID)
                {
                    PlayerCharacter pc = p.getCharacter();

                    pc.setPosition(new Vector2(x, y));
                    pc.setDirection(AbstractCharacter.Direction.values()[direction]);
                    pc.setMoving(isMoving);

                    p.setInFarm(isInFarm);
                    p.setInCity(isInCity);
                    p.setInGreenHouse(isInGreenHouse);
                    p.setInHome(isInHome);
                    p.setInZeidiesFarm(isInZeidiesFarm);
                    p.setInZeidiesHome(isInZeidiesHome);
                    p.setInShop(isInShop);

                    if (isInShop && !currentShop.equals("null"))
                    {
                        City city = App.getCurrentGame().getCity();

                        ShopType shopType = ShopType.getShopType(currentShop);

                        for (java.util.Map.Entry<Point, Shop> entry : city.getShopDoors().entrySet())
                        {
                            Shop shop = entry.getValue();

                            if (shopType != null && shop.getType() == shopType)
                            {
                                p.goToShop(shop);
                            }
                        }
                    }

                    pc.setLastUpdateTime(System.currentTimeMillis());
                }
            }
        }
    }

    public void updateTimeMinute()
    {
        game.getCurrentTime().updateMinute(1);
    }

    public void syncTime(int minute, int hour, int day, int totalHours, int totalDays, String currentWeather, String tomorrowWeather)
    {
        Time time = game.getCurrentTime();

        int localMinute = time.getMinute();
        int localHour = time.getHour();
        int localDay = time.getDay();
        int localTotalHours = time.getTotalHoursPassed();
        int localTotalDays = time.getTotalDaysPassed();

        Weather localCurrent = time.getCurrentWeather();
        Weather localTomorrow = time.getTomorrowWeather();

        Weather globalCurrent = Weather.getWeather(currentWeather);
        Weather globalTomorrow = Weather.getWeather(tomorrowWeather);

        if (globalCurrent != null && localCurrent != globalCurrent)
        {
            time.setCurrentWeather(globalCurrent);
        }

        if (globalTomorrow != null && localTomorrow != globalTomorrow)
        {
            time.setTomorrowWeather(globalTomorrow);
        }

        if ((Math.abs(localMinute - minute) > 5) || localHour != hour || localDay != day ||
            localTotalHours != totalHours || localTotalDays != totalDays)
        {
            time.setMinute(minute);
            time.setHour(hour);
            time.setDay(day);
            time.setTotalHoursPassed(totalHours);
            time.setTotalDaysPassed(totalDays);
        }
    }

    private void handleLocalSaveForOnlineGame()
    {
        SQLiteUtil.saveGameTime(game.getId(), game.getCurrentTime());

        for (java.util.Map.Entry<Integer, PlayerDTO> entry : playerStateCache.entrySet())
        {
            try
            {
                SQLiteUtil.savePlayerState(
                    game.getId(),
                    String.valueOf(entry.getKey()),
                    entry.getValue()
                );
            } catch (Exception e)
            {
                System.err.println("Error saving player state: " + e.getMessage());
            }
        }

        try
        {
            SQLiteUtil.savePlayerState(game.getId(), String.valueOf(player.getUser().getHashId()), new PlayerDTO(player));
        } catch (Exception e)
        {
            System.err.println("Error saving player state: " + e.getMessage());
        }
    }

    public void updatePlayersStateCache(java.util.Map<Integer, PlayerDTO> playerStateCache)
    {
        this.playerStateCache.putAll(playerStateCache);
    }

    public TradeWindow getTradeWindow() {
        if (tradeWindow == null) {
            tradeWindow = new TradeWindow(uiStage, skin);
            tradeWindow.setDependencies(inventoryWindow, new TradeController());
        }
        return tradeWindow;
    }

    public void setTradeWindow(TradeWindow tradeWindow) {
        this.tradeWindow = tradeWindow;
    }

    public InventoryWindow getInventoryWindow() {
        return inventoryWindow;
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public void updateOtherPlayerReaction(PlayerReactionMessage message)
    {
        for (Player p : game.getPlayers())
        {
            if ((p.getUser().getHashId() == message.playerId) &&
                (p.getUser().getHashId() != player.getUser().getHashId()))
            {
                ReactionEmoji emoji = message.currentEmoji;
                String text = message.currentReactionText;
                if (emoji != null)
                {
                    p.setReaction(emoji);
                } else if (!text.isEmpty())
                {
                    p.setReaction(text);
                }
            }
        }
    }

    public void sendReactionMessage(ReactionEmoji reactionEmoji)
    {
        if (ONLINE_MODE)
        {
            client.send(new PlayerReactionMessage(game.getId(), player.getUser().getHashId(), reactionEmoji));
        }
    }

    public void sendReactionMessage(String text)
    {
        if (ONLINE_MODE)
        {
            client.send(new PlayerReactionMessage(game.getId(), player.getUser().getHashId(), text));
        }
    }
}
