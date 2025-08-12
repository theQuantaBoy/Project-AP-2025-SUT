package ap.project.screen;

import ap.project.control.CharacterController;
import ap.project.control.WorldController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.animal.Animal;
import ap.project.model.enums.Gender;
import ap.project.model.enums.Season;
import ap.project.model.enums.animal_enums.FarmAnimalsType;
import ap.project.model.building.CraftingItem;
import ap.project.model.enums.*;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.enums.animal_enums.FishType;
import ap.project.model.enums.tool_enums.FishingPoleLevel;
import ap.project.model.game.Game;
import ap.project.model.tools.BackPack;
import ap.project.model.tools.Tool;
import ap.project.model.tools.BackPack;
import ap.project.screen.input.WorldScreenInputProcessor;
import ap.project.util.MapAssetLoader;
import ap.project.view.GameMenu;
import ap.project.visual.*;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class WorldScreen implements Screen
{

    private FishingMinigameManager fishingMinigame;

    private static WorldScreen INSTANCE;

    public static final float MAP_SCALE = 1.0f;
    private static final float CHAR_SCALE = 1f;
    static final float TILE_SIZE = 24f * MAP_SCALE;
    private static final float PLAYER_SPEED = 50f * MAP_SCALE;

    private final Game game;

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
    private CookBookWindow cookBookWindow;
    private RefrigeratorWindow refrigeratorWindow;
    private CraftingItemWindow craftingItemWindow;
    private FriendsWindow friendsWindow;
    private GreenHouseBuildWindow greenHouseBuildWindow;
    private CommunicationWindow communicationWindow;
    private WorldController worldController;
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

    private ShopWindow shopWindow;

//    private NPCManager npcManager;

    public WorldScreen() {
        INSTANCE = this;

        this.shapeRenderer = new ShapeRenderer();
//        this.miniGame = new FishingGame();
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        this.game = new Game(new ArrayList<>(List.of(
            new Player(new User("mohsen","","mohsen","", Gender.MALE, "", ""), MapTypes.MINING, 0),
            new Player(new User("arash","","arash","", Gender.FEMALE, "", ""), MapTypes.FISHING, 0),
            new Player(new User("moshtagh","","moshtagh","", Gender.FEMALE, "", ""), MapTypes.FORAGING, 0),
            new Player(new User("ottie","","ottie","", Gender.FEMALE, "", ""), MapTypes.COMBAT, 0)
        )));

        App.setCurrentGame(game);
        App.setCurrentMenu(Menu.GameMenu);
        App.setCurrentUser(game.getPlayers().get(0).getUser());
        game.setCurrentPlayer(game.getPlayers().get(0));

//        gameStage = new Stage(new ExtendViewport(20 * TILE_SIZE, 15 * TILE_SIZE, cam));
//        animalActors = new Array<>();
//        animalInteractionScreen = new AnimalInteractionScreen(uiStage.getViewport(), skin);
//
//        Animal testChicken = new Animal("Clucky", FarmAnimalsType.CHICKEN);
//        testChicken.setX(20 * TILE_SIZE);
//        testChicken.setY(15 * TILE_SIZE);
//        testChicken.goOut(); // Make sure it's set to be outside
//        AnimalActor chickenActor = new AnimalActor(testChicken, animalInteractionScreen);
//        animalActors.add(chickenActor);
//        gameStage.addActor(chickenActor);

        for (Player p : game.getPlayers()) {
            Farm f = new Farm(p.getMapType());
            p.setFarm(f);
            p.setCurrentMap(f);
        }

        for (Player p : game.getPlayers()) {
            p.spawn();
        }

        this.map = game.getCurrentPlayer().getCurrentMap();
        this.shopWindow = new ShopWindow(uiStage, null);
        time = game.getCurrentTime();
        currentSeason = time.getSeason();

        this.characterRenderer = new CharacterRenderer(shapeRenderer);

        if (SECOND_PLAYER) {
            Vector2 spawn2 = new Vector2(62 * TILE_SIZE, 60 * TILE_SIZE);
            Player player2p = new Player(new User("arash", "", "arash", "", Gender.FEMALE, "", ""), MapTypes.FISHING, 0);
            player2 = new PlayerCharacter(CharacterType.ABIGAIL, spawn2, "Player 456", player2p);
            controller2 = new CharacterController(player2, map, PLAYER_SPEED, TILE_SIZE);
            controller2.chnageMoveKeys(Input.Keys.UP, Input.Keys.LEFT, Input.Keys.DOWN, Input.Keys.RIGHT);
        }

        ShaderProgram.pedantic = false;
        uiRenderer = new UIRenderer(time);

        inventoryWindow = new InventoryWindow(uiStage, this);
        friendsWindow = new FriendsWindow(uiStage, this);
        cookBookWindow = new CookBookWindow(uiStage);
        refrigeratorWindow = new RefrigeratorWindow(uiStage);
        craftingItemWindow = new CraftingItemWindow(uiStage);
        communicationWindow = new CommunicationWindow(uiStage, this);
        greenHouseBuildWindow = new GreenHouseBuildWindow(uiStage);
        createTerminalDialog();
        worldController = new WorldController();
        worldController.setCommunicationWindow(communicationWindow);
        inputMultiplexer = new InputMultiplexer();
        checkGameInfo();
        initializeHotbar();
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

    public void checkGameInfo() {
        if (player == null || !App.getCurrentGame().getCurrentPlayer().equals(player)) {
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
            inventoryWindow, communicationWindow, shopWindow);

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
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        boolean one = !terminalDialog.isVisible();
        boolean two = !isInventoryVisible();
        boolean three = !isCookBookVisible();
        boolean four = !isRefrigeratorVisible();
        boolean five = !isShopWindowVisible();
        boolean six = !isPurchaseWindowVisible();
        boolean seven = !greenHouseBuildWindow.isVisible();

        if (one && two && three && four && five && six && seven)
        {
            update(dt);
            refreshHotbarUI();
            character.updateAnimation(dt);
            cam.update();
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

        if (map.getMapType().getMapKind() == MapKind.TOWN)
        {
            for (Player p : game.getPlayers())
            {
                if (p.isInCity())
                {
                    characterRenderer.render(batch, p.getCharacter(), CHAR_SCALE);
                }
            }
        } else
        {
            characterRenderer.render(batch, character, CHAR_SCALE);
        }

        if (SECOND_PLAYER) characterRenderer.render(batch, player2, CHAR_SCALE);

        if (!isDialogVisible() && !isInventoryVisible() && !isCookBookVisible() && !isRefrigeratorVisible()
            && !greenHouseBuildWindow.isVisible() && !isShopWindowVisible() && !isPurchaseWindowVisible())
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

//        if (fishingWindow != null)
//        {
//            fishingWindow.update(Gdx.graphics.getDeltaTime());
//        }
//        animalInteractionScreen.render();
    }

    private void update(float dt) {
        characterController.update(dt);
        if (SECOND_PLAYER) controller2.update(dt);

//        gameStage.act(dt);
//        updateAnimalMovement(dt);
        updateSeason();
        checkGameInfo();
        checkMapSeason();
        map.getMapVisual().update(dt);

        if (map.getMapType() == MapTypes.GREEN_HOUSE || map.getMapType() == MapTypes.CARPENTER_SHOP ||
            map.getMapType() == MapTypes.MARNIE_RANCH) {
            cameraFixed = false;
        }

//        Vector2 targetPos = new Vector2(
//            player.getPosition().x + TILE_SIZE / 2,
//            player.getPosition().y + TILE_SIZE / 2
//        );
//
//        cam.position.lerp(new Vector3(targetPos.x, targetPos.y, 0), 5f * dt);

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

//        if (Gdx.input.isKeyJustPressed(Input.Keys.F))
//        {
//            if (fishingWindow == null)
//            {
//                fishingWindow = new FishingMinigameWindow(skin);
//                uiStage.clear();
//                uiStage.addActor(fishingWindow);
//            } else
//            {
//                fishingWindow.remove();
//                fishingWindow = null;
//            }
//        }
    }

    @Override
    public void resize(int w, int h) {
        float targetRatio = 4f / 3f;
        float currentRatio = (float) w / h;

        if (currentRatio > targetRatio) {
            cam.viewportHeight = 15 * TILE_SIZE;
            cam.viewportWidth  = cam.viewportHeight * currentRatio;
        } else {                                      // too tall → letterbox
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

    public Map getMap() {
        return map;
    }

    public ShopWindow getShopWindow() {
        return shopWindow;
    }

    public boolean isShopWindowVisible() {
        return shopWindow.isVisible();
    }

    public boolean isPurchaseWindowVisible() {
        return shopWindow.isPurchaseWindowVisible();
    }

    public Season getCurrentSeason() {
        return time.getSeason();
    }

    public TimeOfDay getCurrentTimeOfDay() {
        return time.getTimeOfDay(); // Implement this in Time class
    }

    public CommunicationWindow getCommunicationWindow() {
        return communicationWindow;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public PlayerCharacter getCharacter() {
        return character;
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
}
