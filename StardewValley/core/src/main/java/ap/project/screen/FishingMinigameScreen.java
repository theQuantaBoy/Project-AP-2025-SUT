package ap.project.screen;// FishingMinigameScreen.java
// This class implements the libGDX Screen interface.
// It handles all rendering, input, and game logic updates for the minigame.

import ap.project.model.animal.MiniGameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;


public class FishingMinigameScreen extends ScreenAdapter {

    // --- Game Logic ---
    private MiniGameState gameState;

    // --- Rendering & Camera ---
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout glyphLayout; // Used to measure text for centering

    // --- Game Logic Constants ---
    private static final double CATCH_RATE = 0.3; // Progress per second when catching
    private static final double LOSE_RATE = 0.2;  // Progress lost per second

    private boolean wasSpacePressed = false;

    /**
     * Called when this screen becomes the current screen.
     */
    @Override
    public void show() {
        gameState = new MiniGameState();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont(); // A default font
        glyphLayout = new GlyphLayout();

        // Set up the camera to view our game world
        float totalWidth = MiniGameState.TRACK_WIDTH + MiniGameState.PROGRESS_BAR_WIDTH + 50;
        float totalHeight = MiniGameState.TRACK_HEIGHT + 40;
        camera = new OrthographicCamera(totalWidth, totalHeight);
        camera.position.set(totalWidth / 2, totalHeight / 2, 0);
        camera.update();
    }

    /**
     * Called every frame by the application. This is the main game loop.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        // --- 1. Handle Input and Update Logic ---
        if (!gameState.isGameOver()) {
            handleInput(delta);
            updateGame(delta);
        }

        // --- 2. Clear the Screen ---
        ScreenUtils.clear(new Color(0.81f, 0.91f, 0.99f, 1)); // Light blue background

        // --- 3. Render the Game ---
        // Use the ShapeRenderer for drawing geometric shapes.
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawTrack();
        drawPlayerBar();
        drawFish();
        drawProgressBar();

        shapeRenderer.end();

        // Use the SpriteBatch for drawing text (and textures later).
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        if (gameState.isGameOver()){
            drawGameOver();
        }

        spriteBatch.end();
    }

    private void handleInput(float delta) {
        boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if (isSpacePressed && !wasSpacePressed) {
            gameState.getPlayerBar().flap();
        }
        wasSpacePressed = isSpacePressed;
    }

    private void updateGame(float delta) {
        boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        gameState.getPlayerBar().update(delta, isSpacePressed);
        gameState.getFish().update(delta);

        if (gameState.isFishInBar()) {
            gameState.setCatchProgress(gameState.getCatchProgress() + CATCH_RATE * delta);
        } else {
            gameState.setCatchProgress(gameState.getCatchProgress() - LOSE_RATE * delta);
        }

        if (gameState.getCatchProgress() >= 1.0) {
            gameState.setGameOver(true, true); // Win
        } else if (gameState.getCatchProgress() <= 0.0) {
            gameState.setGameOver(true, false); // Loss
        }
    }

    // --- Drawing Methods ---

    private void drawTrack() {
        shapeRenderer.setColor(new Color(0.3f, 0.58f, 0.83f, 1)); // Water blue
        shapeRenderer.rect(20, 20, MiniGameState.TRACK_WIDTH, MiniGameState.TRACK_HEIGHT);
    }

    private void drawFish() {
        Rectangle fishBounds = gameState.getFish().getBounds();
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.rect(fishBounds.x + 20, fishBounds.y + 20, fishBounds.width, fishBounds.height);
    }

    private void drawPlayerBar() {
        Rectangle barBounds = gameState.getPlayerBar().getBounds();
        Color barColor = gameState.isFishInBar() ? new Color(0.47f, 0.95f, 0.56f, 0.7f) : new Color(0.95f, 0.47f, 0.47f, 0.7f);

        // Enable blending for transparency
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(barColor);
        shapeRenderer.rect(barBounds.x + 20, barBounds.y + 20, barBounds.width, barBounds.height);
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }

    private void drawProgressBar() {
        float barX = MiniGameState.TRACK_WIDTH + 30;
        // Background
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(barX, 20, MiniGameState.PROGRESS_BAR_WIDTH, MiniGameState.TRACK_HEIGHT);
        // Fill
        float progressHeight = (float) (gameState.getCatchProgress() * MiniGameState.TRACK_HEIGHT);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, 20, MiniGameState.PROGRESS_BAR_WIDTH, progressHeight);
    }

    private void drawGameOver() {
        String message = gameState.didPlayerWin() ? "You caught it!" : "It got away...";
        glyphLayout.setText(font, message);
        float textX = (Gdx.graphics.getWidth() - glyphLayout.width) / 2f;
        float textY = (Gdx.graphics.getHeight() + glyphLayout.height) / 2f;

        // This is a bit of a hack for positioning text without a proper scene graph.
        // For a real game, you'd use a Scene2D Stage.
        font.draw(spriteBatch, glyphLayout, textX, textY);
    }

    @Override
    public void dispose() {
        // Dispose of resources to prevent memory leaks
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }
}
