package ap.project.screen;

import ap.project.Main;
import ap.project.control.CharacterController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.Season;
import ap.project.model.game.*;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.CloseLobbyMessage;
import ap.project.network.shared.messages.CreateGameRequestMessage;
import ap.project.network.shared.messages.LeaveLobbyMessage;
import ap.project.network.shared.messages.LobbyPresenceMessage;
import ap.project.util.MapAssetLoader;
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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class LobbyScreen implements Screen
{
    private static LobbyScreen INSTANCE;
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

    private final OrthographicCamera cam;
    private final OrthographicCamera uiCam = new OrthographicCamera();
    private final TextBoxSystem textBoxSystem = new TextBoxSystem();
    private final Stage stage;
    private final Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch uiBatch = new SpriteBatch();

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
    private static final float POSITION_UPDATE_INTERVAL = 0.016f;

    public LobbyScreen(String lobbyName, String lobbyId, String adminName, Player player)
    {
        INSTANCE = this;

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
        player.spawn(map);

        localPlayer = player;
        localCharacter = player.getCharacter();

        this.shapeRenderer = new ShapeRenderer();

        // Initialize controllers and renderers
        characterController = new CharacterController(localCharacter, map, PLAYER_SPEED, TILE_SIZE);
        characterRenderer = new CharacterRenderer(shapeRenderer);

        ShaderProgram.pedantic = false;

        // Replace current camera initialization
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        // Add this after map initialization
        MapAssetLoader.LoadedMap loaded = MapAssetLoader.loadFromTmx(map.getMapType().getName(), Season.Spring, map.getMapType().getMapKind());
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

        // Set up UI
        stage = new Stage(new ScreenViewport()); // Ensure proper viewport
        Gdx.input.setInputProcessor(stage);

        // Create lobby info label
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        infoFont = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle infoLabelStyle = new Label.LabelStyle();
        infoLabelStyle.font = infoFont;

        lobbyInfoLabel = new Label("", infoLabelStyle);
        lobbyInfoLabel.setAlignment(Align.topRight);
        updateLobbyInfo(300);

        // Create buttons
        leaveButton = new TextButton("Leave Lobby", skin);
        closeButton = new TextButton("Close Lobby", skin);
        startButton = new TextButton("Start Game", skin);

        // Create info container (top-right)
        Table infoContainer = new Table();
        infoContainer.top().right();
        infoContainer.setFillParent(true);

        // Create a nested table for the lobby info with left alignment
        Table contentTable = new Table();
        contentTable.align(Align.topLeft); // Left-align text
        contentTable.add(lobbyInfoLabel).pad(20).top().left();

        infoContainer.add(contentTable).expand().top().right().pad(20);

        // Position buttons at bottom center
        Table buttonTable = new Table();
        buttonTable.bottom();
        buttonTable.setFillParent(true);
        buttonTable.add(leaveButton).pad(10);
        buttonTable.add(closeButton).pad(10);
        buttonTable.add(startButton).pad(10);

        // Add UI elements to stage
        stage.addActor(infoContainer);
        stage.addActor(buttonTable);

        // Add button listeners
        setupButtonListeners();
    }

    public static LobbyScreen getINSTANCE()
    {
        return INSTANCE;
    }

    private void setupButtonListeners()
    {
        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLeaveLobby();
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleCloseLobby();
            }
        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleStartGame();
            }
        });
    }

    private void updateLobbyInfo(int remainingSeconds)
    {
        String info = "Name: " + lobbyName + "\n" +
            "ID: " + lobbyId + "\n" +
            "Remaining Time: " + formatTime(remainingSeconds) + "\n" +
            "Admin: " + getAdminName() + "\n" +
            "Players: " + getPlayerList();

        lobbyInfoLabel.setText(info);
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
    }

    @Override
    public void render(float delta)
    {
        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update game state
        update(delta);
        cam.update();
        textBoxSystem.update(delta);

        // Render map
        mapVisual.render(cam);

        Batch batch = map.getMapVisual().getRenderer().getBatch();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);
        batch.begin();

        Vector3 mouseWorldPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouseWorldPos);

        renderCharacters(batch);

        batch.end();

        stage.act(delta);
        stage.draw();

        uiBatch.begin();
        textBoxSystem.render(uiBatch);
        uiBatch.end();
    }

    private void update(float delta)
    {
        // Update character controller
        characterController.update(delta);
        map.getMapVisual().update(delta);
        updateOtherPlayers(delta);

        if (!cameraFixed)
        {
            float playerX = localCharacter.getPosition().x + TILE_SIZE / 2;
            float playerY = localCharacter.getPosition().y + TILE_SIZE / 2;

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

        client.send(new LobbyPresenceMessage(localCharacter.getPosition().x,
            localCharacter.getPosition().y,
            directionByte,
            localCharacter.isMoving()));
    }

    public void setRemainingTime(int seconds)
    {
        updateLobbyInfo(seconds);
    }

    private void handleLeaveLobby()
    {
        // Implementation to leave lobby
        client.send(new LeaveLobbyMessage());
        textBoxSystem.showTextBox("Leaving lobby...");
    }

    private void handleCloseLobby()
    {
        // Implementation to close lobby (admin only)
        client.send(new CloseLobbyMessage());
        textBoxSystem.showTextBox("Closing lobby...");
    }

    private void handleStartGame()
    {
        // Implementation to start game
        client.send(new CreateGameRequestMessage());
    }

    @Override
    public void resize(int width, int height)
    {
        float currentRatio = (float) width / height;
        cam.viewportHeight = 15 * TILE_SIZE;
        cam.viewportWidth = cam.viewportHeight * currentRatio;
        cam.update();

        stage.getViewport().update(width, height, true);

        // Update UI camera
        uiCam.setToOrtho(false, width, height);
        uiCam.update();
        uiBatch.setProjectionMatrix(uiCam.combined);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose()
    {
        stage.dispose();
        textBoxSystem.dispose();
        mapVisual.dispose();
        infoFont.dispose();
        uiBatch.dispose();
    }

    public void showText(String text)
    {
        textBoxSystem.showTextBox(text);
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
            return false;
        }

        User user = new User(username, nickname, userId, avatarChoice, mapChoice);
        Player player = new Player(user, true);

        if (otherPlayers.size() < 3)
        {
            otherPlayers.add(player);
            player.spawn(map);
            return true;
        }

        return false;
    }

    private void updateOtherPlayers(float delta)
    {
        long currentTime = System.currentTimeMillis();

        for (Player p : otherPlayers)
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

    public void updatePlayerPosition(int userId, float x, float y, byte direction, boolean isMoving)
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

    public void setCameraFixed(boolean cameraFixed)
    {
        this.cameraFixed = cameraFixed;

        if (cameraFixed)
        {
            cam.position.set(localCharacter.getPosition().x + TILE_SIZE/2, localCharacter.getPosition().y + TILE_SIZE/2, 0);
            cam.update();
        }
    }

    private Player getPlayer(int userId)
    {
        if (localPlayer.getUser().getHashId() == userId)
        {
            return localPlayer;
        }

        for (Player p : otherPlayers)
        {
            if (p.getUser().getHashId() == userId)
            {
                return p;
            }
        }

        return null;
    }

    public void createGame(String gameID, int[] userIds)
    {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < userIds.length; i++)
        {
            int id = userIds[i];
            Player player = getPlayer(id);

            if (player != null)
            {
                players.add(new Player(player.getUser()));
            }
        }

        Game game = new Game(players, gameID);
        App.addGame(game);
        App.setCurrentGame(game);

        for (Player player : players)
        {
            if (player.getUser().getHashId() == localPlayer.getUser().getHashId())
            {
                game.setCurrentPlayer(player);
                break;
            }
        }

        Main.getApp().setScreen(new WorldScreen(players));
    }
}
