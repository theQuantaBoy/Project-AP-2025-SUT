package ap.project.model.enums;

public enum ShopType
{
    BLACK_SMITH("Blacksmith", MapTypes.BLACK_SMITH),
    JOJA_MART("Joja Mart", MapTypes.JOJA_MART),
    PIERRE_GENERAL_STORE("Pierre's General Store", MapTypes.PIERRE_GENERAL_STORE),
    CARPENTER_SHOP("Carpenter's Shop", MapTypes.CARPENTER_SHOP),
    FISH_SHOP("Fish Shop", MapTypes.FISH_SHOP),
    MARINE_RANCH("Marine's Ranch", MapTypes.MARNIE_RANCH),
    STARDROP_SALOON("The Stardrop Saloon", MapTypes.SALOON),
    ;

    private final String name;
    private final MapTypes mapType;

    ShopType(String name, MapTypes mapType)
    {
        this.name = name;
        this.mapType = mapType;
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
}
