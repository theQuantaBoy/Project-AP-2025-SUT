package ap.project.model.animal;

import ap.project.Main;
import ap.project.screen.FishingMinigameManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import static ap.project.screen.FishingMinigameManager.MINIGAME_SCALE;

public class PlayerBarModel {
    private float y;
    private static final float SPEED = 300f;
    private boolean hitTopBound = false;
    private boolean hitBottomBound = false;

    private float velocity = 0f;
    private static final float GRAVITY = 2000f;
    private static final float MAX_SPEED = 600f;

    public PlayerBarModel() {
        reset();
    }

    public void update(float delta)
    {
        // Apply stronger gravity
        velocity = GRAVITY * delta;

        // Clamp velocity
        velocity = MathUtils.clamp(velocity, -MAX_SPEED, MAX_SPEED);

        // Update position
        y -= velocity * delta;

        // Handle bounds
        if (y < MiniGameState.MIN_Y)
        {
            y = MiniGameState.MIN_Y;
            velocity = 0;
        } else if (y > MiniGameState.MAX_Y - (200 * MINIGAME_SCALE))
        {
            y = MiniGameState.MAX_Y - (200 * MINIGAME_SCALE);
            velocity = 0;
        }
    }

    public void reset()
    {
        // Start at the bottom
        y = 0;
        hitTopBound = false;
        hitBottomBound = false;
    }

    public void moveUp(float delta)
    {
        float newY = y + SPEED * delta;
        if (newY > MiniGameState.MAX_Y - (200 * MINIGAME_SCALE))
        {
            y = MiniGameState.MAX_Y - (50 * MINIGAME_SCALE);
            hitTopBound = true;
        } else
        {
            y = newY;
            hitTopBound = false;
        }
    }

    public void moveDown(float delta) {
        float newY = y - SPEED * delta;
        if (newY < MiniGameState.MIN_Y) {
            y = MiniGameState.MIN_Y;
            hitBottomBound = true;
        } else {
            y = newY;
            hitBottomBound = false;
        }
    }

    public Rectangle getBounds()
    {
        float x = MiniGameState.LOGICAL_WIDTH * FishingMinigameManager.BAR_HORIZONTAL_POS;
        return new Rectangle(x, y, (200 * MINIGAME_SCALE), (50 * MINIGAME_SCALE));
    }

    public float getY() {
        return y;
    }
}
