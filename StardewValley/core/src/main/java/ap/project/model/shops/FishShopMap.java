package ap.project.model.shops;


import ap.project.model.enums.ShopType;
import ap.project.model.enums.shop_enums.FishShopStock;

public class FishShopMap extends ShopMap {
    public FishShopMap() {
        super(ShopType.FISH_SHOP);
    }

    @Override
    protected void initializeProducts() {
        for (FishShopStock stock : FishShopStock.values()) {
            products.add(new ShopProduct(
                stock.getName(),
                stock.getPrice(),
                stock.getDailyLimit(),
                stock.getGameObjectType(),
                stock
            ));
        }
    }
}

