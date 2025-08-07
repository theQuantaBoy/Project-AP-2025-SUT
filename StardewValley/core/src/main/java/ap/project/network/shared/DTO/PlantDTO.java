package ap.project.network.shared.DTO;

import ap.project.model.enums.Season;
import ap.project.model.game.GameObject;
import ap.project.model.game.Point;
import ap.project.model.resources.Plant;

import java.util.ArrayList;
import java.util.List;

public class PlantDTO extends GameObjectDTO
{
    public String name;
    public Enum<?> type;
    public int totalHarvestTime;
    public int baseSellPrice;
    public boolean isEdible;
    public int energy;

    public boolean hasStarted;
    public int lastWatered;
    public int currentStageDay;
    public int lastHarvested;

    public boolean hasHarvested;
    public int harvestWaitTime;

    public boolean isGrowFaster;
    public boolean isInGreenhouse;

    public int playerIndex;
    public Point point;

    public PlantDTO() {}

    public PlantDTO(Plant plant)
    {
        super(plant);

        if (plant != null)
        {
            this.name = plant.getName();
            this.type = plant.getType();
            this.totalHarvestTime = plant.getTotalHarvestTime();
            this.baseSellPrice = plant.getBaseSellPrice();
            this.isEdible = plant.isEdible();
            this.energy = plant.getEnergy();

            this.hasStarted = plant.hasStarted();
            this.lastWatered = plant.getLastWatered();
            this.currentStageDay = plant.getCurrentStageDay();
            this.lastHarvested = plant.getLastHarvested();

            this.hasHarvested = plant.isHasHarvested();
            this.harvestWaitTime = plant.getHarvestWaitTime();

            this.isGrowFaster = plant.isGrowFaster();
            this.isInGreenhouse = plant.isInGreenhouse();

            this.playerIndex = plant.getPlayerIndex();
            this.point = plant.getPoint();
        }
    }
}
