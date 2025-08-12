package ap.project.model.shops;

import ap.project.model.enums.ShopType;
import ap.project.model.enums.shop_enums.BlacksmithStockItem;
import ap.project.model.enums.shop_enums.BlacksmithUpgradeTools;


public class BlacksmithMap extends ShopMap {

    public BlacksmithMap() {
        super(ShopType.BLACK_SMITH);
    }
    @Override
    protected void initializeProducts() {
        for (BlacksmithStockItem item : BlacksmithStockItem.values()) {
            products.add(new ShopProduct(
                item.getName(),
                item.getPrice(),
                -1, // Unlimited stock
                item.getGameObjectType(),
                item
            ));
        }
        for (BlacksmithUpgradeTools tool : BlacksmithUpgradeTools.values()) {
            products.add(new ShopProduct(
                tool.getName(),
                tool.getCost(),
                tool.getDailyLimit(),
                null, // Not a GameObject
                tool
            ));
        }

    }
}
