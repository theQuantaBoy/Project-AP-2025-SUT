package ap.project.model.enums;

import com.badlogic.gdx.graphics.Texture;

public enum ReactionEmojis
{
    ;

    private final Texture texture;

    ReactionEmojis(String path)
    {
        this.texture = new Texture(path);
    }

    public Texture getTexture()
    {
        return texture;
    }
}
