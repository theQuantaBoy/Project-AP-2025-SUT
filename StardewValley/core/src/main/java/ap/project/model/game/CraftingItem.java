package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.building_enums.ArtisanGoodsType;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;

public class CraftingItem extends GameObject
{
    private final ItemType itemType;
    private final CraftingRecipeEnums craftingType;

    private ArtisanGoodsType artisanType = null;
    private boolean isWorking = false;

    private int startDay;
    private int startHour;

    private int neededDays;
    private int neededHours;

    public CraftingItem (CraftingRecipeEnums craftingType)
    {
        super(craftingType.getProduct(), 1);
        this.craftingType = craftingType;
        this.itemType = craftingType.getItemType();
    }

    public boolean isDone()
    {
        int daysPassed = getDaysPassed();
        int hoursPassed = getHoursPassed();

        return (daysPassed >= neededDays) && (hoursPassed >= neededHours);
    }

    public int getDaysPassed()
    {
        Time time = App.getCurrentGame().getCurrentTime();
        int day = time.getTotalDaysPassed();
        return day - this.startDay;
    }

    public int getHoursPassed()
    {
        Time time = App.getCurrentGame().getCurrentTime();
        int hour = time.getTotalHoursPassed();
        return hour - this.startHour;
    }

    public GameObject getProduct()
    {
        if (artisanType != null)
        {
            GameObjectType type = artisanType.getType();
            return new GameObject(type, 1);
        }

        return null;
    }

    public void start(ArtisanGoodsType artisanType)
    {
        this.artisanType = artisanType;
        int totalHors = artisanType.getProcessTime();

        if (totalHors != -1)
        {
            Time time = App.getCurrentGame().getCurrentTime();
            this.startDay = time.getTotalDaysPassed();
            this.startHour = time.getTotalHoursPassed();

            this.neededDays = totalHors / 14;
            this.neededHours = totalHors % 14;
            this.isWorking = true;
        }
    }

    public float getHowMuchDone()
    {
        if (isWorking)
        {
            if (neededDays != 0)
            {
                return ((float) getDaysPassed() / neededDays);
            }

            return ((float) getHoursPassed() / neededHours);
        }

        return 0;
    }

    public void reset()
    {
        this.artisanType = null;
        this.neededDays = 0;
        this.neededHours = 0;
        this.isWorking = false;
    }

    public enum ItemType
    {
        ONE_TIME,
        PERIODIC,
        PERMANENT
    }
}
