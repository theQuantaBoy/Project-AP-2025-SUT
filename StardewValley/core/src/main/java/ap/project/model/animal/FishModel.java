package ap.project.model.animal;

import ap.project.model.enums.animal_enums.FishBehavior;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class FishModel {
    private float x, y;
    private FishBehavior behavior;

    public FishModel(FishBehavior behavior) {
        this.behavior = behavior;
        reset();
    }

    public void reset() {
        // Center horizontally, random vertical position
        x = (MiniGameState.LOGICAL_WIDTH - MiniGameState.FISH_SIZE) / 2;
        y = MiniGameState.MIN_Y + 5f;
    }

    public void update(float delta) {
        // Movement logic now handled by MiniGameState
    }

    public Rectangle getBounds()
    {
        return new Rectangle(x, y, MiniGameState.FISH_SIZE, MiniGameState.FISH_SIZE);
    }

    public FishBehavior getBehavior() {
        return behavior;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = MathUtils.clamp(y, MiniGameState.MIN_Y,
            MiniGameState.MAX_Y - MiniGameState.FISH_SIZE);
    }

    public void setBehavior(FishBehavior behavior)
    {
        this.behavior = behavior;
    }
}
