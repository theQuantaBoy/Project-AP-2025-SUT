package ap.project.model.enums;

public enum CharacterType
{
    ABIGAIL("Abigail", "characters/Abigail/Abigail_Sheet.atlas", "characters/Abigail/Abigail_Avatar.png"),
    ;

    private final String name;
    private final String spritesAddress;
    private final String avatarPath;

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
}
