package ap.project.model.animal;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PlayerBarModel {

    private float y;
    private float velocityY;
    private final float height;
    private final float trackHeight;

    // --- NEW: Updated physics for better control ---
    private static final float GRAVITY = -2800f; // Constant downward pull
    private static final float LIFT_FORCE = 5000f; // Upward force when key is pressed

    public PlayerBarModel(float trackHeight) {
        this.trackHeight = trackHeight;
        this.height = 100f;
        this.y = 0; // Start at the bottom
        this.velocityY = 0;
    }

    /**
     * Updates the bar's position based on physics.
     * @param delta The time elapsed since the last frame.
     * @param isLifting True if the player is holding the button to go up.
     */
    public void update(float delta, boolean isLifting) {
        // By default, only gravity is acting on the bar
        float acceleration = GRAVITY;

        // If the player is holding the key, add the lift force
        if (isLifting) {
            acceleration += LIFT_FORCE;
        }

        // Apply acceleration to velocity
        this.velocityY += acceleration * delta;

        // Apply velocity to position
        this.y += this.velocityY * delta;

        // Clamp position to stay within the track and reset velocity at the boundaries
        if (y < 0) {
            y = 0;
            velocityY = 0;
        }
        if (y > trackHeight - height) {
            y = trackHeight - height;
            velocityY = 0;
        }
    }

    /** The flap() method is no longer used in this control scheme. */
    public void flap() {
        // This method is now empty.
    }

    public float getY() { return y; }

    public Rectangle getBounds() {
        return new Rectangle(0, y, MiniGameState.TRACK_WIDTH, height);
    }
}
