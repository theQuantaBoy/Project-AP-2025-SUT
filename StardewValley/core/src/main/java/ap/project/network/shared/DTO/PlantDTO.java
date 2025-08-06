package ap.project.network.shared.DTO;

import ap.project.model.enums.Season;
import ap.project.model.game.GameObject;
import ap.project.model.resources.Plant;

import java.util.ArrayList;
import java.util.List;

public class PlantDTO extends GameObjectDTO
{
    public String name;
    public Enum<?> type;
    public List<Integer> stages = new ArrayList<>();
    public int totalHarvestTime;
    public int baseSellPrice;
    public boolean isEdible;
    public int energy;
    public List<Season> seasons = new ArrayList<>();

    public boolean hasStarted;
    public int lastWatered;
    public int currentStageDay;
    public int lastHarvested;

    public boolean hasHarvested;
    public int harvestWaitTime;

    public boolean isInGreenhouse;
    public TileDTO tileDTO;

    public PlantDTO() {}

    public PlantDTO(Plant plant)
    {
        super(plant);

        if (plant != null)
        {
            this.name = plant.getName();
            this.type = plant.getType();
            this.stages = plant.getStages();
            this.totalHarvestTime = plant.getTotalHarvestTime();
            this.baseSellPrice = plant.getBaseSellPrice();
            this.isEdible = plant.isEdible();
            this.energy = plant.getEnergy();
            this.seasons = plant.getSeasons();

            this.hasStarted = plant.hasStarted();
            this.lastWatered = plant.getLastWatered();
            this.currentStageDay = plant.getCurrentStageDay();
            this.lastHarvested = plant.getLastHarvested();

            this.hasHarvested = plant.isHasHarvested();
            this.harvestWaitTime = plant.getHarvestWaitTime();

            this.isInGreenhouse = plant.isInGreenhouse();
            this.tileDTO = new TileDTO(plant.getTile());
        }
    }
}
