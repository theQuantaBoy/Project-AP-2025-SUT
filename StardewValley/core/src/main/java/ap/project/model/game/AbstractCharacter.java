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
    protected float maxEnergy = 100f;
    protected float currentEnergy = maxEnergy;

    protected final String nickName;
    protected float maxWidth;
    protected float maxHeight;

    protected final Texture avatar;

    protected boolean isMoving = false;

    public AbstractCharacter(CharacterType type, Vector2 spawnPoint, String nickName)
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

        this.avatar = new Texture(type.getAvatarPath());

        this.currentAnimation = animations.get(Direction.DOWN);
        this.nickName = nickName;

        maxHeight = 0;
        maxWidth = 0;
    }

    private Animation<TextureRegion> createAnim(TextureAtlas atlas, String base)
    {
        Array<TextureRegion> frames = new Array<>(4);
        for (int i = 0; i < 4; i++)
        {
            TextureRegion frame = atlas.findRegion(base + i);
            if (frame != null) {
                frames.add(frame);
                maxWidth = Math.max(maxWidth, frame.getRegionWidth());
                maxHeight = Math.max(maxHeight, frame.getRegionHeight());
            }
        }
        return new Animation<>(0.24f, frames, Animation.PlayMode.LOOP);
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

    public void decreaseEnergy(float amount) {
        currentEnergy = Math.max(0, currentEnergy - amount);
    }

    public void restoreEnergy(float amount) {
        currentEnergy = Math.min(maxEnergy, currentEnergy + amount);
    }

    public float getEnergyPercentage() {
        return currentEnergy / maxEnergy;
    }

    public float getCurrentEnergy() {
        return currentEnergy;
    }

    public enum Direction
    {
        UP, DOWN, LEFT, RIGHT
    }

    public String getNickName()
    {
        return nickName;
    }

    public float getMaxWidth()
    {
        return maxWidth;
    }

    public float getMaxHeight()
    {
        return maxHeight;
    }

    public void setPosition(Vector2 position)
    {
        this.position.set(position);
    }

    public Texture getAvatar()
    {
        return avatar;
    }

    public Direction getDirection()
    {
        for (java.util.Map.Entry<Direction, Animation<TextureRegion>> entry : animations.entrySet())
        {
            if (entry.getValue() == currentAnimation)
            {
                return entry.getKey();
            }
        }
        return Direction.DOWN; // Default if not found
    }

    public boolean isMoving()
    {
        return isMoving;
    }

    public void setMoving(boolean moving)
    {
        this.isMoving = moving;
    }
}
