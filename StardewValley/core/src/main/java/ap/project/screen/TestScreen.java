package ap.project.screen;

import ap.project.control.CharacterController;
import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.enums.Season;
import ap.project.util.MapAssetLoader;
import ap.project.visual.CharacterRenderer;
import ap.project.model.enums.CharacterType;
import ap.project.model.enums.MapTypes;
import ap.project.model.game.*;
import ap.project.visual.MapVisual;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;

public final class TestScreen implements Screen
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

    private Season season = Season.Spring;

    private final boolean SECOND_PLAYER = false;
    private PlayerCharacter player2;
    private CharacterController controller2;

    private final Time time;
    private Season currentSeason;

    public TestScreen()
    {
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        this.game = new Game(new ArrayList<>(List.of(new Player(new User("","","","", Gender.FEMALE, "", ""), MapTypes.STANDARD, 0))));

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
    }

    @Override
    public void render (float dt)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(dt);

        cam.update();

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

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
        {
            Tile tile = cursorToTile();

            if (tile != null)
            {
                System.out.println("Clicked Tile (x: " + tile.getX() + ", y: " + tile.getY() + ") - " + tile.getTexture());
//                for (int i = 0; i < farm.getDepth(); i++)
//                {
//                    Tile layerTile = farm.getLayerTiles()[i][tile.getY()][tile.getX()];
//                    if (layerTile != null)
//                    {
//                        System.out.println("Layer " + i + ": " + layerTile.getTypeName());
//                    } else
//                    {
//                        System.out.println("Layer " + i + ": null");
//                    }
//                }
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT))
        {
            Point clicked = farm.screenToTile(Gdx.input.getX(), Gdx.input.getY(), cam);

            if (clicked != null)
            {
                Vector2 playerPos = player.getPosition();
                Point playerTile = farm.worldToTile(playerPos.x, playerPos.y);

                ArrayList<Point> path = farm.findShortestPath(playerTile, clicked);

                if (path != null)
                {
                    characterController.moveToPath(path);
                }
            }
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
    }

    @Override public void pause(){}  @Override public void resume(){}
    @Override public void show(){}   @Override public void hide(){}
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

    private Tile cursorToTile()
    {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY() + (60); // Hard-Coded

        hoveredTile = farm.screenToTile(mouseX, mouseY, cam);
        Tile tile = farm.getTile(hoveredTile.getX(), hoveredTile.getY() - 1);

        return tile;
    }
}
