package ap.project.screen;

import ap.project.model.enums.DayOfWeek;
import ap.project.model.enums.TileTexture;
import ap.project.model.game.Farm;
import ap.project.model.game.Tile;
import ap.project.model.game.Time;
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

public final class ForestScreen implements Screen {

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
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera cam;

    private final OrthographicCamera uiCam = new OrthographicCamera();

    // ------------------------------------------------------------------------
    // player
    // ------------------------------------------------------------------------
    private final Vector2                    pos = new Vector2();      // world‑space
    private final Animation<TextureRegion>   walkDown;
    private final Animation<TextureRegion>   walkUp;
    private final Animation<TextureRegion>   walkLeft;
    private final Animation<TextureRegion>   walkRight;
    private Animation<TextureRegion>         currentAnim;              // which to render
    private float                            stateTime = 0f;
    private final Texture shadowTexture;

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
    public ForestScreen() {

        // map (unitScale converts map px → world units)
        float unitScale = MAP_SCALE;                 // 1 tile = 16 * MAP_SCALE world units
        map = new TmxMapLoader().load("maps/Forest.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);

        // camera (shows 20×15 tiles *after* scaling)
        cam = new OrthographicCamera(20 * TILE_SIZE, 15 * TILE_SIZE);
        cam.setToOrtho(false);

    // start in map centre
        pos.set(60 * TILE_SIZE, 60 * TILE_SIZE);


        // --- player animations ---
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(
            "characters/Abigail/Abigail_Sheet.atlas"));
        atlas.getTextures().forEach(tex -> tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest));

        shadowTexture = new Texture(Gdx.files.internal("characters/shadow.png"));
        shadowTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // optional

        walkDown  = createAnim(atlas, "Abigail_0_walk_front_");
        walkUp    = createAnim(atlas, "Abigail_0_walk_back_");
        walkLeft  = createAnim(atlas, "Abigail_0_walk_left_");
        walkRight = createAnim(atlas, "Abigail_0_walk_right_");

        currentAnim = walkDown;        // default facing

        Tile[][] tiles = GameMapLoader.load("maps/Forest.tmx");
        farm = new Farm(tiles);

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
        mapRenderer.setView(cam);          // set view once, up‑front

        // ----- 1) MAP WITH (OPTIONAL) SHADER -----
        if (useAutumn && autumnShader.isCompiled()) {
            mapRenderer.getBatch().setShader(autumnShader);
        } else {
            mapRenderer.getBatch().setShader(null);
        }
        mapRenderer.render();              // draw only once
        mapRenderer.getBatch().setShader(null);   // reset for everything else

        // ----- 2) WORLD ACTORS -----
        Batch batch = mapRenderer.getBatch();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(shadowTexture, pos.x, pos.y - 3);
        TextureRegion frame = currentAnim.getKeyFrame(stateTime);
        batch.draw(currentAnim.getKeyFrame(stateTime), pos.x, pos.y,
            0, 0, /*w h*/           // …unchanged…
            frame.getRegionWidth(), frame.getRegionHeight(),
            CHAR_SCALE, CHAR_SCALE, 0);
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

        // -------- movement --------
        float dx = (Gdx.input.isKeyPressed(Keys.D) ? 1 : 0)
            - (Gdx.input.isKeyPressed(Keys.A) ? 1 : 0);
        float dy = (Gdx.input.isKeyPressed(Keys.W) ? 1 : 0)
            - (Gdx.input.isKeyPressed(Keys.S) ? 1 : 0);

        if (dx != 0 || dy != 0) {
            // normalise and scale
            float len = (float) Math.sqrt(dx*dx + dy*dy);
            dx = dx / len * PLAYER_SPEED * dt;
            dy = dy / len * PLAYER_SPEED * dt;

            // Preview the new position
            float nextX = pos.x + dx;
            float nextY = pos.y + dy;

            // Check target tile before moving
//            System.out.println("x: " + nextY + ", y: " + nextX);
//            System.out.println("next tile at " + (int)(nextX / 16) + (int) ((120 - nextY) / 16));

            int x = (int)(nextX / 16);
            int y = (int)((1912 - nextY) / 16);

            Tile target = farm.getTile(x, y);

            System.out.println("x: " + x + " - y: " + y);

            // Block movement if tile is LAKE
            if (target != null && (target.getTexture() == TileTexture.LAKE || target.getTexture() == TileTexture.UNPASSABLE
            || target.getTexture() == TileTexture.BUILDING || target.getTexture() == TileTexture.OBJECT)) {
                // blocked by water – don’t move
                return;
            }

//            System.out.println(nextX + " " + nextY);

//            System.out.println(target.getTexture().getName() + " at " + target.getX() + "," + target.getY());

            // Allowed tile – apply movement
            pos.set(nextX, nextY);
            stateTime += dt;


            // pick animation based on last direction pressed
            if (Math.abs(dx) > Math.abs(dy))    // horiz dominates
                currentAnim = dx > 0 ? walkRight : walkLeft;
            else
                currentAnim = dy > 0 ? walkUp   : walkDown;

        } else {
            // standing still → use first frame of current facing
            stateTime = 0f;
        }

        // -------- camera --------  keep centred but clamped to map bounds
        cam.position.set(pos.x + TILE_SIZE/2, pos.y + TILE_SIZE/2, 0);

        float mapPixelW = map.getProperties().get("width",  Integer.class) *
            map.getProperties().get("tilewidth",  Integer.class) * MAP_SCALE;
        float mapPixelH = map.getProperties().get("height", Integer.class) *
            map.getProperties().get("tileheight", Integer.class) * MAP_SCALE;

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
        map.dispose(); mapRenderer.dispose();
        clockTexture.dispose();
        handleUpTexture.dispose();
        handleDownTexture.dispose();
        handleMiddleTexture.dispose();
        journalAlertTexture.dispose();
        font.dispose();
    }
}
