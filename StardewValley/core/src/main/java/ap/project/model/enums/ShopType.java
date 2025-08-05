package ap.project.model.enums;

import ap.project.model.game.Point;

public enum ShopType
{
    BLACK_SMITH("Blacksmith", MapTypes.BLACK_SMITH, new Point(94, 82), "maps/general/shops/blacksmith.tmx"),
    JOJA_MART("Joja Mart", MapTypes.JOJA_MART, new Point(95, 51), "maps/general/shops/joja_mart.tmx"),
    PIERRE_GENERAL_STORE("Pierre's General Store", MapTypes.PIERRE_GENERAL_STORE, new Point(43, 57), "maps/general/shops/pierre_store.tmx"),
    CARPENTER_SHOP("Carpenter's Shop", MapTypes.CARPENTER_SHOP, new Point(57, 64), "maps/general/shops/carpenter_shop.tmx"),
    FISH_SHOP("Fish Shop", MapTypes.FISH_SHOP, new Point(10, 86), "maps/general/shops/fish_shop.tmx"),
    MARINE_RANCH("Marine's Ranch", MapTypes.MARNIE_RANCH, new Point(58, 86), "maps/general/shops/animal_shop.tmx"),
    STARDROP_SALOON("The Stardrop Saloon", MapTypes.SALOON, new Point(45, 71), "maps/general/shops/saloon.tmx"),
    ;

    private final String name;
    private final MapTypes mapType;
    private final Point exteriorDoor;
    private final String tmxPath;

    ShopType(String name, MapTypes mapType,  Point exteriorDoor, String tmxPath)
    {
        this.name = name;
        this.mapType = mapType;
        this.exteriorDoor = exteriorDoor;
        this.tmxPath = tmxPath;
    }

    public String getName()
    {
        return name;
    }

    public static ShopType getShop(String name)
    {
        for (ShopType shopType : ShopType.values())
        {
            if (shopType.getName().equalsIgnoreCase(name))
            {
                return shopType;
            }
        }
        return null;
    }

    public MapTypes getMapType()
    {
        return mapType;
    }

    public Point getExteriorDoor()
    {
        return exteriorDoor;
    }

    public String getTmxFileName() {
        return tmxPath;
    }
}
