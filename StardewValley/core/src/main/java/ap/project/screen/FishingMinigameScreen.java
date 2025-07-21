package ap.project.screen;

import ap.project.model.animal.MiniGameState;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class FishingMinigameScreen extends ScreenAdapter {

    private final Game game;
    private MiniGameState gameState;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private Texture backgroundTexture;
    private Texture fishTexture;
    private Texture playerBarTexture;

    private static final double CATCH_RATE = 0.3;
    private static final double BASE_LOSE_RATE = 0.2;
    private static final double TRAP_BOBBER_LOSE_RATE = 0.07;

    private enum State { READY, PLAYING, GAME_OVER }
    private State currentState;
    private float readyTimer;

    public FishingMinigameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // This method remains unchanged...
        gameState = new MiniGameState();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        glyphLayout = new GlyphLayout();
        backgroundTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\300px-Fishingicons-0 (1)-2.png");
        fishTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\300px-Fishingicons-0 (1)-0.png");
        playerBarTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\300px-Fishingicons-0 (1)-1.png");
        float totalWidth = 100f;
        float totalHeight = MiniGameState.TRACK_HEIGHT + 40;
        camera = new OrthographicCamera(totalWidth, totalHeight);
        camera.position.set(totalWidth / 2, totalHeight / 2, 0);
        camera.update();
        currentState = State.READY;
        readyTimer = 2.0f;
    }

    @Override
    public void render(float delta) {
        // --- 1. Update ---
        switch (currentState) {
            case READY:
                updateReady(delta);
                break;
            case PLAYING:
                // NOTE: handleInput() is no longer needed as the check is done directly in updateGame()
                updateGame(delta);
                if (gameState.isGameOver()) {
                    currentState = State.GAME_OVER;
                }
                break;
            case GAME_OVER:
                handleGameOverInput();
                break;
        }

        // --- 2. Drawing ---
        // This section remains unchanged...
        ScreenUtils.clear(new Color(0.81f, 0.91f, 0.99f, 1));
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        drawBackground();
        drawFish();
        drawPlayerBar();
        spriteBatch.end();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawProgressBar();
        shapeRenderer.end();
        spriteBatch.begin();
        if (currentState == State.READY) {
            drawReadyMessage();
        } else if (currentState == State.GAME_OVER) {
            drawGameOver();
        }
        spriteBatch.end();
    }

    // --- UPDATED: updateGame now handles input directly ---
    private void updateGame(float delta) {
        // Check for both SPACE and UP ARROW keys to control the bar
        boolean isLifting = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP);

        // Update the player bar with the new physics
        gameState.getPlayerBar().update(delta, isLifting);
        gameState.getFish().update(delta);

        boolean isFishInBar = gameState.isFishInBar();
        gameState.updatePerfectCatchStatus(isFishInBar);

        if (isFishInBar) {
            gameState.setCatchProgress(gameState.getCatchProgress() + CATCH_RATE * delta);
        } else {
            double currentLoseRate = gameState.isTrapBobberEquipped() ? TRAP_BOBBER_LOSE_RATE : BASE_LOSE_RATE;
            gameState.setCatchProgress(gameState.getCatchProgress() - currentLoseRate * delta);
        }

        if (gameState.getCatchProgress() >= 1.0) {
            gameState.setGameOver(true, true);
        } else if (gameState.getCatchProgress() <= 0.0) {
            gameState.setGameOver(true, false);
        }

        // Allow exiting the minigame
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //((com.badlogic.gdx.Game)Gdx.app.getApplicationListener()).setScreen(new WorldScreen(this.game));
        }
    }

    private void updateReady(float delta) {
        readyTimer -= delta;
        if (readyTimer <= 0) { currentState = State.PLAYING; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //((com.badlogic.gdx.Game)Gdx.app.getApplicationListener()).setScreen(new WorldScreen(this.game));
        }
    }

    private void handleGameOverInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ((com.badlogic.gdx.Game)Gdx.app.getApplicationListener()).setScreen(new FishingMinigameScreen(this.game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //((com.badlogic.gdx.Game)Gdx.app.getApplicationListener()).setScreen(new WorldScreen(this.game));
        }
    }

    // The rest of the file (drawing methods, dispose, etc.) remains unchanged.
    // ...
    private void drawGameOver() {
        String message;
        if (gameState.isPerfectCatch()) {
            message = "Perfect!";
            font.setColor(Color.YELLOW);
        } else if (gameState.didPlayerWin()) {
            message = "You caught it!";
            font.setColor(Color.WHITE);
        } else {
            message = "It got away...";
            font.setColor(Color.WHITE);
        }
        glyphLayout.setText(font, message);
        float textX = (camera.viewportWidth - glyphLayout.width) / 2f;
        float textY = (camera.viewportHeight + glyphLayout.height) / 2f;
        font.draw(spriteBatch, glyphLayout, textX, textY);
        font.setColor(Color.WHITE);
        String restartMessage = "Press SPACE to try again, or ESC for menu.";
        glyphLayout.setText(font, restartMessage);
        float restartX = (camera.viewportWidth - glyphLayout.width) / 2f;
        font.draw(spriteBatch, glyphLayout, restartX, textY - 30);
    }
    private void drawBackground() {
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

    }
    private void drawFish() {
        Rectangle fishBounds = gameState.getFish().getBounds();
        spriteBatch.draw(fishTexture, fishBounds.x + 20, fishBounds.y + 20, fishBounds.width, fishBounds.height);
    }
    private void drawPlayerBar() {
        Rectangle barBounds = gameState.getPlayerBar().getBounds();
        if (gameState.isFishInBar()) {
            spriteBatch.setColor(0.8f, 1f, 0.8f, 0.8f);
        } else {
            spriteBatch.setColor(1f, 1f, 1f, 0.7f);
        }
        spriteBatch.draw(playerBarTexture, barBounds.x + 20, barBounds.y + 20, barBounds.width, barBounds.height);
        spriteBatch.setColor(Color.WHITE);
    }
    private void drawProgressBar() {
        float barX = MiniGameState.TRACK_WIDTH + 30;
        float barY = 20;
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, MiniGameState.PROGRESS_BAR_WIDTH, MiniGameState.TRACK_HEIGHT);
        float progressHeight = (float) (gameState.getCatchProgress() * MiniGameState.TRACK_HEIGHT);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, MiniGameState.PROGRESS_BAR_WIDTH, progressHeight);
    }
    private void drawReadyMessage() {
        String message = "Ready...";
        glyphLayout.setText(font, message);
        float textX = (camera.viewportWidth - glyphLayout.width) / 2f;
        float textY = (camera.viewportHeight + glyphLayout.height) / 2f;
        font.draw(spriteBatch, glyphLayout, textX, textY);
    }
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
        backgroundTexture.dispose();
        fishTexture.dispose();
        playerBarTexture.dispose();
    }
}
