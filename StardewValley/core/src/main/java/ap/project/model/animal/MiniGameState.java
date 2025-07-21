package ap.project.model.animal;

import com.badlogic.gdx.math.Rectangle;

public class MiniGameState {

    public static final float TRACK_WIDTH = 60f;
    public static final float TRACK_HEIGHT = 400f;
    public static final float PROGRESS_BAR_WIDTH = 30f;

    private FishModel fish;
    private PlayerBarModel playerBar;
    private double catchProgress;
    private boolean isGameOver;
    private boolean playerWon;

    // --- NEW: Logic for Perfect Catch and Trap Bobber ---
    private boolean fishNeverLeftBar;
    private boolean trapBobberEquipped;

    public enum FishBehavior {
        SMOOTH, SINKER, FLOATER, DART, MIXED
    }

    public MiniGameState() {
        // You can change the behavior here to test. MIXED is now more interesting.
        this.fish = new FishModel(TRACK_HEIGHT, FishBehavior.MIXED);
        this.playerBar = new PlayerBarModel(TRACK_HEIGHT);
        this.catchProgress = 0.25;
        this.isGameOver = false;
        this.playerWon = false;

        // --- NEW: Initialize new game state variables ---
        this.fishNeverLeftBar = true; // Start by assuming a perfect catch
        this.trapBobberEquipped = true; // Set to true for testing. In a real game, this would depend on player equipment.
    }

    // --- Getters ---
    public FishModel getFish() { return fish; }
    public PlayerBarModel getPlayerBar() { return playerBar; }
    public double getCatchProgress() { return catchProgress; }
    public boolean isGameOver() { return isGameOver; }
    public boolean didPlayerWin() { return playerWon; }

    // --- NEW: Getters for new states ---
    public boolean isPerfectCatch() { return didPlayerWin() && fishNeverLeftBar; }
    public boolean isTrapBobberEquipped() { return trapBobberEquipped; }


    public void setCatchProgress(double progress) {
        this.catchProgress = Math.max(0.0, Math.min(1.0, progress));
    }

    public void setGameOver(boolean isGameOver, boolean playerWon) {
        this.isGameOver = isGameOver;
        this.playerWon = playerWon;
    }

    // --- NEW: Method to update the perfect catch status ---
    public void updatePerfectCatchStatus(boolean fishIsInBar) {
        if (!fishIsInBar) {
            this.fishNeverLeftBar = false;
        }
    }

    public boolean isFishInBar() {
        Rectangle fishRect = getFish().getBounds();
        Rectangle barRect = getPlayerBar().getBounds();
        return barRect.overlaps(fishRect);
    }
}
