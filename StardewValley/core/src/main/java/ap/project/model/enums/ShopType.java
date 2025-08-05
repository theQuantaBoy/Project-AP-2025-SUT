package ap.project.model.enums;

import ap.project.model.game.Point;

public enum ShopType
{
    BLACK_SMITH("Blacksmith", MapTypes.BLACK_SMITH, new Point(94, 82)),
    JOJA_MART("Joja Mart", MapTypes.JOJA_MART, new Point(95, 51)),
    PIERRE_GENERAL_STORE("Pierre's General Store", MapTypes.PIERRE_GENERAL_STORE, new Point(43, 57)),
    CARPENTER_SHOP("Carpenter's Shop", MapTypes.CARPENTER_SHOP, new Point(57, 64)),
    FISH_SHOP("Fish Shop", MapTypes.FISH_SHOP, new Point(10, 86)),
    MARINE_RANCH("Marine's Ranch", MapTypes.MARNIE_RANCH, new Point(58, 86)),
    STARDROP_SALOON("The Stardrop Saloon", MapTypes.SALOON, new Point(45, 71)),
    ;

    private final String name;
    private final MapTypes mapType;
    private final Point exteriorDoor;

    ShopType(String name, MapTypes mapType,  Point exteriorDoor)
    {
        this.name = name;
        this.mapType = mapType;
        this.exteriorDoor = exteriorDoor;
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

    @Override
    public String toString()
    {
        return name;
    }

    public static ShopType getShopType(String name)
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
}
