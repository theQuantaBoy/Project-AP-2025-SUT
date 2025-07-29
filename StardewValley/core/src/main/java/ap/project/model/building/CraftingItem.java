package ap.project.model.building;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.building_enums.ArtisanGoodsType;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.game.GameObject;
import ap.project.model.game.Tile;
import ap.project.model.game.Time;

import java.util.ArrayList;

public class CraftingItem extends GameObject
{
    private final ItemType itemType;
    private final CraftingRecipeEnums craftingType;

    private ArtisanGoodsType artisanType = null;
    private boolean isWorking = false;

    private int startDay = -1;
    private int startHour = -1;

    private int neededDays = -1;
    private int neededHours = -1;

    private ArrayList<GameObject> craftingIngredients = new ArrayList<>();
    private final Tile tile;

    public CraftingItem (CraftingRecipeEnums craftingType, Tile tile)
    {
        super(craftingType.getProduct(), 1);
        this.craftingType = craftingType;
        this.itemType = craftingType.getItemType();
        this.tile = tile;
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
        if (!isWorking ) return 0f;

        int totalNeededHours = neededHours + (14 *  neededDays);
        int totalDoneHours = getHoursPassed() + (14 * getDaysPassed());

        float progress = ((float) totalDoneHours / (float)totalNeededHours);
        return Math.min(progress, 1f);
    }

    public void reset()
    {
        this.artisanType = null;
        this.startDay = -1;
        this.startHour = -1;
        this.neededDays = -1;
        this.neededHours = -1;
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

        if (isDone()) return "Ready!";

        // Format as "Xd Xh" (e.g., "2d 5h")
        int days = hoursLeft / 14;
        int hours = hoursLeft % 14;

        if (neededDays > 0 && neededHours > 0) {
            return String.format("%dd %dh", days, hours);
        } else if (neededDays > 0) {
            return String.format("%dd", days);
        } else {
            return String.format("%dh", hours);
        }
    }

    public Tile getTile()
    {
        return tile;
    }

    public ItemType getItemType()
    {
        return itemType;
    }

    public enum ItemType
    {
        ONE_TIME,
        PERIODIC,
        PERMANENT
    }
}
