package ap.project.model.shops;

import ap.project.model.App.App;
import ap.project.model.game.GameObject;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.ShopType;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.enums.shop_enums.CarpentersShopStock;

import java.util.ArrayList;
import java.util.Arrays;

public class CarpentersShop extends Shop {
    private ArrayList<CarpentersShopStock> stocks = new ArrayList<>();
    private ArrayList<FarmBuildingType> farmBuildings = new ArrayList<>();

    public CarpentersShop() {
        super(ShopType.CARPENTER_SHOP, "Robin", 9, 20);
    }

    public void setStocks() {
        this.stocks.addAll(Arrays.asList(CarpentersShopStock.values()));
    }
    public void setFarmBuildings() {
        this.farmBuildings.addAll(Arrays.asList(FarmBuildingType.values()));
    }

    @Override
    public String showProducts()
    {
        StringBuilder products = new StringBuilder();

        products.append(super.showProducts());

        for(FarmBuildingType farmBuilding : FarmBuildingType.values())
        {
            products.append("\tfarm building: ").append(farmBuilding.getName()).append("\n\tprice: ")
                    .append(farmBuilding.getPrice()).append("\n");
            products.append("--------------------------------\n");
        }

        for(CarpentersShopStock stock : CarpentersShopStock.values())
        {
            products.append("\tstock: ").append(stock.getName()).append("\n\tprice: ").append(stock.getPrice()).append("\n");
            products.append("--------------------------------\n");
        }

        return products.toString();
    }

    @Override
    public String showAvailableProducts()
    {
        StringBuilder products = new StringBuilder();
        products.append(super.showAvailableProducts());

        for(FarmBuildingType farmBuilding : farmBuildings)
        {
            products.append("\tfarm building: ").append(farmBuilding.getName()).append("\n\tprice: ")
                    .append(farmBuilding.getPrice()).append("\n");
            products.append("--------------------------------\n");
        }

        for(CarpentersShopStock stock : stocks)
        {
            products.append("\tstock: ").append(stock.getName()).append("\n\tprice: ").append(stock.getPrice()).append("\n");
            products.append("--------------------------------\n");
        }

        return products.toString();
    }

    @Override
    public void purchase(GameObject gameObject)
    {
        GameObjectType targetType = gameObject.getObjectType();
        for (CarpentersShopStock stock : stocks)
        {
            if (stock.getGameObjectType() == targetType)
            {
                App.getCurrentGame().getCurrentPlayer().decreaseMoney(stock.getPrice());
                App.getCurrentGame().getCurrentPlayer().addToInventory(gameObject);
            }
        }
    }

    @Override
    public boolean isAffordable(GameObject gameObject) {
        GameObjectType targetType = gameObject.getObjectType();
        for (CarpentersShopStock stock : CarpentersShopStock.values())
        {
            if (stock.getGameObjectType() == targetType)
            {
                return App.getCurrentGame().getCurrentPlayer().getMoney() >= stock.getPrice();
            }
        }
        return false;
    }

    @Override
    public boolean isCorrectShop(GameObject gameObject)
    {
        GameObjectType targetType = gameObject.getObjectType();
        for (CarpentersShopStock stock : CarpentersShopStock.values())
        {
            if (stock.getGameObjectType() == targetType)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dailyLimitCheck(GameObject gameObject)
    {
        return true;
    }
}
