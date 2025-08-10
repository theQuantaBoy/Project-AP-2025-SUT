package ap.project.model.animal;

import com.badlogic.gdx.math.Rectangle;

public class MiniGameState {
    public static final float TRACK_WIDTH = 800f;
    public static final float TRACK_HEIGHT = 400f;
    public static final float PLAYER_BAR_WIDTH = 100f;
    public static final float PLAYER_BAR_HEIGHT = 60f;
    public static final float FISH_SIZE = 90f;
    public static final float MAX_CATCH_PROGRESS = 100f;
    public static final float INITIAL_PROGRESS = 25f; // Start with 25% progress

    private static final float BASE_CATCH_RATE = 20f; // Rate when fish is in bar
    private static final float BASE_LOSE_RATE = 10f;  // Rate when fish is not in bar

    private FishModel fish;
    private PlayerBarModel playerBar;
    private float catchProgressValue;
    private boolean isGameOver;
    private boolean playerWon;
    private boolean perfectCatch;
    private boolean trapBobberEquipped;

    public enum FishBehavior {
        SMOOTH(1.0f), SINKER(0.8f), FLOATER(0.8f), DART(1.2f), MIXED(1.1f);

        public final float difficultyModifier;

        FishBehavior(float difficultyModifier) {
            this.difficultyModifier = difficultyModifier;
        }
    }

    public MiniGameState(FishBehavior behavior, boolean trapBobberEquipped) {
        this.fish = new FishModel(behavior);
        this.playerBar = new PlayerBarModel();
        this.trapBobberEquipped = trapBobberEquipped;
        reset();
    }

    public void update(float delta) {
        if (isGameOver) return;

        // Update game elements
        fish.update(delta);

        // Check collision
        boolean fishInBar = isFishInBar();

        // Update perfect catch status
        if (!fishInBar && perfectCatch) {
            perfectCatch = false;
        }

        // Update progress based on fish position
        if (fishInBar) {
            // Fill progress faster when fish is in bar
            float catchRate = BASE_CATCH_RATE * fish.getBehavior().difficultyModifier; //TODO: random
            catchProgressValue += catchRate * delta;

            // Cap progress at max
            if (catchProgressValue >= MAX_CATCH_PROGRESS) {
                catchProgressValue = MAX_CATCH_PROGRESS;
                setGameOver(true, true);
            }
        } else {
            // Empty progress when fish is not in bar
            float loseRate = BASE_LOSE_RATE;
            if (trapBobberEquipped) {
                loseRate *= 0.7f; // Reduce escape rate with trap bobber
            }
            catchProgressValue -= loseRate * delta;

            // Check if progress is empty (fish escaped)
            if (catchProgressValue <= 0) {
                catchProgressValue = 0;
                setGameOver(true, false);
            }
        }
    }

    public void reset() {
        catchProgressValue = INITIAL_PROGRESS; // Start with some progress
        isGameOver = false;
        playerWon = false;
        perfectCatch = true;
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

    public boolean isFishInBar() {
        Rectangle fishRect = fish.getBounds();
        Rectangle barRect = playerBar.getBounds();
        return barRect.overlaps(fishRect);
    }

    private void setGameOver(boolean isGameOver, boolean playerWon) {
        this.isGameOver = isGameOver;
        this.playerWon = playerWon;
    }


}
