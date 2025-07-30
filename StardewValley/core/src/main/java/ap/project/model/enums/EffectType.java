package ap.project.model.enums;

import com.badlogic.gdx.graphics.Texture;

public enum EffectType
{
    PLAIN_TILE("plain tile", "effects/plain_tile.png"),
    PLOUGHED_TILE("ploughed tile", "effects/ploughed_tile.png"),
    FERTILIZED_TILE("fertilized tile", "effects/fertilized_tile.png"),
    WATERED_TILE("watered tile", "effects/watered_tile.png"),
    LIGHTNING_STROKE_TILE("lightning stroke tile", "effects/lightning_stroke_tile.png"),
    ;

    private final String name;
    private final String path;
    private Texture texture = null;

    EffectType(String name, String path)
    {
        this.name = name;
        this.path = path;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    public Texture getTexture()
    {
        return texture;
    }
}
