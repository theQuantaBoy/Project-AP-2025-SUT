package ap.project.model.shops;

import ap.project.model.enums.ShopType;
import ap.project.model.enums.animal_enums.FarmAnimalsType;
import ap.project.model.enums.shop_enums.MarniesRanchShopInventory;

public class MarnieRanchMap extends ShopMap {
    public MarnieRanchMap() {
        super(ShopType.MARINE_RANCH);
    }

    @Override
    protected void initializeProducts() {
        // Inventory items
        for (MarniesRanchShopInventory item : MarniesRanchShopInventory.values()) {
            products.add(new ShopProduct(
                item.getName(),
                item.getPrice(),
                item.getLimit(),
                item.getGameObjectType(),
                item
            ));
        }

        // Animals
        for (FarmAnimalsType animal : FarmAnimalsType.values()) {
            products.add(new ShopProduct(
                animal.getName(),
                animal.getPurchaseCost(),
                -1, // Unlimited stock
                null, // Not a GameObject
                animal
            ));
        }
    }
}
