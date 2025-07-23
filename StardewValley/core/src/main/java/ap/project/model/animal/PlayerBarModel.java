package ap.project.model.animal;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PlayerBarModel {
    private float y;
    private static final float SPEED = 300f;

    public PlayerBarModel() {
        reset();
    }

    public void reset() {
        // Start at the bottom
        y = 0;
    }

    public void moveUp(float delta) {
        y += SPEED * delta;
        y = MathUtils.clamp(y, 0, MiniGameState.TRACK_HEIGHT - MiniGameState.PLAYER_BAR_HEIGHT);
    }

    public void moveDown(float delta) {
        y -= SPEED * delta;
        y = MathUtils.clamp(y, 0, MiniGameState.TRACK_HEIGHT - MiniGameState.PLAYER_BAR_HEIGHT);
    }

    public Rectangle getBounds() {
        // Player bar is always centered horizontally
        float x = (MiniGameState.TRACK_WIDTH - MiniGameState.PLAYER_BAR_WIDTH) / 2f;
        return new Rectangle(x, y, MiniGameState.PLAYER_BAR_WIDTH, MiniGameState.PLAYER_BAR_HEIGHT);
    }

    public float getY() {
        return y;
    }
}
