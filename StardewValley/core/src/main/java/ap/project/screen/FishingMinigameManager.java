package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.animal.Fish;
import ap.project.model.animal.MiniGameState;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.animal_enums.FishBehavior;
import ap.project.model.enums.animal_enums.FishType;
import ap.project.model.game.Player;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class FishingMinigameManager
{
    public enum GameState
    {
        READY, START, RESULT
    }

    // Unified scale factor
    public static float MINIGAME_SCALE = 0.7f;

    // Positions
    public static final float BAR_HORIZONTAL_POS = 0.5f;
    public static final float RESULT_DISPLAY_TIME = 2.0f;
    public static final float BACKGROUND_PADDING = 20f;

    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private GameState currentState = GameState.READY;
    private MiniGameState gameState;
    private FishType caughtFishType;
    private Texture backgroundTexture;
    private Texture fishTexture;
    private Texture barTexture;
    private Texture crownTexture;
    private boolean active = false;
    private String resultMessage = "";
    private float resultTimer = 0;
    private boolean perfectCatch = false;
    private Table gameLayout;

    // Game area actors
    private Image backgroundImage;
    private Image fishImage;
    private Image barImage;
    private Image crownImage;
    private Group gameAreaGroup;

    // Game area dimensions
    private float gameAreaWidth;
    private float gameAreaHeight;

    // Progress bar properties
    private float progressBarWidth = 12;
    private float progressBarHeight = 530;
    private float progressBarValue = 0;

    private Image specialIndicatorImage;
    private boolean sonarBobber = false;

    public FishingMinigameManager(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        window = new Window("Fishing", skin)
        {
            @Override
            public void draw(Batch batch, float parentAlpha)
            {
                super.draw(batch, parentAlpha);

                if (active && currentState != GameState.READY)
                {
                    drawProgressBar(batch);
                }
            }
        };

        window.setVisible(false);
        window.setMovable(false);

        initializeTextures();
        setupUI();
        stage.addActor(window);
    }

    private void initializeTextures()
    {
        backgroundTexture = new Texture(Gdx.files.internal("minigame/background.png"));
        fishTexture = new Texture(Gdx.files.internal("minigame/fish_minigame.png"));
        barTexture = new Texture(Gdx.files.internal("minigame/bar.png"));
        crownTexture = new Texture(Gdx.files.internal("minigame/crown.png"));

        // Create image actors
        backgroundImage = new Image(backgroundTexture);
        fishImage = new Image(fishTexture);
        barImage = new Image(barTexture);
        crownImage = new Image(crownTexture);

        // Random fish behavior
        FishBehavior[] behaviors = FishBehavior.values();
        FishBehavior behavior = behaviors[(int)(Math.random() * behaviors.length)];
        gameState = new MiniGameState(behavior, true);
    }

    private void setupUI()
    {
        window.clear();
        window.pad(15);

        // Create game area group for precise positioning
        gameAreaGroup = new Group();
        gameAreaGroup.addActor(backgroundImage);
        gameAreaGroup.addActor(barImage);
        gameAreaGroup.addActor(fishImage);
        gameAreaGroup.addActor(crownImage);

        if (specialIndicatorImage != null && sonarBobber)
        {
            gameAreaGroup.addActor(specialIndicatorImage);
        }

        // Main layout table
        Table mainTable = new Table();
        window.add(mainTable).grow();

        // Game area layout with padding
        gameLayout = new Table();
        gameLayout.pad(BACKGROUND_PADDING);
        gameLayout.add(gameAreaGroup).grow();

        mainTable.add(gameLayout).grow().row();

        resizeUI();
    }

    private void resizeUI()
    {
        // Base dimensions at scale 1.0
        float baseGameWidth = 300f;
        float baseGameHeight = 850f;

        // Apply scale factor
        gameAreaWidth = baseGameWidth * MINIGAME_SCALE;
        gameAreaHeight = baseGameHeight * MINIGAME_SCALE;

        // Set game area size
        gameAreaGroup.setSize(gameAreaWidth, gameAreaHeight);
        backgroundImage.setSize(gameAreaWidth, gameAreaHeight);

        // Set texture sizes
        float fishWidth = 50 * MINIGAME_SCALE;
        float fishHeight = 50 * MINIGAME_SCALE;
        fishImage.setSize(fishWidth, fishHeight);

        float barWidth = 50 * MINIGAME_SCALE;
        float barHeight = 200 * MINIGAME_SCALE;
        barImage.setSize(barWidth, barHeight);

        float crownWidth = 54 * MINIGAME_SCALE;
        float crownHeight = 48 * MINIGAME_SCALE;
        crownImage.setSize(crownWidth, crownHeight);

        float indicatorSize = 48 * MINIGAME_SCALE;
        if (specialIndicatorImage != null && sonarBobber)
        {
            specialIndicatorImage.setSize(indicatorSize, indicatorSize);
        }

        // Set window size with extra space for padding
        window.pack();
        window.setSize(
                gameAreaWidth + 100 + (BACKGROUND_PADDING * 2),
                gameAreaHeight + 100 + (BACKGROUND_PADDING * 2)
        );

        // Center window
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        window.setPosition(
                (screenWidth - window.getWidth()) / 2,
                (screenHeight - window.getHeight()) / 2
        );
    }

    public void showMinigame(FishType fishType)
    {
        this.caughtFishType = fishType;
        active = true;
        perfectCatch = false;
        gameState.reset();
        currentState = GameState.READY;
        progressBarValue = 0;

        specialIndicatorImage = new Image(fishType.getType().getTexture());

        sonarBobber = App.getCurrentGame().getCurrentPlayer().hasEnoughInInventory(GameObjectType.SONAR_BOBBER, 1);
        specialIndicatorImage.setVisible(sonarBobber);

        updateGameElements();

        setupUI();

        window.setVisible(true);
        window.toFront();
    }

    public void update(float delta)
    {
        if (!active) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
        {
            hideMinigame();
            return;
        }

        switch (currentState)
        {
            case READY:
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                {
                    currentState = GameState.START;
                    gameState.reset();
                }
                break;

            case START:
                if (Gdx.input.isKeyPressed(Input.Keys.UP))
                {
                    gameState.getPlayerBar().moveUp(delta);
                }

                if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                {
                    gameState.getPlayerBar().moveDown(delta);
                }

                gameState.update(delta);
                progressBarValue = gameState.getCatchProgressValue();

                // Update UI positions
                updateGameElements();

                // Check for perfect catch
                perfectCatch = gameState.isPerfectCatch();

                if (gameState.isGameOver())
                {
                    if (gameState.didPlayerWin())
                    {
                        resultMessage = perfectCatch ? "Perfect catch!" : "Fish caught!";
                        Player player = App.getCurrentGame().getCurrentPlayer();
                        Fish fish = new Fish(caughtFishType);
                        if (perfectCatch)
                        {
                            fish.increaseQuality();
                            player.getFishingSkill().setUnit((player.getFishingSkill().getUnit() + 1) * 2.4f);
                        }
                        player.addToInventory(fish);
                    } else
                    {
                        resultMessage = "Fish escaped!";
                    }

                    updateGameElements();

                    UIRenderer.showTextBox(resultMessage);
                    currentState = GameState.RESULT;
                    resultTimer = 0;
                }
                break;

            case RESULT:
                resultTimer += delta;
                if (resultTimer >= RESULT_DISPLAY_TIME || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                {
                    hideMinigame();
                }
                break;
        }
    }

    private void updateGameElements()
    {
        if (gameState == null) return;

        // Get the fish and bar positions from the game state
        Rectangle fishBounds = gameState.getFish().getBounds();
        Rectangle barBounds = gameState.getPlayerBar().getBounds();

        // Calculate scaling factors
        float scaleY = gameAreaHeight / MiniGameState.LOGICAL_HEIGHT;

        // Position fish relative to background
        float fishX = ((gameAreaWidth - fishImage.getWidth()) / 2) + 15;
        float fishY = (fishBounds.y * scaleY) + 42;
        fishImage.setPosition(fishX, fishY);

        // Position bar relative to background
        float barX = ((gameAreaWidth - barImage.getWidth()) / 2) + 15;
        float barY = (barBounds.y * scaleY) + 42;
        barImage.setPosition(barX, barY);

        // Position crown relative to fish
        if (isLegendaryFish())
        {
            crownImage.setVisible(true);
            float crownX = fishX + (fishImage.getWidth() - crownImage.getWidth()) / 2;
            float crownY = fishY + fishImage.getHeight();
            crownImage.setPosition(crownX, crownY);
        } else
        {
            crownImage.setVisible(false);
        }

        if (specialIndicatorImage != null && sonarBobber)
        {
            System.out.println("yes");
            float indicatorX = gameAreaWidth - specialIndicatorImage.getWidth() + 40;
            float indicatorY = gameAreaHeight - specialIndicatorImage.getHeight() - 10;
            specialIndicatorImage.setPosition(indicatorX, indicatorY);
        }
    }

    private void drawProgressBar(Batch batch)
    {
        // Calculate progress bar position relative to window
        float barX = window.getX() + window.getWidth() - progressBarWidth - 109;
        float barY = (window.getY() + (window.getHeight() - progressBarHeight) / 2) + 5;

        // Calculate filled portion based on progress
        float filledHeight = progressBarHeight * (progressBarValue / MiniGameState.MAX_CATCH_PROGRESS);

        // End batch so we can use ShapeRenderer
        batch.end();

        // Enable blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Set up shape renderer
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw background (empty portion)
        shapeRenderer.setColor(0.0f, 0.0f, 0.0f, 0.0f);
        shapeRenderer.rect(barX, barY, progressBarWidth, progressBarHeight);

        // Draw filled portion (progress level)
        float progressPercent = progressBarValue / MiniGameState.MAX_CATCH_PROGRESS;
        if (progressPercent > 0.7f)
        {
            shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 0.8f); // Green when high
        } else if (progressPercent > 0.3f)
        {
            shapeRenderer.setColor(0.8f, 0.8f, 0.2f, 0.8f); // Yellow when medium
        } else
        {
            shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 0.8f); // Red when low
        }
        shapeRenderer.rect(barX, barY, progressBarWidth, filledHeight);

        shapeRenderer.end();

        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1); // White border
        shapeRenderer.rect(barX, barY, progressBarWidth, progressBarHeight);
        shapeRenderer.end();

        // Disable blending
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Restart batch for other UI drawing
        batch.begin();
    }

    public void hideMinigame()
    {
        active = false;
        currentState = GameState.READY;
        perfectCatch = false;
        window.setVisible(false);
    }

    public boolean isActive()
    {
        return active;
    }

    public void dispose()
    {
        backgroundTexture.dispose();
        fishTexture.dispose();
        barTexture.dispose();
        crownTexture.dispose();
        shapeRenderer.dispose();
        window.remove();
    }

    private boolean isLegendaryFish()
    {
        return caughtFishType != null && caughtFishType.isLegendary();
    }
}
