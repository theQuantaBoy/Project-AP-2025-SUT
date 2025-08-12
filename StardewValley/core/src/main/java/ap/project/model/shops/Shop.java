package ap.project.model.shops;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.game.GameObject;
import ap.project.model.game.Map;
import ap.project.model.game.Point;
import ap.project.model.game.Time;
import ap.project.model.enums.ShopType;

import java.util.ArrayList;
import java.util.List;

public abstract class Shop extends Map
{
    private final ShopType type;
    private final String shopName;
    private final String salesManName;
    private final int startWork;
    private final int endWork;
    private Point exteriorDoor;
    private Point interiorDoor;
    protected List<ShopProduct> products = new ArrayList<>();
    private Point counterPoint;

    public String showProducts()
    {
        return shopName + "'s Products:\n--------------------------------\n";
    }

    public String showAvailableProducts()
    {
        return shopName + "'s Available Products:\n--------------------------------\n";
    }

    protected abstract void initializeProducts();

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
        this.counterPoint = type.getCounterPoint();
        initializeProducts();
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

    // Returns all products regardless of availability
    public List<ShopProduct> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Returns only available products (shop open and in stock)
    public List<ShopProduct> getAvailableProducts() {
        List<ShopProduct> availableProducts = new ArrayList<>();
        Time currentTime = App.getCurrentGame().getCurrentTime();

        if (!isOpen(currentTime)) {
            return availableProducts;
        }

        for (ShopProduct product : products) {
            if (product.getStock() != 0 && product.isAvailable()) {
                availableProducts.add(product);
            }
        }
        return availableProducts;
    }

    public int getStartWork() {
        return startWork;
    }

    public int getEndWork() {
        return endWork;
    }

    public Point getCounterPoint() {
        return counterPoint;
    }
}
