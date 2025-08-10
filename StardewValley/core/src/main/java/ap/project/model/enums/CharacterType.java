package ap.project.model.enums;

import com.badlogic.gdx.graphics.Texture;

public enum CharacterType
{
    ABIGAIL("Abigail", "characters/Abigail/Abigail_Sheet.atlas",
        "characters/Abigail/Abigail_Avatar.png", "characters/Abigail/Abigail_Profile.png"),

    ALEX("Alex", "characters/Alex/Alex_Sheet.atlas",
        "characters/Alex/Alex_Avatar.png", "characters/Alex/Alex_Profile.png"),

    HARVEY("Harvey", "characters/npc/Harvey/Harvey_Sheet.atlas",
        "characters/npc/Harvey/Harvey_Avatar.png", "characters/npc/Harvey/Harvey_Profile.png"),

    LEAH("Leah", "characters/npc/Leah/Leah_Sheet.atlas",
        "characters/npc/Leah/Leah_Avatar.png", "characters/npc/Leah/Leah_Profile.png"),

    ROBIN("Robin", "characters/npc/Robin/Robin_Sheet.atlas",
        "characters/npc/Robin/Robin_Avatar.png", "characters/npc/Robin/Robin_Profile.png"),

    SEBASTIAN("Sebastian", "characters/npc/Sebastian/Sebastian_Sheet.atlas",
        "characters/npc/Sebastian/Sebastian_Avatar.png", "characters/npc/Sebastian/Sebastian_Profile.png"),
    ;

    private final String name;
    private final String spritesAddress;
    private final String avatarPath;
    private final String profilePath;
    private Texture avatarTexture;

    CharacterType(String name, String spritesAddress, String avatarPath, String profilePath)
    {
        this.name = name;
        this.spritesAddress = spritesAddress;
        this.avatarPath = avatarPath;
        this.profilePath = profilePath;
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

    public String getProfilePath()
    {
        return profilePath;
    }
}
