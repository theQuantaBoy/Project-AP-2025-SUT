package ap.project.screen;

import ap.project.control.CharacterController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.*;
import ap.project.model.game.Game;
import ap.project.screen.input.WorldScreenInputProcessor;
import ap.project.util.MapAssetLoader;
import ap.project.view.GameMenu;
import ap.project.visual.CharacterRenderer;
import ap.project.model.game.*;
import ap.project.visual.MapVisual;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public final class WorldScreen implements Screen
{
    private static WorldScreen INSTANCE;

    public static final float MAP_SCALE  = 1.0f;
    private static final float CHAR_SCALE = 1f;
    private static final float TILE_SIZE  = 24f * MAP_SCALE;
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

    private InputMultiplexer inputMultiplexer;
    private boolean inputMultiplexerHadSetUp = false;

    private boolean cameraFixed = false;

    public WorldScreen()
    {
        INSTANCE = this;

        this.shapeRenderer = new ShapeRenderer();
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        this.game = new Game(new ArrayList<>(List.of(
            new Player(new User("mohsen","","mohsen","", Gender.FEMALE, "", ""), MapTypes.STANDARD, 0),
            new Player(new User("arash","","arash","", Gender.FEMALE, "", ""), MapTypes.FISHING, 0),
            new Player(new User("moshtagh","","moshtagh","", Gender.FEMALE, "", ""), MapTypes.FORAGING, 0),
            new Player(new User("ottie","","ottie","", Gender.FEMALE, "", ""), MapTypes.COMBAT, 0)
            )));

        App.setCurrentGame(game);
        App.setCurrentMenu(Menu.GameMenu);
        App.setCurrentUser(game.getPlayers().get(0).getUser());
        game.setCurrentPlayer(game.getPlayers().get(0));

//        this.game = App.getCurrentGame();

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

        this.map = game.getCurrentPlayer().getCurrentMap();
        time = game.getCurrentTime();
        currentSeason = time.getSeason();

        this.characterRenderer = new CharacterRenderer(shapeRenderer);

        if (SECOND_PLAYER)
        {
            Vector2 spawn2 = new Vector2(62 * TILE_SIZE, 60 * TILE_SIZE);
            player2 = new PlayerCharacter(CharacterType.ABIGAIL, spawn2, "Player 456");
            controller2 = new CharacterController(player2, map, PLAYER_SPEED, TILE_SIZE);
            controller2.chnageMoveKeys(Input.Keys.UP, Input.Keys.LEFT, Input.Keys.DOWN, Input.Keys.RIGHT);
        }

        ShaderProgram.pedantic = false;
        uiRenderer = new UIRenderer(time);

        inventoryWindow = new InventoryWindow(uiStage);
        createTerminalDialog();

        inputMultiplexer = new InputMultiplexer();
        checkGameInfo();
    }

    public void checkGameInfo()
    {
        if (player == null || !App.getCurrentGame().getCurrentPlayer().equals(player))
        {
            updateGameInfo();
        }
    }

    public void updateGameInfo()
    {
        this.player = App.getCurrentGame().getCurrentPlayer();
        this.character = player.getCharacter();
        this.map = player.getCurrentMap();

        updateMap();

        characterController = new CharacterController(character, map, PLAYER_SPEED, TILE_SIZE);

        gameInputProcessor = new WorldScreenInputProcessor(map, character, characterController, cam, this);

        if (inputMultiplexer != null && inputMultiplexerHadSetUp)
        {
            inputMultiplexer.removeProcessor(1);
            inputMultiplexer.addProcessor(1, gameInputProcessor);
        }

        uiRenderer.updatePlayer();
    }

    @Override
    public void show()
    {
        inputMultiplexer.clear();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(gameInputProcessor);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.E || keycode == Input.Keys.ESCAPE) {
                    inventoryWindow.toggleVisibility();
                    return true;
                }
                else if (keycode == Input.Keys.TAB) {
                    inventoryWindow.toggleVisibility();
                    inventoryWindow.getToolsTab().setChecked(true);
                    return true;
                }
                else if (keycode == Input.Keys.M) {
                    inventoryWindow.toggleVisibility();
                    inventoryWindow.getMapTab().setChecked(true);
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexerHadSetUp = true;
    }

    @Override
    public void render (float dt)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!terminalDialog.isVisible() && !isInventoryVisible())
        {
            update(dt);
            cam.update();
        }

        map.getMapVisual().render(cam);

        Batch batch = map.getMapVisual().getRenderer().getBatch();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);
        batch.begin();

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
        batch.end();

        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        uiRenderer.renderUI(batch, uiCam);
        uiRenderer.renderWeatherOverlay(batch, uiCam);
        batch.end();

        uiRenderer.renderDarkOverlay(uiCam);

        if (hoveredTile != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // ← add this

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 0f, 0f, 0.3f);

            Tile tile = map.getTile(hoveredTile.getX(), hoveredTile.getY() - 1);
            Vector2 location = map.tileToWorld(tile);

            if (location != null)
            {
//                shapeRenderer.rect(location.x, location.y - (16f * MAP_SCALE), (16f * MAP_SCALE), (16f * MAP_SCALE));
            }

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        uiStage.act(dt);
        uiStage.draw();
    }

    private void update(float dt)
    {
        characterController.update(dt);
        if (SECOND_PLAYER) controller2.update(dt);

        checkGameInfo();
        checkMapSeason();
        map.getMapVisual().update(dt);
        UIRenderer.updateTextBoxes(dt);

        if ( map.getMapType() == MapTypes.GREEN_HOUSE || map.getMapType() == MapTypes.CARPENTER_SHOP ||
        map.getMapType() == MapTypes.MARNIE_RANCH)
        {
            cameraFixed = false;
        }

        if (!cameraFixed)
        {
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
        // keep a 4:3 virtual viewport (letter‑box if needed)
        float targetRatio = 4f / 3f;
        float currentRatio = (float) w / h;

        if (currentRatio > targetRatio) {             // too wide → pillarbox
            cam.viewportHeight = 15 * TILE_SIZE;
            cam.viewportWidth  = cam.viewportHeight * currentRatio;
        } else {                                      // too tall → letterbox
            cam.viewportWidth  = 20 * TILE_SIZE;
            cam.viewportHeight = cam.viewportWidth / currentRatio;
        }

        // --- UI camera uses raw screen pixels (0,0 bottom-left)
        uiCam.setToOrtho(false, w, h);
        uiCam.update();

        uiStage.getViewport().update(w, h, true);
    }

    @Override public void pause(){}  @Override public void resume(){}
    @Override public void hide(){}
    @Override public void dispose(){
        map.getMapVisual().dispose();
        uiRenderer.dispose();
        shapeRenderer.dispose();
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
                if (object != null && object.equals("SUBMIT"))
                {
                    processUserInput();
                }
                closeDialog();
            }
        };

        // Create text buffer display
        dialogContentLabel = new Label("", skin);
        dialogContentLabel.setWrap(true); // Enable text wrapping

        // Create scrollable container for text buffer
        ScrollPane scrollPane = new ScrollPane(dialogContentLabel, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setFlickScroll(false);
        scrollPane.setOverscroll(false, false);

        // Create input field
        userInputField = new TextField("", skin);
        userInputField.setMessageText("Enter command here...");

        // Layout components - WIDER dimensions
        Table contentTable = terminalDialog.getContentTable();
        contentTable.add(scrollPane).grow().width(1000).height(350).pad(10); // Wider and taller
        contentTable.row();
        contentTable.add(userInputField).growX().width(1000).pad(10); // Wider input field

        // Create button table with smaller buttons
        Table buttonTable = new Table();
        buttonTable.defaults().pad(3).minWidth(40); // Smaller padding and width

        // Create smaller buttons
        TextButton submitButton = new TextButton("Submit", skin);
        TextButton closeButton = new TextButton("Close", skin);

        // Scale down button text
        Label submitLabel = (Label)submitButton.getLabel();
        submitLabel.setFontScale(0.4f); // Reduce font size

        Label closeLabel = (Label)closeButton.getLabel();
        closeLabel.setFontScale(0.4f); // Reduce font size

        // Reduce button padding
        submitButton.pad(3, 8, 3, 8);
        closeButton.pad(3, 8, 3, 8);

        // Add button listeners
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

        // Add buttons to table
        buttonTable.add(submitButton).padRight(10);
        buttonTable.add(closeButton);

        // Add button table to dialog
        contentTable.row();
        contentTable.add(buttonTable).padTop(10).right();

        // Keyboard shortcuts
        terminalDialog.key(Input.Keys.ENTER, "SUBMIT"); // Press Enter to submit
        terminalDialog.key(Input.Keys.ESCAPE, "CLOSE"); // Press ESC to close

        // Configure dialog window
        terminalDialog.setModal(true);
        terminalDialog.setMovable(true);
        terminalDialog.setResizable(true);
        terminalDialog.setSize(700, 500); // Larger initial size (wider and taller)
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

        // Reset input field
        userInputField.setText("");

        terminalDialog.show(uiStage);
        terminalDialog.setVisible(true);

        // Center dialog on screen
        terminalDialog.setPosition(
            (uiStage.getWidth() - terminalDialog.getWidth()) / 2,
            (uiStage.getHeight() - terminalDialog.getHeight()) / 2
        );

        // Update display with current buffer content
        updateDialogDisplay();

        // Set focus to text field
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

            // Auto-scroll to bottom
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


}
