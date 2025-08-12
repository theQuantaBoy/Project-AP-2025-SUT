package ap.project.screen;

import ap.project.model.animal.Fish;
import ap.project.model.animal.MiniGameState;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.animal_enums.FishBehavior;
import ap.project.model.enums.animal_enums.FishType;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.visual.UIRenderer;
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
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.Random;

public class FishingMinigameManager extends Window {
    public enum GameState {
        READY, START, RESULT
    }

    private static final float HORIZONTAL_OFFSET = 25f;
    private static final float RESULT_DISPLAY_TIME = 2.0f;

    private FishType caughtFishType;

    private GameState currentState = GameState.READY;
    private MiniGameState gameState;
    private Texture backgroundTexture;
    private TextureRegion fishRegion;
    private TextureRegion playerBarRegion;
    private ProgressBar catchProgressBar;
    private boolean active = false;
    private BitmapFont font;
    private String resultMessage = "";
    private float resultTimer = 0;
    private GlyphLayout glyphLayout = new GlyphLayout();

    private float fishAspectRatio;
    private float barAspectRatio;

    // Game area dimensions
    private float gameWidth = 800;
    private float gameHeight = 600;

    public FishingMinigameManager(Skin skin) {
        super("", skin);
        this.font = new BitmapFont();
        initialize();
        setupUI();
        setBackground((TextureRegionDrawable) null);
        setVisible(false);
    }

    private void initialize() {
        FishBehavior behavior = null;
        for (int i = 0; i < FishBehavior.values().length; i++) {
            Random random = new Random();
            int r = random.nextInt(FishBehavior.values().length);
            behavior = FishBehavior.values()[r];
        }
        gameState = new MiniGameState(behavior, true);
        backgroundTexture = new Texture(Gdx.files.internal("animals/background.png"));
        fishRegion = new TextureRegion(new Texture(Gdx.files.internal("animals/fish_minigame.png")));
        playerBarRegion = new TextureRegion(new Texture(Gdx.files.internal("animals/bar.png")));

        // Calculate aspect ratios
        fishAspectRatio = (float) fishRegion.getRegionWidth() / fishRegion.getRegionHeight();
        barAspectRatio = (float) playerBarRegion.getRegionWidth() / playerBarRegion.getRegionHeight();
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

        // Create vertical progress bar for catch progress
        ProgressBar.ProgressBarStyle catchStyle = new ProgressBar.ProgressBarStyle();
        catchStyle.background = new TextureRegionDrawable(createColorTexture(Color.DARK_GRAY));
        catchStyle.knobBefore = new TextureRegionDrawable(createColorTexture(Color.BLUE));

        // Wider vertical progress bar (80px wide)
        catchProgressBar = new ProgressBar(0, MiniGameState.MAX_CATCH_PROGRESS, 1f, true, catchStyle);
        catchProgressBar.setSize(80, gameHeight - 40); // Wider bar

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Table gameArea = new Table();
        gameArea.setBackground(new TextureRegionDrawable(backgroundTexture));
        gameArea.pad(20).setSize(gameWidth, gameHeight);

        // Layout: Progress bar | Game area
        mainTable.add(catchProgressBar).width(80).height(gameHeight - 40).padRight(10);
        mainTable.add(gameArea).width(gameWidth).height(gameHeight).padRight(10);

        addActor(mainTable);
    }

    public void showMinigame(FishType fishType) {
        this.caughtFishType = fishType;
        active = true;
        gameState.reset();
        currentState = GameState.READY;
        resultMessage = "";
        setVisible(true);
        toFront();
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

                // Update progress bar
                catchProgressBar.setValue(gameState.getCatchProgressValue());

                // Update progress bar color
                float progress = gameState.getCatchProgressValue() / MiniGameState.MAX_CATCH_PROGRESS;
                ProgressBar.ProgressBarStyle style = catchProgressBar.getStyle();
                if (progress > 0.7f) {
                    style.knobBefore = new TextureRegionDrawable(createColorTexture(Color.GREEN));
                } else if (progress > 0.3f) {
                    style.knobBefore = new TextureRegionDrawable(createColorTexture(Color.YELLOW));
                } else {
                    style.knobBefore = new TextureRegionDrawable(createColorTexture(Color.RED));
                }

                if (gameState.isGameOver()) {
                    if (gameState.didPlayerWin()) {
                        resultMessage = gameState.isPerfectCatch() ? "Perfect catch!" : "Fish caught!"; //TODO
                        Player player = WorldScreen.getInstance().getCurrentPlayer();
                        Fish fish = new Fish(caughtFishType);
                        player.addToInventory(fish);


                        if (gameState.isPerfectCatch()) {
                            player.getFishingSkill().setUnit((int) (player.getFishingSkill().getUnit() * 2.4));
                            fish.setPrice((int) (fish.getPrice() * 2.4));
                        }
                        player.increaseMoney(fish.getPrice());

                    } else {
                        resultMessage = "Fish escaped!";
                    }
                    currentState = GameState.RESULT;
                    resultTimer = 0;
                }
                break;

            case RESULT:
                resultTimer += delta;
                if (resultTimer >= RESULT_DISPLAY_TIME || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    hideMinigame();
                }
                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!active) return;

        float gameAreaX = (getWidth() - gameWidth - 50) / 2;
        float gameAreaY = (getHeight() - gameHeight) / 2;

        if (currentState != GameState.READY) {
            // Draw fish centered horizontally
            Rectangle fishBounds = gameState.getFish().getBounds();
            float fishHeight = MiniGameState.FISH_SIZE;
            float fishWidth = fishHeight * fishAspectRatio;

            batch.draw(fishRegion,
                gameAreaX + fishBounds.x,
                gameAreaY + fishBounds.y,
                fishWidth, fishHeight);

            // Draw player bar with automatic sizing
            Rectangle barBounds = gameState.getPlayerBar().getBounds();
            float barHeight = MiniGameState.PLAYER_BAR_HEIGHT;
            float barWidth = barHeight * barAspectRatio;

            batch.draw(playerBarRegion,
                gameAreaX + barBounds.x,
                gameAreaY + barBounds.y,
                barWidth, barHeight);
        }

        batch.setColor(Color.WHITE);
        switch (currentState) {
            case READY:
                Player player = WorldScreen.getInstance().getCurrentPlayer();
                if (player.getInventory().contains(new GameObject(GameObjectType.SONAR_BOBBER, 1))) {
                    UIRenderer.showTextBox("Press SPACE to start fishing" + caughtFishType.getDisplayName());
                } else {
                    UIRenderer.showTextBox("Press SPACE to start fishing!");
                }
                break;
            case RESULT:
                UIRenderer.showTextBox(resultMessage);
                if (gameState.didPlayerWin()) {
                    UIRenderer.showTextBox("You caught a fish!");
                } else {
                    UIRenderer.showTextBox("The fish got away!");
                }
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
        fishRegion.getTexture().dispose();
        playerBarRegion.getTexture().dispose();
        font.dispose();
        return super.remove();
    }

    public void hideMinigame() {
        active = false;
        currentState = GameState.READY;
        resultMessage = "";
        setVisible(false);
    }

    public boolean isActive() {
        return active;
    }

    public boolean didPlayerWin() {
        return gameState.didPlayerWin();
    }
}
