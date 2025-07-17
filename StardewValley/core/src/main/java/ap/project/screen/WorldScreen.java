package ap.project.screen;

import ap.project.control.CharacterController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.enums.Season;
import ap.project.model.game.Game;
import ap.project.screen.input.WorldScreenInputProcessor;
import ap.project.util.MapAssetLoader;
import ap.project.view.GameMenu;
import ap.project.visual.CharacterRenderer;
import ap.project.model.enums.CharacterType;
import ap.project.model.enums.MapTypes;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public final class WorldScreen implements Screen
{
    public static final float MAP_SCALE  = 1.0f;
    private static final float CHAR_SCALE = 1f;
    private static final float TILE_SIZE  = 24f * MAP_SCALE;
    private static final float PLAYER_SPEED = 50f * MAP_SCALE;

    private final Game game;

    private final OrthographicCamera cam;
    private final OrthographicCamera uiCam = new OrthographicCamera();

    private final PlayerCharacter player;
    private CharacterController characterController;
    private final CharacterRenderer characterRenderer;

    private Farm farm;
    private final UIRenderer uiRenderer;

    private Point hoveredTile = null;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final boolean SECOND_PLAYER = false;
    private PlayerCharacter player2;
    private CharacterController controller2;

    private final Time time;
    private Season currentSeason;

    private final Stage uiStage = new Stage();
    private final Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

    private WorldScreenInputProcessor gameInputProcessor;
    private Dialog testDialog;
    private TextField userInputField;
    private static StringBuilder dialogTextBuffer = new StringBuilder();
    private static Label dialogContentLabel;
    private InventoryWindow inventoryWindow;

    public WorldScreen()
    {
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        this.game = new Game(new ArrayList<>(List.of(new Player(new User("","","","", Gender.FEMALE, "", ""), MapTypes.STANDARD, 0))));
        App.setCurrentGame(game);
        game.setCurrentPlayer(game.getPlayers().get(0));

//        this.game = App.getCurrentGame();
        for (Player p : game.getPlayers())
        {
            Farm f = new Farm(p.getMapType());
            p.setFarm(f);
        }

        farm = game.getPlayers().get(0).getFarm();
        time = game.getCurrentTime();
        currentSeason = time.getSeason();

        Vector2 spawn = new Vector2(10 * TILE_SIZE, 10 * TILE_SIZE);
        player = new PlayerCharacter(CharacterType.ABIGAIL, spawn);
        characterController = new CharacterController(player, farm, PLAYER_SPEED, TILE_SIZE);
        characterRenderer = new CharacterRenderer();

        if (SECOND_PLAYER)
        {
            Vector2 spawn2 = new Vector2(62 * TILE_SIZE, 60 * TILE_SIZE);
            player2 = new PlayerCharacter(CharacterType.ABIGAIL, spawn2); // or any other character
            controller2 = new CharacterController(player2, farm, PLAYER_SPEED, TILE_SIZE);
            controller2.chnageMoveKeys(Input.Keys.UP, Input.Keys.LEFT, Input.Keys.DOWN, Input.Keys.RIGHT);
        }

        ShaderProgram.pedantic = false;
        uiRenderer = new UIRenderer(time);

        gameInputProcessor = new WorldScreenInputProcessor(farm, player, characterController, cam, this);
        Gdx.input.setInputProcessor(gameInputProcessor);
        inventoryWindow = new InventoryWindow(uiStage);
        createTestDialog();
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.E || keycode == Input.Keys.ESCAPE) {
                    inventoryWindow.toggleVisibility();
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render (float dt)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!testDialog.isVisible())
        {
            update(dt);
            cam.update();
        }

        farm.getMapVisual().render(cam);

        Batch batch = farm.getMapVisual().getRenderer().getBatch();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        characterRenderer.render(batch, player, CHAR_SCALE);
        if (SECOND_PLAYER) characterRenderer.render(batch, player2, CHAR_SCALE);
        batch.end();

        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        uiRenderer.renderUI(batch, uiCam);
        batch.end();

        uiRenderer.renderDarkOverlay(uiCam);

        if (hoveredTile != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // ← add this

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 0f, 0f, 0.3f);

            Tile tile = farm.getTile(hoveredTile.getX(), hoveredTile.getY() - 1);
            Vector2 location = farm.tileToWorld(tile);

            if (location != null)
            {
                shapeRenderer.rect(location.x, location.y - (16f * MAP_SCALE), (16f * MAP_SCALE), (16f * MAP_SCALE));
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

        updateSeason();

        cam.position.set(player.getPosition().x + TILE_SIZE/2, player.getPosition().y + TILE_SIZE/2, 0);

        float mapPixelW = farm.getWIDTH()  * TILE_SIZE;
        float mapPixelH = farm.getHEIGHT() * TILE_SIZE;

        cam.position.x = MathUtils.clamp(cam.position.x,
            cam.viewportWidth  /2, mapPixelW  - cam.viewportWidth /2);
        cam.position.y = MathUtils.clamp(cam.position.y,
            cam.viewportHeight /2, mapPixelH - cam.viewportHeight/2);
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
        farm.getMapVisual().dispose();
        uiRenderer.dispose();
        shapeRenderer.dispose();
    }

    private void updateSeason()
    {
        if (time.getSeason() != currentSeason)
        {
            MapAssetLoader.LoadedMap loaded = MapAssetLoader.loadFromTmx(farm.getMapType().getName(), time.getSeason());
            TiledMap tiledMap = loaded.tiledMap;
            farm.setVisual(new MapVisual(farm, tiledMap));
        }

        currentSeason = time.getSeason();
    }

    public Tile cursorToTile()
    {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY() + (60); // Hard-Coded

        hoveredTile = farm.screenToTile(mouseX, mouseY, cam);
        Tile tile = farm.getTile(hoveredTile.getX(), hoveredTile.getY() - 1);

        return tile;
    }

    private void createTestDialog() {
        testDialog = new Dialog("Game Console", skin) {
            @Override
            protected void result(Object object) {
                // Only process if submit was clicked
                if (object != null && object.equals("SUBMIT")) {
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
        Table contentTable = testDialog.getContentTable();
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
        testDialog.key(Input.Keys.ENTER, "SUBMIT"); // Press Enter to submit
        testDialog.key(Input.Keys.ESCAPE, "CLOSE"); // Press ESC to close

        // Configure dialog window
        testDialog.setModal(true);
        testDialog.setMovable(true);
        testDialog.setResizable(true);
        testDialog.setSize(700, 500); // Larger initial size (wider and taller)
        testDialog.setVisible(false);
    }

    private void processUserInput() {
        String input = userInputField.getText().trim();

        if (!input.isEmpty()) {
            appendToDialog("> " + input);
            GameMenu.check(input);
        }
    }

    public void showTestDialog() {
        if (testDialog.isVisible()) return;

        // Reset input field
        userInputField.setText("");

        testDialog.show(uiStage);
        testDialog.setVisible(true);

        // Center dialog on screen
        testDialog.setPosition(
            (uiStage.getWidth() - testDialog.getWidth()) / 2,
            (uiStage.getHeight() - testDialog.getHeight()) / 2
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
        testDialog.hide();
        testDialog.setVisible(false);
        Gdx.input.setInputProcessor(gameInputProcessor); // Restore game input
    }

    public boolean isDialogVisible() {
        return testDialog.isVisible();
    }

}
