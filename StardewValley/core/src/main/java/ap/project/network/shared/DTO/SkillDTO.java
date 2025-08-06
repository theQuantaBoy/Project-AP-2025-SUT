package ap.project.network.shared.DTO;

import ap.project.model.enums.SkillType;
import ap.project.model.player_data.Skill;

public class SkillDTO
{
    public boolean initialized;

    public SkillType type;
    public int level;
    public int unit;

    public SkillDTO() {}

    public SkillDTO(Skill skill)
    {
        if (skill != null)
        {
            this.type = skill.getType();
            this.level = skill.getLevel();
            this.unit = skill.getUnit();

            this.initialized = true;
        } else
        {
            this.initialized = false;
        }
    }
}
