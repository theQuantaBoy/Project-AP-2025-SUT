package ap.project.model.shops;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.ShopType;
import ap.project.model.enums.shop_enums.PierresGeneralStoreBackpacks;
import ap.project.model.enums.shop_enums.PierresGeneralStoreSeasonalStock;
import ap.project.model.enums.shop_enums.PierresGeneralStoreYearRoundStock;

public class PierreGeneralStoreMap extends ShopMap {
    public PierreGeneralStoreMap() {
        super(ShopType.PIERRE_GENERAL_STORE);
    }

    @Override
    protected void initializeProducts() {
        // Year-round stock
        for (PierresGeneralStoreYearRoundStock stock : PierresGeneralStoreYearRoundStock.values()) {
            products.add(new ShopProduct(
                stock.getDisplayName(),
                stock.getPrice(),
                stock.getDailyLimit(),
                stock.getGameObjectType(),
                stock
            ));
        }

        // Seasonal stock
        for (PierresGeneralStoreSeasonalStock stock : PierresGeneralStoreSeasonalStock.values()) {
            ShopProduct product = new ShopProduct(
                stock.getName(),
                stock.getBasePrice(),
                stock.getDailyLimit(),
                stock.getType(),
                stock
            );
            product.isSeasonal = true;
            product.isAvailable = stock.getSeasons().contains(App.getCurrentGame().getCurrentTime().getSeason());
            products.add(product);
        }

        // Backpacks
        for (PierresGeneralStoreBackpacks backpack : PierresGeneralStoreBackpacks.values()) {
            products.add(new ShopProduct(
                backpack.getName(),
                backpack.getPrice(),
                backpack.getDailyLimit(),
                GameObjectType.BackPack,
                backpack
            ));
        }
    }
}
