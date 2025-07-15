package ap.project.model.game;

import ap.project.model.enums.CharacterType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.EnumMap;

public abstract class AbstractCharacter
{
    protected final CharacterType type;
    protected final Vector2 position;
    protected final Texture shadow;
    protected final EnumMap<Direction, Animation<TextureRegion>> animations;
    protected Animation<TextureRegion> currentAnimation;
    protected float stateTime = 0f;

    public AbstractCharacter(CharacterType type, Vector2 spawnPoint)
    {
        this.type = type;
        this.position = new Vector2(spawnPoint);

        TextureAtlas atlas = new TextureAtlas(type.getSpritesAddress());
        atlas.getTextures().forEach(tex -> tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest));

        this.shadow = new Texture("characters/shadow.png");
        this.shadow.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.animations = new EnumMap<>(Direction.class);
        animations.put(Direction.UP,    createAnim(atlas, type.getName() + "_0_walk_up_"));
        animations.put(Direction.DOWN,  createAnim(atlas, type.getName() + "_0_walk_down_"));
        animations.put(Direction.LEFT,  createAnim(atlas, type.getName() + "_0_walk_left_"));
        animations.put(Direction.RIGHT, createAnim(atlas, type.getName() + "_0_walk_right_"));

        this.currentAnimation = animations.get(Direction.DOWN);
    }

    private Animation<TextureRegion> createAnim(TextureAtlas atlas, String base)
    {
        Array<TextureRegion> frames = new Array<>(4);
        for (int i = 0; i < 4; i++)
        {
            frames.add(atlas.findRegion(base + i));
        }
        return new Animation<>(0.16f, frames, Animation.PlayMode.LOOP);
    }

    public void setDirection(Direction dir)
    {
        currentAnimation = animations.get(dir);
    }

    public void updateAnimation(float delta)
    {
        stateTime += delta;
    }

    public void resetAnimation()
    {
        stateTime = 0f;
    }

    public TextureRegion getCurrentFrame()
    {
        return currentAnimation.getKeyFrame(stateTime);
    }

    public Vector2 getPosition()
    {
        return position;
    }

    public Texture getShadow()
    {
        return shadow;
    }

    public enum Direction
    {
        UP, DOWN, LEFT, RIGHT
    }
}
