package ap.project.screen;

import ap.project.model.animal.MiniGameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class FishingMinigameWindow extends Window {
    public enum GameState {
        READY, START, RESULT
    }

    private GameState currentState = GameState.READY;
    private MiniGameState gameState;
    private Texture backgroundTexture;
    private Texture fishTexture;
    private Texture playerBarTexture;
    private Stage privateStage;
    private boolean active = false;
    private BitmapFont font;
    private String resultMessage = "";
    private float resultTimer = 0;
    private static final float RESULT_DISPLAY_TIME = 2.0f;
    private ProgressBar progressBar;
    private GlyphLayout glyphLayout = new GlyphLayout();

    // Game area dimensions
    private float gameWidth = 600;
    private float gameHeight = 400;
    private float fishSize = 60; // Increased fish size

    public FishingMinigameWindow(Skin skin) {
        super("", skin); // Empty title
        this.privateStage = new Stage(new ScreenViewport());
        this.font = new BitmapFont();

        initialize();
        setupUI();

        // Make window transparent
        setBackground((TextureRegionDrawable) null);
    }

    private void initialize() {
        gameState = new MiniGameState(MiniGameState.FishBehavior.DART, true);

        // Create solid color textures
        backgroundTexture = new Texture(Gdx.files.internal("animals/background.png"));
        fishTexture = new Texture(Gdx.files.internal("animals/fish_minigame.png"));
        playerBarTexture = new Texture(Gdx.files.internal("animals/bar.png"));
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void setupUI() {
        clearChildren();

        // Create progress bar
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = new TextureRegionDrawable(createColorTexture(Color.DARK_GRAY));
        progressBarStyle.knobBefore = new TextureRegionDrawable(createColorTexture(Color.GREEN));

        progressBar = new ProgressBar(0, 1, 0.01f, false, progressBarStyle);
        progressBar.setSize(30, gameHeight - 40);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Table gameArea = new Table();
        gameArea.setBackground(new TextureRegionDrawable(backgroundTexture));
        gameArea.pad(20).setSize(gameWidth, gameHeight);

        mainTable.add(gameArea).padRight(20);
        mainTable.add(progressBar).width(30).height(gameHeight - 40);

        addActor(mainTable);
    }

    public void show() {
        active = true;
        gameState.reset();
        currentState = GameState.READY;
        resultMessage = "";
        setVisible(true);
        toFront();

        // Center window on screen
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setPosition(0, 0);
    }

    public void update(float delta) {
        if (!active) return;

        switch (currentState) {
            case READY:
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    currentState = GameState.START;
                    gameState.reset();
                }
                break;

            case START:
                // Player controls
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    gameState.getPlayerBar().moveUp(delta);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    gameState.getPlayerBar().moveDown(delta);
                }

                gameState.getFish().update(delta);
                gameState.update(delta);
                progressBar.setValue((float) gameState.getCatchProgressValue());

                if (gameState.isGameOver()) {
                    resultMessage = gameState.didPlayerWin() ?
                        (gameState.isPerfectCatch() ? "Perfect catch!" : "Fish caught!") :
                        "Fish escaped!";
                    currentState = GameState.RESULT;
                    resultTimer = 0;
                }
                break;

            case RESULT:
                resultTimer += delta;
                if (resultTimer >= RESULT_DISPLAY_TIME || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    hide();
                }
                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!active) return;

        // Calculate positions (centered within game area)
        float gameAreaX = (getWidth() - gameWidth - 50) / 2; // Account for progress bar
        float gameAreaY = (getHeight() - gameHeight) / 2;

        // Draw game elements
        if (currentState != GameState.READY) {
            // Draw fish (larger size)
            Rectangle fishBounds = gameState.getFish().getBounds();
            batch.setColor(fishTexture.getTextureData().getFormat() == Pixmap.Format.RGB565 ? Color.RED : Color.WHITE);
            batch.draw(fishTexture,
                gameAreaX + fishBounds.x,
                gameAreaY + fishBounds.y,
                fishSize, fishSize); // Use larger size

            // Draw player bar
            Rectangle barBounds = gameState.getPlayerBar().getBounds();
            batch.setColor(playerBarTexture.getTextureData().getFormat() == Pixmap.Format.RGB565 ? Color.GREEN : Color.WHITE);
            batch.draw(playerBarTexture,
                gameAreaX + barBounds.x,
                gameAreaY + barBounds.y,
                barBounds.width, barBounds.height);
        }

        // Draw text messages
        batch.setColor(Color.WHITE);
        switch (currentState) {
            case READY:
                drawCenteredText(batch, "Press SPACE to start fishing!", getHeight() * 0.75f);
                break;
            case RESULT:
                drawCenteredText(batch, resultMessage, getHeight() / 2);
                break;
        }
    }

    private void drawCenteredText(Batch batch, String text, float y) {
        glyphLayout.setText(font, text);
        font.draw(batch, text,
            (getWidth() - glyphLayout.width) / 2,
            y);
    }

    @Override
    public boolean remove() {
        backgroundTexture.dispose();
        fishTexture.dispose();
        playerBarTexture.dispose();
        privateStage.dispose();
        font.dispose();
        return super.remove();
    }

    public void hide() {
        active = false;
        currentState = GameState.READY; // Reset to ready state
        resultMessage = ""; // Clear any result message
        setVisible(false); // Hide the window

        // Return input focus to the main game stage
        if (privateStage != null) {
            privateStage.unfocusAll();
        }

        // Optional: Notify parent screen that minigame closed
        if (getStage() != null) {
            getStage().unfocus(this);
        }
    }
}
