package ap.project.model.resources;

import ap.project.model.game.GameObject;
import ap.project.model.enums.resources_enums.ForagingMineralType;

public class ForagingMineral extends GameObject
{
    private final ForagingMineralType mineralType;

    public ForagingMineral(ForagingMineralType mineralType)
    {
        this.mineralType = mineralType;
        this.ObjectType = mineralType.getType();
    }
}
