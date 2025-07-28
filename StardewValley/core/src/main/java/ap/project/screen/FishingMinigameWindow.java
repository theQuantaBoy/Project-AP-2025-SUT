package ap.project.screen;

import ap.project.model.animal.MiniGameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class FishingMinigameWindow extends Window {
    private MiniGameState gameState;
    private Texture backgroundTexture;
    private Texture fishTexture;
    private Texture playerBarTexture;

    public FishingMinigameWindow(Skin skin) {
        super("Fishing Minigame", skin);
        initialize();
        setupUI();
    }

    private void initialize() {
        gameState = new MiniGameState();
        backgroundTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\background.png");
        fishTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\fish_minigame.png");
        playerBarTexture = new Texture("C:\\Users\\ArmanPC\\IdeaProjects\\advanced-programming-phase-1-group-26\\StardewValley\\core\\assets\\animals\\bar.png");

        setModal(true);
        setMovable(false);
        setResizable(false);
    }

    private void setupUI() {
        Table content = new Table(getSkin());
        // Add your minigame UI components here
        add(content).expand().fill();
        pack();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Draw game elements
        drawBackground(batch);
        drawFish(batch);
        drawPlayerBar(batch);
    }

    private void drawBackground(Batch batch) {
        batch.draw(backgroundTexture, getX(), getY(), getWidth(), getHeight());
    }

    private void drawFish(Batch batch) {
        Rectangle fishBounds = gameState.getFish().getBounds();
        batch.draw(fishTexture,
            getX() + fishBounds.x,
            getY() + fishBounds.y,
            fishBounds.width,
            fishBounds.height);
    }

    private void drawPlayerBar(Batch batch) {
        Rectangle barBounds = gameState.getPlayerBar().getBounds();
        batch.draw(playerBarTexture,
            getX() + barBounds.x,
            getY() + barBounds.y,
            barBounds.width,
            barBounds.height);
    }

    public void update(float delta) {
        // Update game state
        gameState.getPlayerBar().update(delta, Gdx.input.isKeyPressed(Input.Keys.SPACE));
        gameState.getFish().update(delta);

        // Check win/lose conditions
        if (gameState.isFishInBar()) {
            gameState.setCatchProgress(gameState.getCatchProgress() + 0.3 * delta);
        } else {
            gameState.setCatchProgress(gameState.getCatchProgress() - 0.2 * delta);
        }
    }

    @Override
    public boolean remove() {
        // Clean up resources
        backgroundTexture.dispose();
        fishTexture.dispose();
        playerBarTexture.dispose();
        return super.remove();
    }
}
