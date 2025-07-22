package ap.project.model.animal;

import com.badlogic.gdx.math.Rectangle;

public class MiniGameState {

    // Game constants
    public static final float TRACK_WIDTH = 300f;
    public static final float TRACK_HEIGHT = 400f;
    public static final float PROGRESS_BAR_WIDTH = 30f;
    private static final double BASE_CATCH_RATE = 0.3;
    private static final double BASE_LOSE_RATE = 0.2;
    private static final double TRAP_BOBBER_REDUCTION = 0.07;

    private FishModel fish;
    private PlayerBarModel playerBar;
    private double catchProgress;
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
        this.fish = new FishModel(TRACK_HEIGHT, behavior);
        this.playerBar = new PlayerBarModel(TRACK_HEIGHT);
        this.trapBobberEquipped = trapBobberEquipped;
        reset();
    }

    public void update(float delta, boolean isPlayerLifting) {
        if (isGameOver) return;

        playerBar.update(delta, isPlayerLifting);
        fish.update(delta);

        boolean fishInBar = isFishInBar();
        if (!fishInBar) perfectCatch = false;

        if (fishInBar) {
            double catchRate = BASE_CATCH_RATE * fish.getBehavior().difficultyModifier;
            catchProgress = Math.min(1.0, catchProgress + catchRate * delta);
        } else {
            double loseRate = trapBobberEquipped ?
                Math.max(0, BASE_LOSE_RATE - TRAP_BOBBER_REDUCTION) : BASE_LOSE_RATE;
            catchProgress = Math.max(0.0, catchProgress - loseRate * delta);
        }

        if (catchProgress >= 1.0) setGameOver(true, true);
        else if (catchProgress <= 0.0) setGameOver(true, false);
    }

    public void reset() {
        catchProgress = 0.25;
        isGameOver = false;
        playerWon = false;
        perfectCatch = true;
        fish.reset();
        playerBar.reset();
    }

    // Getters and other methods remain the same...
    public FishModel getFish() { return fish; }
    public PlayerBarModel getPlayerBar() { return playerBar; }
    public double getCatchProgress() { return catchProgress; }
    public boolean isGameOver() { return isGameOver; }
    public boolean didPlayerWin() { return playerWon; }
    public boolean isPerfectCatch() { return playerWon && perfectCatch; }
    public boolean isTrapBobberEquipped() { return trapBobberEquipped; }
    public boolean isFishInBar() { return playerBar.getBounds().overlaps(fish.getBounds()); }

    private void setGameOver(boolean isGameOver, boolean playerWon) {
        this.isGameOver = isGameOver;
        this.playerWon = playerWon;
    }

    public void setCatchProgress(double catchProgress) { this.catchProgress = catchProgress; }
}
