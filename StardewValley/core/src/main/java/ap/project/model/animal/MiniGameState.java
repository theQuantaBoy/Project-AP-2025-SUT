package ap.project.model.animal;

// MiniGameState.java
// This class is the "Model". It holds all the data for the minigame state.
// It is independent of how the game is drawn or controlled.

import com.badlogic.gdx.math.Rectangle;

public class MiniGameState {

    // --- Game Constants (in world units) ---
    // These are public and static so other classes, like the DesktopLauncher, can access them.
    public static final float TRACK_WIDTH = 60f;
    public static final float TRACK_HEIGHT = 400f;
    public static final float PROGRESS_BAR_WIDTH = 30f;

    // --- Game State Variables ---
    private FishModel fish;
    private PlayerBarModel playerBar;
    private double catchProgress; // A value from 0.0 to 1.0
    private boolean isGameOver;
    private boolean playerWon;

    // Enum to define the possible movement patterns for the fish.
    public enum FishBehavior {
        SMOOTH, SINKER, FLOATER, DART, MIXED
    }

    /**
     * Constructor for MiniGameState.
     */
    public MiniGameState() {
        // You can change the behavior to test other fish types.
        this.fish = new FishModel(TRACK_HEIGHT, FishBehavior.SMOOTH);
        this.playerBar = new PlayerBarModel(TRACK_HEIGHT);
        this.catchProgress = 0.25; // Start with some progress
        this.isGameOver = false;
        this.playerWon = false;
    }

    // --- Getters and Setters ---
    // These methods provide controlled access to the game's state.

    public FishModel getFish() { return fish; }
    public PlayerBarModel getPlayerBar() { return playerBar; }
    public double getCatchProgress() { return catchProgress; }
    public boolean isGameOver() { return isGameOver; }
    public boolean didPlayerWin() { return playerWon; }

    public void setCatchProgress(double progress) {
        // Clamp the value between 0 and 1.
        this.catchProgress = Math.max(0.0, Math.min(1.0, progress));
    }

    public void setGameOver(boolean isGameOver, boolean playerWon) {
        this.isGameOver = isGameOver;
        this.playerWon = playerWon;
    }

    /**
     * Checks if the player's bar is currently overlapping with the fish.
     * Uses libGDX's Rectangle.overlaps() method for collision detection.
     * @return true if the bar and fish are colliding.
     */
    public boolean isFishInBar() {
        Rectangle fishRect = getFish().getBounds();
        Rectangle barRect = getPlayerBar().getBounds();
        return barRect.overlaps(fishRect);
    }
}

