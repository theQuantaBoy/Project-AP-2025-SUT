package ap.project.model.resources;

import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.MixedSeedType;
import ap.project.model.enums.Season;

import java.util.List;

public class MixedSeed
{
    private final MixedSeedType seedType;
    private Season season;
    private List<CropType> possibleCrops;

    public MixedSeed(MixedSeedType seedType)
    {
        this.seedType = seedType;
        this.season = seedType.getSeason();
        this.possibleCrops = seedType.getPossibleCrops();
    }
}
