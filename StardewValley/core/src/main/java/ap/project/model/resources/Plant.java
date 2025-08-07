package ap.project.model.resources;

import ap.project.model.App.App;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import ap.project.model.enums.Season;

import java.util.List;

public class Plant extends GameObject
{
    protected String name;
    protected Enum<?> type;
    protected List<Integer> stages;
    protected int totalHarvestTime;
    protected int baseSellPrice;
    protected boolean isEdible;
    protected int energy;
    protected List<Season> seasons;

    protected boolean hasStarted = false;
    protected int lastWatered = 1; // TODO: can this fix the problem?
    protected int currentStageDay = 0;
    protected int lastHarvested = 0;

    protected boolean hasHarvested = false;
    protected int harvestWaitTime;

    protected boolean isGrowFaster = false;
    protected boolean isInGreenhouse = false;
    protected Point point;

    protected int playerIndex;

    public void water()
    {
        if (!hasStarted)
        {
            hasStarted = true;
        }

        lastWatered = 0;
    }

    private void grow()
    {
        if (currentStageDay < totalHarvestTime)
        {
            if (seasons.contains(App.getCurrentGame().getCurrentTime().getSeason()) || isInGreenhouse)
            {
                currentStageDay += 1;
            }
        }
    }

    public int getCurrentStage()
    {
        int counter = 0;
        int copy = currentStageDay;

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

    public void update()
    {
        if (hasBeenWateredToday() || (Math.random() < getTile().getWateringChance() / 100.0) || getTile().isShouldBeWateredAutomatically())
        {
            grow();
        }

        if (hasHarvested)
        {
            lastHarvested = Math.min(harvestWaitTime, lastHarvested + 1);
        }

        lastWatered += 1;
    }

    public boolean canHarvest() // would be overridden
    {
        if (!seasons.contains(App.getCurrentGame().getCurrentTime().getSeason()) && !isInGreenhouse)
        {
            return false;
        }

        if (!hasHarvested)
        {
            return (currentStageDay == totalHarvestTime);
        }

        return lastHarvested >= harvestWaitTime;
    }

    public Enum<?> getPlantType()
    {
        return type;
    }

    public boolean isInGreenHouse()
    {
        return isInGreenhouse;
    }

    public void putInGreenhouse()
    {
        isInGreenhouse = true;
    }

    public String showDetails(Plant plant, Tile tile)
    {
        StringBuilder output = new StringBuilder();

        if (plant instanceof Tree)
        {
            output.append(((Tree) plant).getTreeType().getCraftInfo());
        } else if (plant instanceof Crop)
        {
            output.append(((Crop) plant).getCropType().getCraftInfo());
        }

        output.append("\n------------------------------------\n");

        output.append("Remaining Time: ").append(totalHarvestTime - currentStageDay).append(" (days)\n");
        output.append("Current Stage: ").append(currentStageDay).append(" (days ago)\n");

        output.append("------------------------------------\n");
        if (hasHarvested)
        {
            output.append("Harvest Wait Time: ").append(harvestWaitTime).append(" (days)\n");
            output.append("Days required before Next Harvest: ").append(harvestWaitTime - lastHarvested).append(" (days)\n");
            output.append("------------------------------------\n");
        }

        output.append("Watered Today: ").append(lastWatered == 0 ? "positive" : "negative").append("\n");
        output.append("Has Been Fertilized: ").append(tile.isFertilized() ? "positive" : "negative").append("\n");

        return output.toString().trim();
    }

    public boolean hasBeenWateredToday()
    {
        if (!hasStarted)
        {
            return false;
        }

        return lastWatered == 0;
    }

    public int getLastWatered()
    {
        if (!hasStarted)
        {
            return -1;
        }

        return lastWatered;
    }

    public boolean hasStarted()
    {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted)
    {
        this.hasStarted = hasStarted;
    }

    public void setHasStarted()
    {
        this.hasStarted = true;
    }

    protected void setGrowFaster()
    {
        totalHarvestTime -= 1;
    }

    public void getAttacked()
    {
        // overrided in Crop and Tree
    }

    public String getName()
    {
        return name;
    }

    public Enum<?> getType()
    {
        return type;
    }

    public List<Integer> getStages()
    {
        return stages;
    }

    public int getTotalHarvestTime()
    {
        return totalHarvestTime;
    }

    public int getBaseSellPrice()
    {
        return baseSellPrice;
    }

    public boolean isEdible()
    {
        return isEdible;
    }

    public int getEnergy()
    {
        return energy;
    }

    public List<Season> getSeasons()
    {
        return seasons;
    }

    public boolean isHasStarted()
    {
        return hasStarted;
    }

    public int getCurrentStageDay()
    {
        return currentStageDay;
    }

    public int getLastHarvested()
    {
        return lastHarvested;
    }

    public boolean isHasHarvested()
    {
        return hasHarvested;
    }

    public int getHarvestWaitTime()
    {
        return harvestWaitTime;
    }

    public boolean isInGreenhouse()
    {
        return isInGreenhouse;
    }

    public void setLastWatered(int lastWatered)
    {
        this.lastWatered = lastWatered;
    }

    public void setCurrentStageDay(int currentStageDay)
    {
        this.currentStageDay = currentStageDay;
    }

    public void setLastHarvested(int lastHarvested)
    {
        this.lastHarvested = lastHarvested;
    }

    public void setHasHarvested(boolean hasHarvested)
    {
        this.hasHarvested = hasHarvested;
    }

    public void setHarvestWaitTime(int harvestWaitTime)
    {
        this.harvestWaitTime = harvestWaitTime;
    }

    public void setInGreenhouse(boolean inGreenhouse)
    {
        isInGreenhouse = inGreenhouse;
    }

    public Point getPoint()
    {
        return point;
    }

    public int getPlayerIndex()
    {
        return playerIndex;
    }

    public Tile getTile()
    {
        Player player = App.getCurrentGame().getPlayers().get(this.playerIndex);
        if (!isInGreenhouse())
        {
            return player.getFarm().getTile(point.getX(), point.getY());
        }

        return player.getGreenHouse().getTile(point.getX(), point.getY());
    }

    public boolean isGrowFaster()
    {
        return isGrowFaster;
    }

    public void setGrowFaster(boolean growFaster)
    {
        isGrowFaster = growFaster;
    }
}
