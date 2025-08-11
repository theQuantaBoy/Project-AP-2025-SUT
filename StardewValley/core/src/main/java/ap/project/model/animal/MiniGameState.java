package ap.project.model.animal;

import ap.project.model.enums.animal_enums.FishBehavior;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

import static ap.project.screen.FishingMinigameManager.MINIGAME_SCALE;

public class MiniGameState
{
    public static final float LOGICAL_WIDTH = 800f;
    public static final float LOGICAL_HEIGHT = 400f;
    public static final float FISH_SIZE = 50 * MINIGAME_SCALE;
    public static final float MAX_CATCH_PROGRESS = 100f;
    public static final float INITIAL_PROGRESS = 25f;
    public static final float MIN_Y = 0f;
    public static final float MAX_Y = LOGICAL_HEIGHT - FISH_SIZE;

    private static final float BASE_CATCH_RATE = 20f;
    private static final float BASE_LOSE_RATE = 10f;
    private static final float PERFECT_CATCH_THRESHOLD = 0.95f; // 95% of progress filled

    private FishModel fish;
    private PlayerBarModel playerBar;
    private float catchProgressValue;
    private boolean isGameOver;
    private boolean playerWon;
    private boolean perfectCatch = true;
    private boolean trapBobberEquipped;
    private boolean barOutOfBounds;
    private Random random = new Random();

    private float fishMoveTimer = 0;
    private static final float FISH_MOVE_INTERVAL = 0.1f;

    private float fishMovementTimer = 0;
    private static final float FISH_UPDATE_INTERVAL = 0.5f;

    private float MOVE_SCALE = 0.5f;

    public MiniGameState(FishBehavior behavior, boolean trapBobberEquipped)
    {
        this.fish = new FishModel(behavior);
        this.playerBar = new PlayerBarModel();
        this.trapBobberEquipped = trapBobberEquipped;
        reset();
    }

    public void update(float delta)
    {
        if (isGameOver) return;

        fishMoveTimer += delta;
        if (fishMoveTimer > FISH_MOVE_INTERVAL)
        {
            moveFish();
        }

        fishMovementTimer += delta;
        if (fishMovementTimer >= FISH_UPDATE_INTERVAL)
        {
            FishBehavior[] behaviors = FishBehavior.values();
            FishBehavior behavior = behaviors[(int)(Math.random() * behaviors.length)];
            fish.setBehavior(behavior);
            fishMovementTimer = 0;
        }

        updateBarPosition(delta);
        playerBar.update(delta);

        boolean fishInBar = isFishInBar();

        if (!fishInBar || barOutOfBounds)
        {
            if (perfectCatch)
            {
                System.out.println("not perfect any more");
            }
            perfectCatch = false;
        }

        // Update progress based on fish position
        if (fishInBar && !barOutOfBounds)
        {
            // Apply behavior-specific difficulty
            float catchRate = BASE_CATCH_RATE * getBehaviorDifficulty();
            catchProgressValue += catchRate * delta;

            // Cap progress at max
            if (catchProgressValue >= MAX_CATCH_PROGRESS)
            {
                catchProgressValue = MAX_CATCH_PROGRESS;
                setGameOver(true, true);
            }
        } else
        {
            // Empty progress when fish is not in bar or bar out of bounds
            float loseRate = BASE_LOSE_RATE;
            if (trapBobberEquipped)
            {
                loseRate *= 0.7f; // Reduce escape rate with trap bobber
            }
            catchProgressValue -= loseRate * delta;

            // Check if progress is empty (fish escaped)
            if (catchProgressValue <= 0)
            {
                catchProgressValue = 0;
                setGameOver(true, false);
            }
        }
    }

    private float getBehaviorDifficulty()
    {
        switch (fish.getBehavior())
        {
            case DART: return 0.7f;
            case SINKER: return 0.9f;
            case FLOATER: return 0.9f;
            case MIXED: return 1.0f;
            case SMOOTH: return 1.1f;
            default: return 1.0f;
        }
    }

    private void moveFish()
    {
        switch (fish.getBehavior())
        {
            case SMOOTH: applySmoothMovement(); break;
            case SINKER: applySinkerMovement(); break;
            case FLOATER: applyFloaterMovement(); break;
            case DART: applyDartMovement(); break;
            default: applyMixedMovement();
        }
        fish.setY(MathUtils.clamp(fish.getY(), MIN_Y, MAX_Y));
    }

    private void updateBarPosition(float delta)
    {
        Rectangle barRect = playerBar.getBounds();
        barOutOfBounds = (barRect.y < MIN_Y || barRect.y + barRect.height > LOGICAL_HEIGHT);
    }

    private void applyMixedMovement()
    {
        int move = random.nextInt(3);
        if (move == 0) fish.setY(fish.getY() + (5 * MOVE_SCALE));
        else if (move == 1) fish.setY(fish.getY() - (5 * MOVE_SCALE));
    }

    private void applySmoothMovement()
    {
        if (random.nextFloat() > 0.7f)
        {
            int move = random.nextInt(3);
            if (move == 0) fish.setY(fish.getY() + (5 * MOVE_SCALE));
            else if (move == 1) fish.setY(fish.getY() - (5 * MOVE_SCALE));
        }
    }

    private void applySinkerMovement()
    {
        if (random.nextFloat() > 0.2f)
        {
            fish.setY(fish.getY() - (5 * MOVE_SCALE));
        }
    }

    private void applyFloaterMovement()
    {
        if (random.nextFloat() > 0.2f)
        {
            fish.setY(fish.getY() + (5 * MOVE_SCALE));
        }
    }

    private void applyDartMovement()
    {
        int move = random.nextInt(3);
        if (move == 0) fish.setY(fish.getY() + (9 * MOVE_SCALE));
        else if (move == 1) fish.setY(fish.getY() - (9 * MOVE_SCALE));
    }

    public void reset()
    {
        catchProgressValue = INITIAL_PROGRESS;
        isGameOver = false;
        playerWon = false;
        perfectCatch = true;
        barOutOfBounds = false;
        fish.reset();
        playerBar.reset();
    }

    // Getters
    public FishModel getFish() { return fish; }
    public PlayerBarModel getPlayerBar() { return playerBar; }
    public float getCatchProgressValue() { return catchProgressValue; }
    public boolean isGameOver() { return isGameOver; }
    public boolean didPlayerWin() { return playerWon; }
    public boolean isPerfectCatch() { return playerWon && perfectCatch; }
    public boolean isTrapBobberEquipped() { return trapBobberEquipped; }
    public boolean isBarOutOfBounds() { return barOutOfBounds; }

    public boolean isFishInBar()
    {
        Rectangle fishRect = fish.getBounds();
        Rectangle barRect = playerBar.getBounds();

        float buffer = 5f;
        return fishRect.overlaps(barRect) ||
            (fishRect.y + fishRect.height - buffer > barRect.y &&
                fishRect.y + buffer < barRect.y + barRect.height);
    }

    private void setGameOver(boolean isGameOver, boolean playerWon)
    {
        this.isGameOver = isGameOver;
        this.playerWon = playerWon;
    }
}
