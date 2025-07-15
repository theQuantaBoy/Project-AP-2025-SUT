package ap.project.model.enums;

public enum CharacterType
{
    ABIGAIL("Abigail", "characters/Abigail/Abigail_Sheet.atlas"),
    ;

    private final String name;
    private final String spritesAddress;

    CharacterType(String name, String spritesAddress)
    {
        this.name = name;
        this.spritesAddress = spritesAddress;
    }

    public String getName()
    {
        return name;
    }

    public String getSpritesAddress()
    {
        return spritesAddress;
    }
}
