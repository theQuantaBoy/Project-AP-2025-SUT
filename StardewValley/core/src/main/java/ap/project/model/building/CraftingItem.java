package ap.project.model.building;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.building_enums.ArtisanGoodsType;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.game.GameObject;
import ap.project.model.game.Time;

import java.util.ArrayList;

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

    private ArrayList<GameObject> craftingIngredients = new ArrayList<>();

    public CraftingItem (CraftingRecipeEnums craftingType)
    {
        super(craftingType.getProduct(), 1);
        this.craftingType = craftingType;
        this.itemType = craftingType.getItemType();
    }

    public CraftingRecipeEnums getCraftingType()
    {
        return craftingType;
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
        if (!isWorking || neededHours == 0) return 0f;

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

    public boolean isWorking()
    {
        return isWorking;
    }

    public ArrayList<GameObject> getCraftingIngredients()
    {
        return craftingIngredients;
    }

    public void setCraftingIngredients(ArrayList<GameObject> ingredients)
    {
        this.craftingIngredients = ingredients;
    }

    public String getTimeRemaining()
    {
        if (!isWorking) return "Not started";

        int hoursPassed = getHoursPassed();
        int daysPassed = getDaysPassed();
        int totalHoursPassed = daysPassed * 14 + hoursPassed;
        int totalHoursNeeded = neededDays * 14 + neededHours;
        int hoursLeft = totalHoursNeeded - totalHoursPassed;

        if (hoursLeft <= 0) return "Ready!";

        // Format as "Xd Xh" (e.g., "2d 5h")
        int days = hoursLeft / 14;
        int hours = hoursLeft % 14;

        if (days > 0 && hours > 0) {
            return String.format("%dd %dh", days, hours);
        } else if (days > 0) {
            return String.format("%dd", days);
        } else {
            return String.format("%dh", hours);
        }
    }

    public enum ItemType
    {
        ONE_TIME,
        PERIODIC,
        PERMANENT
    }
}
