package ap.project.model.enums;

import com.badlogic.gdx.graphics.Texture;

public enum CharacterType
{
    ABIGAIL("Abigail", "characters/Abigail/Abigail_Sheet.atlas", "characters/Abigail/Abigail_Avatar.png"),
    ALEX("Alex", "characters/Alex/Alex_Sheet.atlas", "characters/Alex/Alex_Avatar.png"),
    ;

    private final String name;
    private final String spritesAddress;
    private final String avatarPath;
    private Texture avatarTexture;

    CharacterType(String name, String spritesAddress, String avatarPath)
    {
        this.name = name;
        this.spritesAddress = spritesAddress;
        this.avatarPath = avatarPath;
    }

    public String getName()
    {
        return name;
    }

    public String getSpritesAddress()
    {
        return spritesAddress;
    }

    public String getAvatarPath()
    {
        return avatarPath;
    }

    public Texture getAvatarTexture()
    {
        return avatarTexture;
    }

    public void setAvatarTexture(Texture avatarTexture)
    {
        this.avatarTexture = avatarTexture;
    }
}
