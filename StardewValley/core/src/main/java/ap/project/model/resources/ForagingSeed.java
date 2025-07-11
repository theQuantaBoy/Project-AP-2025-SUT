package ap.project.model.resources;

import ap.project.model.game.GameObject;
import ap.project.model.enums.resources_enums.ForagingSeedType;
import ap.project.model.enums.Season;

import java.util.List;

public class ForagingSeed extends GameObject
{
    private final ForagingSeedType seedType;
    private final List<Season> seasons;

    public ForagingSeed(ForagingSeedType seedType)
    {
        this.seedType = seedType;
        this.seasons = seedType.getSeasons();
        this.ObjectType = seedType.getType();
    }

    @Override
    public Enum<?> getToolType()
    {
        return seedType;
    }
}
