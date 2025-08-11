package ap.project.model.game;

import ap.project.model.enums.animal_enums.FarmBuildingType;

public class AnimalBuildingMap extends Map
{
    public AnimalBuildingMap(FarmBuildingType farmBuilding)
    {
        super(farmBuilding.getMapType());
    }
}
