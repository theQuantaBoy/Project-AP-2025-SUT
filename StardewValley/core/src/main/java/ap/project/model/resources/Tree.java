package ap.project.model.resources;

import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import ap.project.model.enums.resources_enums.FruitType;
import ap.project.model.enums.resources_enums.TreeType;

public class Tree extends Plant
{
    private FruitType fruit;
    private int fruitHarvestCycle;

    public Tree(TreeType treeType, Point point, int playerIndex, boolean isGrowFaster)
    {
        this.type = treeType;
        this.name = treeType.getName();
        this.stages = treeType.getStages();
        this.totalHarvestTime = treeType.getTotalHarvestTime();
        this.fruit = treeType.getFruit();
        this.fruitHarvestCycle = treeType.getFruitHarvestCycle();
        this.baseSellPrice = treeType.getFruitBaseSellPrice();
        this.isEdible = treeType.isEdible();
        this.energy = treeType.getEnergy();
        this.seasons = treeType.getSeasons();

        harvestWaitTime = this.totalHarvestTime;
        this.point = point;
        this.playerIndex = playerIndex;

        this.isGrowFaster = isGrowFaster;
        if (isGrowFaster)
        {
            setGrowFaster();
        }
    }

    public void harvest()
    {
        hasHarvested = true;
        lastHarvested = 0;
        harvestWaitTime = fruitHarvestCycle;
    }

    @Override
    public int getCurrentStage()
    {
        int counter = 0;
        int copy = currentStageDay;

        if (currentStageDay == totalHarvestTime)
        {
            return stages.size();
        }

        for (int stage : stages)
        {
            copy -= stage;
            if (copy <= 0)
            {
                return counter;
            }
            counter += 1;
        }

        return -1;
    }

    public FruitType getFruit()
    {
        return fruit;
    }

    public TreeType getTreeType()
    {
        return (TreeType)type;
    }

    public int getFruitHarvestCycle()
    {
        return fruitHarvestCycle;
    }

    @Override
    public void getAttacked()
    {
        lastHarvested += 1;
    }
}
