package ap.project.model.shops;

import ap.project.model.enums.MapTypes;
import ap.project.model.game.GameObject;
import ap.project.model.game.Map;
import ap.project.model.game.Point;
import ap.project.model.game.Time;
import ap.project.model.enums.ShopType;

public abstract class Shop extends Map
{
    //    private final ArrayList<Tile> shopTiles;
    private final ShopType type;
    private final String shopName;
    private final String salesManName;
    private final int startWork;
    private final int endWork;
    private Point exteriorDoor;
    private Point interiorDoor;

    public String showProducts()
    {
        return shopName + "'s Products:\n--------------------------------\n";
    }

    public String showAvailableProducts()
    {
        return shopName + "'s Available Products:\n--------------------------------\n";
    }

    public abstract void purchase(GameObject gameObject);

    public abstract boolean isAffordable(GameObject gameObject);

    public abstract boolean isCorrectShop(GameObject gameObject);

    public abstract boolean dailyLimitCheck(GameObject gameObject);

    public Shop(ShopType type, String salesManName, int startWork, int endWork)
    {
        super(type.getMapType());

        this.type = type;
        this.shopName = type.getName();
        this.salesManName = salesManName;
        this.startWork = startWork;
        this.endWork = endWork;
        this.exteriorDoor = type.getExteriorDoor();
    }

    public boolean isOpen(Time currentTime)
    {
        return currentTime.getHour() >= startWork && currentTime.getHour() <= endWork;
    }

    public ShopType getType()
    {
        return type;
    }

    public Point getExteriorDoor()
    {
        return exteriorDoor;
    }

    public Point getInteriorDoor()
    {
        return interiorDoor;
    }

    public void setExteriorDoor(Point exteriorDoor)
    {
        this.exteriorDoor = exteriorDoor;
    }

    public void setInteriorDoor(Point interiorDoor)
    {
        this.interiorDoor = interiorDoor;
    }
}
