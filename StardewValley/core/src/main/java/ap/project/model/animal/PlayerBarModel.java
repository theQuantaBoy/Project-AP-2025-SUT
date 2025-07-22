package ap.project.model.animal;

// PlayerBarModel.java
// Represents the player-controlled bar. Handles its own physics.
// This code is also largely unchanged.

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PlayerBarModel {

    private float y;
    private float velocityY;
    private final float height;
    private final float trackHeight;

    // Physics constants
    private static final float GRAVITY = 1800.0f;
    private static final float FLAP_STRENGTH = -600.0f;

    public PlayerBarModel(float trackHeight) {
        this.trackHeight = trackHeight;
        this.height = 100f;
        this.y = 0; // Start at the bottom
        this.velocityY = 0;
    }

    public void reset() {
        y = 0;
        velocityY = 0;
    }
    public void update(float delta, boolean isFlapping) {
        if (isFlapping) {
            velocityY -= GRAVITY * 2.5f * delta;
        }
        velocityY += GRAVITY * delta;
        y += velocityY * delta;

        // Clamp position and velocity to stay within the track
        if (y < 0) {
            y = 0;
            velocityY = 0;
        }
        if (y > trackHeight - height) {
            y = trackHeight - height;
            velocityY = 0;
        }
    }

    public void flap() {
        velocityY = FLAP_STRENGTH;
    }

    public float getY() { return y; }

    /**
     * Gets the bounding box of the player bar.
     * @return A libGDX Rectangle representing the bar's position and size.
     */
    public Rectangle getBounds() {
        return new Rectangle(0, y, MiniGameState.TRACK_WIDTH, height);
    }
}

