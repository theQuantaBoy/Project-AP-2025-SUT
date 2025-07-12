package ap.project.model.animal;

// FishModel.java
// Represents the fish in the minigame. It handles its own position and movement logic.
// This code is framework-independent, containing only the fish's behavior rules.

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class FishModel {

    private float y; // Y-position of the fish
    private final float size = 30f; // The height and width of the fish icon
    private final float trackHeight;
    private final MiniGameState.FishBehavior behavior;

    // Variables for controlling different movement patterns
    private float sineWaveCounter = 0;
    private float targetY;
    private float dartTimer = 0;

    /**
     * Constructor for FishModel.
     * @param trackHeight The height of the fishing track.
     * @param behavior The movement pattern for this fish.
     */
    public FishModel(float trackHeight, MiniGameState.FishBehavior behavior) {
        this.trackHeight = trackHeight;
        this.behavior = behavior;
        this.y = trackHeight / 2f; // Start in the middle of the track
        this.targetY = this.y;
    }

    /**
     * Updates the fish's position based on its assigned behavior.
     * @param delta The time elapsed since the last frame (provided by libGDX's render loop).
     */
    public void update(float delta) {
        switch (behavior) {
            case SMOOTH:  updateSmooth(delta);  break;
            case SINKER:  updateSinker(delta);  break;
            case FLOATER: updateFloater(delta); break;
            case DART:    updateDart(delta);    break;
            case MIXED:   updateSmooth(delta);  break; // For this example, MIXED will just use the SMOOTH pattern
        }
        // Clamp the fish's Y-position to ensure it stays within the track boundaries.
        y = MathUtils.clamp(y, 0, trackHeight - size);
    }

    private void updateSmooth(float delta) {
        // Moves up and down in a gentle sine wave.
        sineWaveCounter += delta * 2.0f; // Adjust speed of oscillation
        float wave = MathUtils.sin(sineWaveCounter);
        // Move within the middle 70% of the track
        y = (trackHeight * 0.5f) + (wave * (trackHeight * 0.35f));
    }

    private void updateSinker(float delta) {
        // Tends to move downwards.
        targetY += (MathUtils.random() - 0.55f) * 20f; // Bias towards sinking
        moveTowardsTarget(delta * 50f); // Move towards the target Y
        if (y > trackHeight * 0.8f) targetY -= 20f; // Push it back up if it gets too low
    }

    private void updateFloater(float delta) {
        // Tends to move upwards.
        targetY += (MathUtils.random() - 0.45f) * 20f; // Bias towards floating
        moveTowardsTarget(delta * 50f); // Move towards the target Y
        if (y < trackHeight * 0.2f) targetY += 20f; // Push it back down if it gets too high
    }

    private void updateDart(float delta) {
        // Stays still, then quickly darts to a new random location.
        dartTimer -= delta;
        if (dartTimer <= 0) {
            // Pick a new random target position and a new timer
            targetY = MathUtils.random(0, trackHeight - size);
            dartTimer = MathUtils.random(0.5f, 1.0f); // Wait 0.5 to 1 second before darting again
        }
        moveTowardsTarget(delta * 150f); // Moves faster than other types
    }

    /** Helper method to move the fish towards its current targetY */
    private void moveTowardsTarget(float speed) {
        if (y < targetY) {
            y = Math.min(y + speed, targetY);
        }
        if (y > targetY) {
            y = Math.max(y - speed, targetY);
        }
    }

    public float getY() { return y; }

    /**
     * Gets the bounding box of the fish for collision detection.
     * @return A libGDX Rectangle representing the fish's current position and size.
     */
    public Rectangle getBounds() {
        // The fish is drawn in the center of the track's width.
        float xPos = (MiniGameState.TRACK_WIDTH - size) / 2f;
        return new Rectangle(xPos, y, size, size);
    }
}

