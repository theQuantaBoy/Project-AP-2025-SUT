package ap.project.screen;

import ap.project.model.enums.MapTypes;
import ap.project.model.enums.ShopType;
import ap.project.model.shops.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopManager {
    private static final Map<ShopType, ShopMap> shopMaps = new HashMap<>();

    static {
        // Initialize all shop maps
        shopMaps.put(ShopType.BLACK_SMITH, new BlacksmithMap());
        shopMaps.put(ShopType.CARPENTER_SHOP, new CarpenterMap());
        shopMaps.put(ShopType.FISH_SHOP, new FishShopMap());
        shopMaps.put(ShopType.JOJA_MART, new JojaMartMap());
        shopMaps.put(ShopType.MARINE_RANCH, new MarnieRanchMap());
        shopMaps.put(ShopType.PIERRE_GENERAL_STORE, new PierreGeneralStoreMap());
        shopMaps.put(ShopType.STARDROP_SALOON, new SaloonMap());
    }

    public static ShopMap getShopMap(ShopType type) {
        return shopMaps.get(type);
    }

    public static ShopMap getShopMapByMapType(MapTypes mapType) {
        for (ShopMap shopMap : shopMaps.values()) {
            if (shopMap.getMapType() == mapType) {
                return shopMap;
            }
        }
        return null;
    }

    public static List<ShopMap> getAllShopMaps() {
        return new ArrayList<>(shopMaps.values());
    }
}
