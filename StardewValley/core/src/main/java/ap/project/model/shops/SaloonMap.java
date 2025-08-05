package ap.project.model.shops;

import ap.project.model.enums.ShopType;
import ap.project.model.enums.shop_enums.TheStardropSaloonStock;

public class SaloonMap extends ShopMap {
    public SaloonMap() {
        super(ShopType.STARDROP_SALOON);
    }

    @Override
    protected void initializeProducts() {
        for (TheStardropSaloonStock stock : TheStardropSaloonStock.values()) {
            products.add(new ShopProduct(
                stock.getName(),
                stock.getPrice(),
                stock.getLimit(),
                stock.getGameObjectType(),
                stock
            ));
        }
    }
}
