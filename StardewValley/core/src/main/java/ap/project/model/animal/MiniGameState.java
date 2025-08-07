package ap.project.model.animal;

import com.badlogic.gdx.math.Rectangle;

public class MiniGameState {
    public static final float TRACK_WIDTH = 400f;
    public static final float TRACK_HEIGHT = 300f;
    public static final float PLAYER_BAR_WIDTH = 100f;
    public static final float PLAYER_BAR_HEIGHT = 40f;
    public static final float FISH_SIZE = 60f;
    public static final float MAX_CATCH_PROGRESS = 100f;

    private static final double BASE_CATCH_RATE = 0.5;
    private static final double BASE_LOSE_RATE = 0.3;

    private FishModel fish;
    private PlayerBarModel playerBar;
    private double catchProgress;
    private float catchProgressValue;
    private boolean isGameOver;
    private boolean playerWon;
    private boolean perfectCatch;
    private boolean trapBobberEquipped;

    public enum FishBehavior {
        SMOOTH(1.0), SINKER(0.8), FLOATER(0.8), DART(1.2), MIXED(1.1);

        public final double difficultyModifier;

        FishBehavior(double difficultyModifier) {
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

        // Update progress
        if (fishInBar) {
            double catchRate = BASE_CATCH_RATE * fish.getBehavior().difficultyModifier;
            catchProgress += catchRate * delta;
            catchProgressValue += catchRate * delta * 20f; // For the visual progress bar
            catchProgressValue = Math.min(catchProgressValue, MAX_CATCH_PROGRESS);

            // Check if fish is caught
            if (catchProgressValue >= MAX_CATCH_PROGRESS) {
                setGameOver(true, true);
            }
        } else {
            double loseRate = BASE_LOSE_RATE;
            if (trapBobberEquipped) {
                loseRate *= 0.7; // Reduce escape rate with trap bobber
            }
            catchProgress -= loseRate * delta;
            catchProgressValue = (float) Math.max(0, catchProgressValue - loseRate * delta * 5f);
        }

        // Clamp progress between 0 and 1
        catchProgress = Math.max(0.0, Math.min(1.0, catchProgress));
    }

    public void reset() {
        catchProgress = 0.25;
        catchProgressValue = 0;
        isGameOver = false;
        playerWon = false;
        perfectCatch = true;
        fish.reset();
        playerBar.reset();
    }

    // Getters
    public FishModel getFish() { return fish; }
    public PlayerBarModel getPlayerBar() { return playerBar; }
    public double getCatchProgress() { return catchProgress; }
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
