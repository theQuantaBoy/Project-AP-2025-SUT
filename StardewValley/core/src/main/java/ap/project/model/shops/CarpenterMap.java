package ap.project.model.shops;

import ap.project.model.enums.ShopType;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.enums.shop_enums.CarpentersShopStock;

public class CarpenterMap extends ShopMap {
    public CarpenterMap() {
        super(ShopType.CARPENTER_SHOP);
    }

    @Override
    protected void initializeProducts() {
        // Stock items
        for (CarpentersShopStock stock : CarpentersShopStock.values()) {
            products.add(new ShopProduct(
                stock.getName(),
                stock.getPrice(),
                -1, // Unlimited stock
                stock.getGameObjectType(),
                stock
            ));
        }

        // Buildings
        for (FarmBuildingType building : FarmBuildingType.values()) {
            products.add(new ShopProduct(
                building.getName(),
                building.getPrice(),
                -1, // Unlimited stock
                null, // Not a GameObject
                building
            ));
        }
    }
}
