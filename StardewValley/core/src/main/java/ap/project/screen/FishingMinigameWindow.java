package ap.project.screen;

import ap.project.model.animal.MiniGameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class FishingMinigameWindow extends Window {
    private MiniGameState gameState;
    private Texture backgroundTexture;  // Fishing rod background
    private Texture fishTexture;        // Small fish texture
    private Texture playerBarTexture;   // Green bar for player

    private Stage privateStage;
    private boolean active = false;

    // Progress bar properties
    private Color progressBarColor = Color.GREEN;
    private float progressBarWidth = 300;
    private float progressBarHeight = 20;
    private float progressBarX, progressBarY;

    // Track properties
    private float trackWidth = 400;
    private float trackHeight = 300;
    private float trackX, trackY;

    public FishingMinigameWindow(Skin skin) {
        super("Fishing Minigame", skin);
        this.privateStage = new Stage(new ScreenViewport());
        initialize();
        setupUI();
    }

    private void initialize() {
        gameState = new MiniGameState(MiniGameState.FishBehavior.DART, true);
        backgroundTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\background.png");
        fishTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\fish_minigame.png");
        playerBarTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\bar.png");

        setModal(true);
        setMovable(false);
        setResizable(false);
        setFillParent(true);
    }

    private void setupUI() {
        Table content = new Table(getSkin());
        content.setFillParent(true);
        addActor(content);
        pack();
    }

    public void show() {
        active = true;
        gameState.reset();

        // Calculate positions based on window size
        trackX = (getWidth() - trackWidth) / 2;
        trackY = (getHeight() - trackHeight) / 2;
        progressBarX = (getWidth() - progressBarWidth) / 2;
        progressBarY = trackY + trackHeight + 50;
    }

    public void hide() {
        active = false;
    }

    public void update(float delta) {
        if (!active) return;

        // Handle player input
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        // Move player bar based on input
        if (upPressed) gameState.getPlayerBar().moveUp(delta);
        if (downPressed) gameState.getPlayerBar().moveDown(delta);

        // Update fish position
        gameState.getFish().update(delta);

        // Update game state
        gameState.update(delta);

        // Update progress bar color based on catch status
        if (gameState.isFishInBar()) {
            progressBarColor = Color.GREEN;
        } else {
            progressBarColor = Color.RED;
        }

        // Check for exit key
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            hide();
        }

        // Check if game is over
        if (gameState.isGameOver()) {
            if (gameState.didPlayerWin()) {
                if (gameState.isPerfectCatch()) {
                    System.out.println("Perfect catch!");
                } else {
                    System.out.println("Fish caught!");
                }
            } else {
                System.out.println("Fish escaped!");
            }
            hide();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!active) return;

        // Apply window's transform and draw its background
        super.draw(batch, parentAlpha);

        // Save current batch state
        batch.flush();
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        // Draw background texture (fishing rod)
        batch.draw(backgroundTexture,
            getX(), getY(),
            getWidth(), getHeight());

        // Draw track area
        batch.setColor(0.2f, 0.2f, 0.8f, 0.8f); // Semi-transparent blue
        batch.draw(backgroundTexture,
            trackX, trackY,
            trackWidth, trackHeight);
        batch.setColor(Color.WHITE);

        // Draw fish
        Rectangle fishBounds = gameState.getFish().getBounds();
        batch.draw(fishTexture,
            trackX + fishBounds.x,
            trackY + fishBounds.y,
            fishBounds.width,
            fishBounds.height);

        // Draw player bar in the middle of the track
        Rectangle barBounds = gameState.getPlayerBar().getBounds();
        batch.draw(playerBarTexture,
            trackX + barBounds.x,
            trackY + barBounds.y,
            barBounds.width,
            barBounds.height);

        // Draw progress bar
        float filledWidth = progressBarWidth * (float) gameState.getCatchProgress();
        batch.setColor(progressBarColor.r, progressBarColor.g, progressBarColor.b, 0.8f);
        batch.draw(playerBarTexture,
            progressBarX, progressBarY,
            filledWidth, progressBarHeight);

        // Restore batch state
        batch.setColor(Color.WHITE);
    }

    @Override
    public boolean remove() {
        backgroundTexture.dispose();
        fishTexture.dispose();
        playerBarTexture.dispose();
        privateStage.dispose();
        return super.remove();
    }

    public Stage getStage() {
        return privateStage;
    }

    public boolean isActive() {
        return active;
    }
}
