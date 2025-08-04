package ap.project.screen;

import ap.project.control.CharacterController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.CharacterType;
import ap.project.model.game.*;
import ap.project.model.game.Point;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.LobbyPresenceMessage;
import ap.project.visual.CharacterRenderer;
import ap.project.visual.MapVisual;
import ap.project.visual.TextBoxSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LobbyScreen implements Screen
{
    private static LobbyScreen instance;
    private final GameClient client;

    private String adminName;
    private String lobbyName;
    private String lobbyId;

    private final LobbyMap map;
    private final MapVisual mapVisual;
    private final Player localPlayer;
    private final PlayerCharacter localCharacter;
    private final CharacterController characterController;
    private final CharacterRenderer characterRenderer;
    private final ArrayList<Player> otherPlayers = new ArrayList<>();

    private final OrthographicCamera camera;
    private final Batch batch;
    private final TextBoxSystem textBoxSystem = new TextBoxSystem();
    private final Stage stage;
    private final Skin skin;
    private final ShapeRenderer shapeRenderer;

    private final BitmapFont infoFont;
    private final Label lobbyInfoLabel;
    private final TextButton leaveButton;
    private final TextButton closeButton;
    private final TextButton startButton;

    public static final float MAP_SCALE = 1.0f;
    private static final float CHAR_SCALE = 1f;
    private static final float TILE_SIZE = 24f * MAP_SCALE;
    private static final float PLAYER_SPEED = 50f * MAP_SCALE;

    private boolean cameraFixed = false;

    private float positionUpdateTimer = 0;
    private static final float POSITION_UPDATE_INTERVAL = 0.1f; // 100ms

    public LobbyScreen(String lobbyName, String lobbyId, String adminName, Player player)
    {
        this.adminName = adminName;
        this.lobbyName = lobbyName;
        this.lobbyId = lobbyId;
        this.client = GameClient.getInstance();

        Game game = new Game(new ArrayList<Player>(List.of(player)));
        game.setCurrentPlayer(player);
        App.setCurrentGame(game);

        // Initialize map and visual
        map = new LobbyMap();
        mapVisual = map.getMapVisual();

        // Initialize player and character
        player.spawn();
        player.setCurrentMap(map);

        localPlayer = player;
        localCharacter = player.getCharacter();

        this.shapeRenderer = new ShapeRenderer();

        // Initialize controllers and renderers
        characterController = new CharacterController(localCharacter, map, PLAYER_SPEED, TILE_SIZE);
        characterRenderer = new CharacterRenderer(shapeRenderer);

        // Replace current camera initialization
        camera = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        camera.setToOrtho(false);

        // Add this after map initialization
        float mapPixelWidth = map.getWidth() * TILE_SIZE;
        float mapPixelHeight = map.getHeight() * TILE_SIZE;

        if (mapPixelWidth <= camera.viewportWidth || mapPixelHeight <= camera.viewportHeight)
        {
            cameraFixed = true;
            float centerX = (map.getWidth() * TILE_SIZE) / 2f;
            float centerY = (map.getHeight() * TILE_SIZE) / 2f;
            camera.position.set(centerX, centerY, 0);
            camera.update();
        } else
        {
            cameraFixed = false;
        }

        batch = new SpriteBatch();

        // Set up UI
        skin = GameAssetsManager.getGameAssetsManager().getSkin();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create lobby info label
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        infoFont = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle infoLabelStyle = new Label.LabelStyle();
        infoLabelStyle.font = infoFont;

        lobbyInfoLabel = new Label("", infoLabelStyle);
        lobbyInfoLabel.setPosition(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() - 20, Align.topRight);
        updateLobbyInfo(lobbyName, lobbyId, 300); // 5 minutes = 300 seconds

        // Create buttons
        leaveButton = new TextButton("Leave Lobby", skin);
        closeButton = new TextButton("Close Lobby", skin);
        startButton = new TextButton("Start Game", skin);

        // Position buttons at bottom center
        Table buttonTable = new Table();
        buttonTable.bottom();
        buttonTable.setFillParent(true);
        buttonTable.add(leaveButton).pad(10);
        buttonTable.add(closeButton).pad(10);
        buttonTable.add(startButton).pad(10);

        // Add UI elements to stage
        stage.addActor(lobbyInfoLabel);
        stage.addActor(buttonTable);

        // Add button listeners
        setupButtonListeners();

        instance = this;
    }

    public static LobbyScreen getInstance()
    {
        return instance;
    }

    private void setupButtonListeners()
    {
        leaveButton.addListener(event ->
        {
            handleLeaveLobby();
            return true;
        });

        closeButton.addListener(event ->
        {
            handleCloseLobby();
            return true;
        });

        startButton.addListener(event ->
        {
            handleStartGame();
            return true;
        });
    }

    private void updateLobbyInfo(String name, String id, int remainingSeconds)
    {
        String info = "Name: " + name + "\n" +
            "ID: " + id + "\n" +
            "Remaining Time: " + formatTime(remainingSeconds) + "\n" +
            "Admin: " + getAdminName() + "\n" +
            "Players: " + getPlayerList();

        lobbyInfoLabel.setText(info);
        lobbyInfoLabel.setPosition(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() - 20, Align.topRight);
    }

    private String formatTime(int seconds)
    {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private String getAdminName()
    {
        return adminName;
    }

    private String getPlayerList()
    {
        StringBuilder sb = new StringBuilder();
        for (Player player : otherPlayers)
        {
            sb.append("\n- ").append(player.getUser().getUsername());
        }
        return sb.toString();
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);

        int mapPixelWidth = (int) (map.getWidth() * TILE_SIZE);
        int mapPixelHeight = (int) (map.getHeight() * TILE_SIZE);
        cameraFixed = mapPixelWidth <= camera.viewportWidth || mapPixelHeight <= camera.viewportHeight;
    }

    @Override
    public void render(float delta)
    {
        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update game state
        update(delta);

        // Update camera - REMOVE YOUR CURRENT CAMERA UPDATE CALLS
        updateCamera(delta);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Render map
        mapVisual.render(camera); // Use cam, not camera

        // Render characters
        batch.begin();
        renderCharacters(batch);
        batch.end();

        // Render UI
        stage.act(delta);
        stage.draw();

        // Render text boxes
        textBoxSystem.update(delta);
        textBoxSystem.render(batch);
    }

    private void update(float delta)
    {
        // Update character controller
        characterController.update(delta);
        client.processMessages();

        // Update position update timer
        positionUpdateTimer += delta;
        if (positionUpdateTimer >= POSITION_UPDATE_INTERVAL)
        {
            sendPositionUpdate();
            positionUpdateTimer = 0;
        }

        // Update lobby info (remaining time would come from server)
        // updateLobbyInfo(lobbyName, lobbyId, remainingTime);

        // Update other players positions (would come from server)
        // updateOtherPlayers();
    }

    private void renderCharacters(Batch batch)
    {
        characterRenderer.render(batch, localCharacter, CHAR_SCALE);

        for (Player player : otherPlayers)
        {
            PlayerCharacter character = player.getCharacter();
            if (character != null)
            {
                characterRenderer.render(batch, character, CHAR_SCALE);
            }
        }
    }

    private void sendPositionUpdate()
    {
        byte directionByte = (byte) localCharacter.getDirection().ordinal();

        client.send(new LobbyPresenceMessage(localPlayer.getLocation().getX(),
            localPlayer.getLocation().getY(),
            directionByte,
            localCharacter.isMoving()));
    }

    public void setRemainingTime(int seconds)
    {
        updateLobbyInfo("", "", seconds);
    }

    private void handleLeaveLobby()
    {
        // Implementation to leave lobby
        textBoxSystem.showTextBox("Leaving lobby...");
    }

    private void handleCloseLobby()
    {
        // Implementation to close lobby (admin only)
        textBoxSystem.showTextBox("Closing lobby...");
    }

    private void handleStartGame()
    {
        // Implementation to start game
        textBoxSystem.showTextBox("Starting game...");
    }

    @Override
    public void resize(int width, int height)
    {
        float currentRatio = (float) width / height;
        camera.viewportHeight = 15 * TILE_SIZE;
        camera.viewportWidth = camera.viewportHeight * currentRatio;
        camera.update();

        stage.getViewport().update(width, height, true);

        // Reposition info label to top-right corner
        lobbyInfoLabel.setPosition(Gdx.graphics.getWidth() - 20, Gdx.graphics.getHeight() - 20, Align.topRight);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        textBoxSystem.dispose();
        mapVisual.dispose();
        infoFont.dispose();
    }

    public void showText(String text)
    {
        textBoxSystem.showTextBox(text);
    }

    private void updateCamera(float delta)
    {
        if (!cameraFixed)
        {
            float halfViewportW = camera.viewportWidth / 2f;
            float halfViewportH = camera.viewportHeight / 2f;
            float mapPixelWidth = map.getWidth() * TILE_SIZE;
            float mapPixelHeight = map.getHeight() * TILE_SIZE;

            float camX = MathUtils.clamp(localPlayer.getPosition().x, halfViewportW, mapPixelWidth - halfViewportW);
            float camY = MathUtils.clamp(localPlayer.getPosition().y, halfViewportH, mapPixelHeight - halfViewportH);

            camera.position.set(camX, camY, 0);
        }
        else
        {
            // When the map is smaller than the screen, center it
            float centerX = map.getWidth() * TILE_SIZE / 2f;
            float centerY = map.getHeight() * TILE_SIZE / 2f;
            camera.position.set(centerX, centerY, 0);
        }
        camera.update();
    }

    private boolean isInLobby(int userId)
    {
        if (localPlayer.getUser().getHashId() == userId)
        {
            return true;
        }

        for (Player player : otherPlayers)
        {
            if (player.getUser().getHashId() == userId)
            {
                return true;
            }
        }

        return false;
    }

    public String getLobbyName()
    {
        return lobbyName;
    }

    public String getLobbyId()
    {
        return lobbyId;
    }

    public boolean addOtherPlayer(int userId, String username, String nickname, int avatarChoice, int mapChoice)
    {
        if (isInLobby(userId))
        {
            textBoxSystem.showTextBox("user is already in lobby");
            return false;
        }

        User user = new User(username, nickname, userId, avatarChoice, mapChoice);
        Player player = new Player(user);

        if (otherPlayers.size() < 3)
        {
            otherPlayers.add(player);
            player.spawn();
            player.setCurrentMap(map);
            return true;
        }

        return false;
    }

    public void updatePlayerPosition(int userId, int x, int y, byte direction, boolean isMoving)
    {
        for (Player p : otherPlayers)
        {
            if (p.getUser().getHashId() == userId)
            {
                PlayerCharacter pc = p.getCharacter();
                pc.setPosition(new Vector2(x, y));
                pc.setDirection(AbstractCharacter.Direction.values()[direction]);
                pc.setMoving(isMoving);
                pc.setLastUpdateTime(System.currentTimeMillis());
            }
        }
    }

    public void removeOtherPlayer(int userId)
    {
        for (Player p : otherPlayers)
        {
            if (p.getUser().getHashId() == userId)
            {
                otherPlayers.remove(p);
                textBoxSystem.showTextBox(p.getUser().getUsername() + " has been removed");
                return;
            }
        }

        textBoxSystem.showTextBox("player not found");
    }
}
