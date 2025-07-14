package ap.project.screen;

import ap.project.control.CharacterController;
import ap.project.graphics.CharacterRenderer;
import ap.project.model.enums.CharacterType;
import ap.project.model.enums.DayOfWeek;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.TileTexture;
import ap.project.model.game.*;
import ap.project.util.GameMapLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import javax.sound.midi.Soundbank;

public final class TestScreen implements Screen {

    // ------------------------------------------------------------------------
    // constants (change these two numbers to taste)
    // ------------------------------------------------------------------------
    private static final float MAP_SCALE  = 1.0f;   // 1 = native 16‑px tiles, 3 = 48px, etc.
    private static final float CHAR_SCALE = 1f; // how big Abigail appears relative to map
    private static final float TILE_SIZE  = 24f * MAP_SCALE;
    private static final float PLAYER_SPEED = 50f * MAP_SCALE;
    private static final float FRAME_TIME = 0.16f;

    // ------------------------------------------------------------------------
    // map & camera
    // ------------------------------------------------------------------------

    private final OrthographicCamera cam;
    private final MapVisual mapVisual;


    private final OrthographicCamera uiCam = new OrthographicCamera();

    // ------------------------------------------------------------------------
    // player
    // ------------------------------------------------------------------------
    private final PlayerCharacter player;
    private final CharacterController characterController;
    private final CharacterRenderer characterRenderer;

    private final Farm farm;

    private Texture clockTexture;
    private Texture handleUpTexture, handleDownTexture, handleMiddleTexture;
    private Texture journalAlertTexture;
    private BitmapFont font;
    private final Time time = new Time(); // or get it from elsewhere if shared

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private ShaderProgram autumnShader;
    private boolean useAutumn = false;   // toggle if you want seasons

    // ------------------------------------------------------------------------
    // ctor
    // ------------------------------------------------------------------------
    public TestScreen() {

        // map (unitScale converts map px → world units)
        float unitScale = MAP_SCALE;                 // 1 tile = 16 * MAP_SCALE world units

        // camera (shows 20×15 tiles *after* scaling)
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

        farm = new Farm(MapTypes.FOREST);
        mapVisual = farm.getMapVisual();

        Vector2 spawn = new Vector2(60 * TILE_SIZE, 60 * TILE_SIZE);
        player = new PlayerCharacter(CharacterType.ABIGAIL, spawn);
        characterController = new CharacterController(player, farm, PLAYER_SPEED, TILE_SIZE);
        characterRenderer = new CharacterRenderer();

        clockTexture         = new Texture(Gdx.files.internal("clock/clock.png"));
        handleUpTexture      = new Texture(Gdx.files.internal("clock/handle_up.png"));
        handleDownTexture    = new Texture(Gdx.files.internal("clock/handle_down.png"));
        handleMiddleTexture  = new Texture(Gdx.files.internal("clock/handle_middle.png"));
        journalAlertTexture  = new Texture(Gdx.files.internal("clock/journal_alert.png"));

        clockTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        handleUpTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        handleDownTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        handleMiddleTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        journalAlertTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Load your pixel font
        font = new BitmapFont(Gdx.files.internal("fonts/Stardew_Valley.fnt"));
        font.getRegion().getTexture()
            .setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // keep pixels crisp

        ShaderProgram.pedantic = false;  // suppress strict‑mode warnings
        autumnShader = new ShaderProgram(
            Gdx.files.internal("default.vert"),   // or "shaders/autumn.vert" if you have one
            Gdx.files.internal("fall.frag"));
        if (!autumnShader.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + autumnShader.getLog());
        }
    }

    // helper: builds a 4‑frame looping animation
    private static Animation<TextureRegion> createAnim(TextureAtlas atlas,
                                                       String base) {
        Array<TextureRegion> frames = new Array<>(4);
        for (int i = 0; i < 4; i++) frames.add(atlas.findRegion(base + i));
        return new Animation<>(FRAME_TIME, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void render (float dt) {
        update(dt);

        cam.update();

        mapVisual.render(cam, useAutumn ? autumnShader : null);

        // ----- 2) WORLD ACTORS -----
        Batch batch = mapVisual.getRenderer().getBatch();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        characterRenderer.render(batch, player, CHAR_SCALE);
        batch.end();

        // (optional overlay here)
        Gdx.gl.glEnable(GL20.GL_BLEND);          // 1️⃣ allow transparency
        shapeRenderer.setProjectionMatrix(uiCam.combined);   // 2️⃣ screen‑space overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.7f);            // 30 % opaque black
        shapeRenderer.rect(0, 0, uiCam.viewportWidth, uiCam.viewportHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);



        // ----- 3) UI -----
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        drawClockUI(batch);
        batch.end();
    }

    private void drawClockUI(Batch batch) {
        int screenW = (int) uiCam.viewportWidth;
        int screenH = (int) uiCam.viewportHeight;

        int clockX = screenW - clockTexture.getWidth() - 10;
        int clockY = screenH - clockTexture.getHeight() - 10;

        batch.draw(clockTexture, clockX - 20, clockY);
        batch.draw(handleDownTexture, clockX - 20, clockY);
        batch.draw(handleMiddleTexture, clockX - 22, clockY + 4);
        batch.draw(handleUpTexture, clockX - 20, clockY + 8);
        batch.draw(journalAlertTexture, clockX, clockY - 55);

        String dayText = DayOfWeek.getShortDayOfWeek((time.getDay() - 1) % 7) + " " + time.getDay();
        String timeText = time.getHour() + ":00" + ((time.getHour() >= 12) ? " pm" : " am");
        String moneyText = "500000";

        font.getData().setScale(1.5f);

        font.setColor(Color.BLACK);
        font.draw(batch, dayText, clockX + 130, clockY + 210);
        font.draw(batch, timeText, clockX + 115, clockY + 120);

        font.setColor(Color.RED);
        font.draw(batch, moneyText, clockX + 64, clockY + 38);
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
    }

    // ------------------------------------------------------------------------
    // boilerplate
    // ------------------------------------------------------------------------
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
        clockTexture.dispose();
        handleUpTexture.dispose();
        handleDownTexture.dispose();
        handleMiddleTexture.dispose();
        journalAlertTexture.dispose();
        font.dispose();
    }
}
