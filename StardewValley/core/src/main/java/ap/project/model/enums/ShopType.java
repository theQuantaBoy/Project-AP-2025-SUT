package ap.project.model.enums;

import ap.project.model.game.Point;

public enum ShopType
{
    BLACK_SMITH("Blacksmith", MapTypes.BLACK_SMITH, new Point(94, 82), "maps/general/shops/blacksmith.tmx", null),
    JOJA_MART("Joja Mart", MapTypes.JOJA_MART, new Point(95, 51), "maps/general/shops/joja_mart.tmx", null),
    PIERRE_GENERAL_STORE("Pierre's General Store", MapTypes.PIERRE_GENERAL_STORE, new Point(43, 57), "maps/general/shops/pierre_store.tmx",
        new Point(7, 18)),
    CARPENTER_SHOP("Carpenter's Shop", MapTypes.CARPENTER_SHOP, new Point(57, 64), "maps/general/shops/carpenter_shop.tmx", null),
    FISH_SHOP("Fish Shop", MapTypes.FISH_SHOP, new Point(10, 86), "maps/general/shops/fish_shop.tmx", null),
    MARINE_RANCH("Marine's Ranch", MapTypes.MARNIE_RANCH, new Point(58, 86), "maps/general/shops/animal_shop.tmx", null),
    STARDROP_SALOON("The Stardrop Saloon", MapTypes.SALOON, new Point(45, 71), "maps/general/shops/saloon.tmx", null),
    ;

    private final String name;
    private final MapTypes mapType;
    private final Point exteriorDoor;
    private final String tmxPath;
    private final Point counterPoint;

    ShopType(String name, MapTypes mapType,  Point exteriorDoor, String tmxPath,  Point counterPoint)
    {
        this.name = name;
        this.mapType = mapType;
        this.exteriorDoor = exteriorDoor;
        this.tmxPath = tmxPath;
        this.counterPoint = counterPoint;
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

    public Point getCounterPoint()
    {
        return counterPoint;
    }
}
