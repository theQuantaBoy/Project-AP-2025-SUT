package ap.project.model.enums;

import com.badlogic.gdx.graphics.Texture;

public enum BuffType
{
    MAX_ENERGY_100("max energy raised 100 levels for 5 hours", 5, "buffs/Max_Energy_Buff.png"),
    MAX_ENERGY_50("max energy raised 50 levels for 3 hours", 3, "buffs/Max_Energy_Buff.png"),
    FORAGING_11("improved Foraging skill for 11 hours", 11, "buffs/Foraging_Skill_Icon.png"),
    FORAGING_5("improved Foraging skill for 5 hours", 5, "buffs/Foraging_Skill_Icon.png"),
    FISHING_10("improved Fishing skill for 10 hours", 10, "buffs/Fishing_Skill_Icon.png"),
    FISHING_5("improved Fishing skill for 5 hours", 5, "buffs/Fishing_Skill_Icon.png"),
    FARMING("improved Farming skill for 5 hours", 5, "buffs/Farming_Skill_Icon.png"),
    MINING("improved Mining skill for 5 hours", 5, "buffs/Mining_Skill_Icon.png"),
    ;

    private final String description;
    private final int hourDuration;
    private final String texturePath;
    private Texture texture;

    BuffType(String name, int hourDuration, String texturePath)
    {
        this.description = name;
        this.hourDuration = hourDuration;
        this.texturePath = texturePath;
    }

    public String getDescription()
    {
        return description;
    }

    public int getHourDuration()
    {
        return hourDuration;
    }

    public String getTexturePath()
    {
        return texturePath;
    }

    public Texture getTexture()
    {
        return texture;
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }
}
