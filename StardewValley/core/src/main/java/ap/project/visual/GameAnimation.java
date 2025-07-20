package ap.project.visual;

import ap.project.model.enums.GameAnimationType;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GameAnimation
{
    private final Animation<TextureRegion> animation;
    private final Vector2 position;
    private float stateTime;
    private final float maxDuration;

    public GameAnimation(GameAnimationType type, Vector2 position)
    {
        this.animation = type.createAnimation();
        this.position = position;
        this.maxDuration = animation.getAnimationDuration();
        this.stateTime = 0f;
    }

    public boolean isFinished()
    {
        return stateTime >= maxDuration;
    }

    public void update(float delta)
    {
        stateTime += delta;
    }

    public void render(Batch batch)
    {
        TextureRegion frame = animation.getKeyFrame(stateTime, false);
        batch.draw(frame, position.x, position.y);
    }
}
