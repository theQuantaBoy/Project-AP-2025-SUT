package ap.project.visual;

import ap.project.model.enums.GameAnimationType;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class GameAnimation implements Pool.Poolable
{
    private Animation<TextureRegion> animation;
    private Vector2 position;
    private float stateTime;
    private float maxDuration;

    public GameAnimation()
    {
        this.position = new Vector2();
    }

    public GameAnimation(GameAnimationType type, Vector2 position)
    {
        this();
        init(type, position);
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

    public void init(GameAnimationType type, Vector2 position)
    {
        this.animation = type.getAnimation();
        this.position.set(position);
        this.maxDuration = animation.getAnimationDuration();
        this.stateTime = 0f;
    }

    @Override
    public void reset()
    {
        this.animation = null;
        this.position.set(0, 0);
        this.stateTime = 0f;
        this.maxDuration = 0f;
    }
}
