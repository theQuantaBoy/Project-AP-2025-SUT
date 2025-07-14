package ap.project.screen;// FishingMinigameScreen.java
// This class implements the libGDX Screen interface.
// It handles all rendering, input, and game logic updates for the minigame.

import ap.project.model.animal.MiniGameState;
import com.badlogic.gdx.Game;
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

    private final Game game; // A reference back to the main game class to allow screen switching
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
     * Constructor now takes a Game instance so it can switch screens.
     * @param game The main game class instance.
     */
    public FishingMinigameScreen(Game game) {
        this.game = game;
    }

    /**
     * Called when this screen becomes the current screen. Used for initialization.
     */
    @Override
    public void show() {
        // This resets the game state every time the screen is shown.
        gameState = new MiniGameState();

        // Initialize rendering objects
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont(); // Uses libGDX's default Arial font
        glyphLayout = new GlyphLayout();

        // Set up the camera to view our game world at a consistent size
        float totalWidth = MiniGameState.TRACK_WIDTH + MiniGameState.PROGRESS_BAR_WIDTH + 50;
        float totalHeight = MiniGameState.TRACK_HEIGHT + 40;
        camera = new OrthographicCamera(totalWidth, totalHeight);
        camera.position.set(totalWidth / 2, totalHeight / 2, 0);
        camera.update();
    }

    /**
     * Called every frame by the application. This is the main game loop for this screen.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        // --- 1. Handle Input and Update Logic ---
        if (!gameState.isGameOver()) {
            handleInput(delta);
            updateGame(delta);
        } else {
            // After the game ends, check for input to restart or go back to the menu.
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                game.setScreen(((FishingGame) game).fishingScreen); // Restart by resetting the screen
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(((FishingGame) game).menuScreen); // Go back to the menu
            }
        }

        // --- 2. Clear the Screen ---
        ScreenUtils.clear(new Color(0.81f, 0.91f, 0.99f, 1)); // A pleasant light blue

        // --- 3. Render the Game Shapes ---
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawTrack();
        drawPlayerBar();
        drawFish();
        drawProgressBar();
        shapeRenderer.end();

        // --- 4. Render Text ---
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        if (gameState.isGameOver()){
            drawGameOver();
        }
        spriteBatch.end();
    }

    private void handleInput(float delta) {
        boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        // Check if the key was just pressed on this frame
        if (isSpacePressed && !wasSpacePressed) {
            gameState.getPlayerBar().flap();
        }
        wasSpacePressed = isSpacePressed;

        // Allow the player to exit to the menu at any time by pressing Escape.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(((FishingGame) game).menuScreen);
        }
    }

    private void updateGame(float delta) {
        boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        gameState.getPlayerBar().update(delta, isSpacePressed);
        gameState.getFish().update(delta);

        // Update the catch progress based on whether the fish is in the bar
        if (gameState.isFishInBar()) {
            gameState.setCatchProgress(gameState.getCatchProgress() + CATCH_RATE * delta);
        } else {
            gameState.setCatchProgress(gameState.getCatchProgress() - LOSE_RATE * delta);
        }

        // Check for win/loss conditions
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
        // The bar changes color when it's successfully on the fish
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
        float textX = (camera.viewportWidth - glyphLayout.width) / 2f;
        float textY = (camera.viewportHeight + glyphLayout.height) / 2f;
        font.draw(spriteBatch, glyphLayout, textX, textY);

        String restartMessage = "Press SPACE to restart, or ESC for menu.";
        glyphLayout.setText(font, restartMessage);
        float restartX = (camera.viewportWidth - glyphLayout.width) / 2f;
        font.draw(spriteBatch, glyphLayout, restartX, textY - 30);
    }


    /**
     * Called when this screen is no longer the current screen for a Game.
     * We dispose of resources here to prevent memory leaks.
     */
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }
}
