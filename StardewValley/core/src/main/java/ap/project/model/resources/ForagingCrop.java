package ap.project.model.resources;

import ap.project.model.GameObject;
import ap.project.model.enums.resources_enums.ForagingCropType;
import ap.project.model.enums.Season;

import java.util.List;

public class ForagingCrop extends GameObject
{
    private final ForagingCropType cropType;
    private List<Season> seasons;
    private int baseSellPrice;
    private int energy;

    public ForagingCrop(ForagingCropType type)
    {
        this.cropType = type;
        this.seasons = type.getSeasons();
        this.baseSellPrice = type.getBaseSellPrice();
        this.energy = type.getEnergy();
        this.ObjectType = type.getType();
    }
}
