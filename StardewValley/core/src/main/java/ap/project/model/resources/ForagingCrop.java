package ap.project.model.resources;

import ap.project.model.game.GameObject;
import ap.project.model.enums.resources_enums.ForagingCropType;
import ap.project.model.enums.Season;

import java.util.List;

public class ForagingCrop extends GameObject
{
    public ForagingCrop(ForagingCropType type)
    {
        this.ObjectType = type.getType();
    }
}
