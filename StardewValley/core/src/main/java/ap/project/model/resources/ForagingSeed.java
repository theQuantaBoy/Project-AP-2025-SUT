package ap.project.model.resources;

import ap.project.model.game.GameObject;
import ap.project.model.enums.resources_enums.ForagingSeedType;
import ap.project.model.enums.Season;

import java.util.List;

public class ForagingSeed extends GameObject
{
    private final ForagingSeedType seedType;

    public ForagingSeed(ForagingSeedType seedType)
    {
        this.seedType = seedType;
        this.ObjectType = seedType.getType();
    }

    public ForagingSeedType getSeedType()
    {
        return seedType;
    }
}
