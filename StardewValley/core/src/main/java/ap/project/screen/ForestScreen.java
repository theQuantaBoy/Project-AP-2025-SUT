package ap.project.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public final class ForestScreen implements Screen {

    // ------------------------------------------------------------------------
    // constants (change these two numbers to taste)
    // ------------------------------------------------------------------------
    private static final float MAP_SCALE  = 1.0f;   // 1 = native 16‑px tiles, 3 = 48px, etc.
    private static final float CHAR_SCALE = 1.0f; // how big Abigail appears relative to map
    private static final float TILE_SIZE  = 24f * MAP_SCALE;
    private static final float PLAYER_SPEED = 50f * MAP_SCALE;
    private static final float FRAME_TIME = 0.12f;

    // ------------------------------------------------------------------------
    // map & camera
    // ------------------------------------------------------------------------
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera cam;

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

        walkDown  = createAnim(atlas, "Abigail_0_walk_front_");
        walkUp    = createAnim(atlas, "Abigail_0_walk_back_");
        walkLeft  = createAnim(atlas, "Abigail_0_walk_left_");
        walkRight = createAnim(atlas, "Abigail_0_walk_right_");

        currentAnim = walkDown;        // default facing
    }

    // helper: builds a 4‑frame looping animation
    private static Animation<TextureRegion> createAnim(TextureAtlas atlas,
                                                       String base) {
        Array<TextureRegion> frames = new Array<>(4);
        for (int i = 0; i < 4; i++) frames.add(atlas.findRegion(base + i));
        return new Animation<>(FRAME_TIME, frames, Animation.PlayMode.LOOP);
    }

    // ------------------------------------------------------------------------
    // render & update
    // ------------------------------------------------------------------------
    @Override
    public void render(float dt) {
        update(dt);

        cam.update();
        mapRenderer.setView(cam);
        mapRenderer.render();

        Batch batch = mapRenderer.getBatch();
        batch.begin();
        TextureRegion frame = currentAnim.getKeyFrame(stateTime);
        batch.draw(frame,
            pos.x, pos.y,
            0, 0,
            frame.getRegionWidth(), frame.getRegionHeight(),
            CHAR_SCALE, CHAR_SCALE,           // scale X / Y
            0);                               // rotation
        batch.end();
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

            pos.add(dx, dy);
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
    }

    @Override public void pause(){}  @Override public void resume(){}
    @Override public void show(){}   @Override public void hide(){}
    @Override public void dispose(){ map.dispose(); mapRenderer.dispose(); }
}
