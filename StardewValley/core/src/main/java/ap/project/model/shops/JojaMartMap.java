package ap.project.model.shops;

import ap.project.model.App.App;
import ap.project.model.enums.ShopType;
import ap.project.model.enums.shop_enums.JojaMartPermanentStock;
import ap.project.model.enums.shop_enums.JojaMartSeasonalStock;

public class JojaMartMap extends ShopMap {
    public JojaMartMap() {
        super(ShopType.JOJA_MART);
    }

    @Override
    protected void initializeProducts() {
        // Permanent stock
        for (JojaMartPermanentStock item : JojaMartPermanentStock.values()) {
            ShopProduct product = new ShopProduct(
                item.getName(),
                item.getPrice(),
                item.getLimit(),
                item.getGameObjectType(),
                item
            );
            product.isSeasonal = false;
            products.add(product);
        }

        // Seasonal stock
        for (JojaMartSeasonalStock item : JojaMartSeasonalStock.values()) {
            ShopProduct product = new ShopProduct(
                item.getName(),
                item.getPrice(),
                item.getLimit(),
                item.getGameObjectType(),
                item
            );
            product.isSeasonal = true;
            product.isAvailable = item.getSeason().equals(App.getCurrentGame().getCurrentTime().getSeason());
            products.add(product);
        }
    }
}
