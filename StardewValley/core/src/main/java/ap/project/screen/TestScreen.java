package ap.project.screen;

import ap.project.control.CharacterController;
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
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public final class TestScreen implements Screen
{
    public static final float MAP_SCALE  = 1.0f;   // 1 = native 16‑px tiles, 3 = 48px, etc.
    private static final float CHAR_SCALE = 1f; // how big Abigail appears relative to map
    private static final float TILE_SIZE  = 24f * MAP_SCALE;
    private static final float PLAYER_SPEED = 50f * MAP_SCALE;
    private static final float FRAME_TIME = 0.16f;

    private final OrthographicCamera cam;
    private final MapVisual mapVisual;


    private final OrthographicCamera uiCam = new OrthographicCamera();

    private final PlayerCharacter player;
    private final CharacterController characterController;
    private final CharacterRenderer characterRenderer;

    private final Farm farm;
    private final UIRenderer uiRenderer;

    private ShaderProgram autumnShader;
    private boolean useAutumn = false;   // toggle if you want seasons

    private Point hoveredTile = null;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public TestScreen()
    {
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        farm = new Farm(MapTypes.FOREST);
        mapVisual = farm.getMapVisual();

        Vector2 spawn = new Vector2(60 * TILE_SIZE, 60 * TILE_SIZE);
        player = new PlayerCharacter(CharacterType.ABIGAIL, spawn);
        characterController = new CharacterController(player, farm, PLAYER_SPEED, TILE_SIZE);
        characterRenderer = new CharacterRenderer();

        ShaderProgram.pedantic = false;
        autumnShader = new ShaderProgram(
            Gdx.files.internal("default.vert"),
            Gdx.files.internal("fall.frag"));
        if (!autumnShader.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + autumnShader.getLog());
        }

        uiRenderer = new UIRenderer(new Time());
    }

    @Override
    public void render (float dt) {
        update(dt);

        cam.update();

        mapVisual.render(cam, useAutumn ? autumnShader : null);

        Batch batch = mapVisual.getRenderer().getBatch();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        characterRenderer.render(batch, player, CHAR_SCALE);
        batch.end();

        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        uiRenderer.renderUI(batch, uiCam);
        batch.end();

        if (hoveredTile != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // ← add this

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 0f, 0f, 0.3f);

            Tile tile = farm.getTile(hoveredTile.getX(), hoveredTile.getY());
            Vector2 location = farm.tileToWorld(tile);

            if (location != null)
            {
                shapeRenderer.rect(location.x, location.y, TILE_SIZE, TILE_SIZE);
            }

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void update(float dt) {

        characterController.update(dt);

        // -------- camera --------  keep centred but clamped to map bounds
        cam.position.set(player.getPosition().x + TILE_SIZE/2, player.getPosition().y + TILE_SIZE/2, 0);

        float mapPixelW = farm.getWIDTH()  * TILE_SIZE;
        float mapPixelH = farm.getHEIGHT() * TILE_SIZE;

        cam.position.x = MathUtils.clamp(cam.position.x,
            cam.viewportWidth  /2, mapPixelW  - cam.viewportWidth /2);
        cam.position.y = MathUtils.clamp(cam.position.y,
            cam.viewportHeight /2, mapPixelH - cam.viewportHeight/2);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY() + (60); // Hard-Coded

            hoveredTile = farm.screenToTile(mouseX, mouseY, cam);
            Tile tile = farm.getTile(hoveredTile.getX(), hoveredTile.getY());

            if (tile != null) {
                System.out.println("Clicked Tile (x: " + tile.getX() + ", y: " + tile.getY() + ") - " + tile.getTexture());
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
        uiCam.setToOrtho(false, w, h);  // use true if you want (0,0) to be top-left
        uiCam.update();
    }

    @Override public void pause(){}  @Override public void resume(){}
    @Override public void show(){}   @Override public void hide(){}
    @Override public void dispose(){
        mapVisual.dispose();
        uiRenderer.dispose();
        shapeRenderer.dispose();
    }
}
