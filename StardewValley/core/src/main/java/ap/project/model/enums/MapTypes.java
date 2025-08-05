package ap.project.model.enums;

import java.util.ArrayList;

public enum MapTypes
{
    //    STANDARD("standard", "map/standard.json", ""),
    RIVERLAND("riverland", "map/river.json", "", null),
    HILL_TOP("hilltop", "map/hilltop.json", "", null),
    BEACH("four_corners", "map/beach.json", "", null),


    COMBAT("combat", "", "", MapKind.FARM),
    FISHING("fishing", "", "", MapKind.FARM),
    FORAGING("foraging", "", "", MapKind.FARM),
    MINING("mining", "", "", MapKind.FARM),
    STANDARD("standard", "", "", MapKind.FARM),
    TOWN("town", "", "", MapKind.TOWN),
    HOUSE("house", "", "", MapKind.HOUSE),
    GREEN_HOUSE("greenhouse", "", "", MapKind.GREEN_HOUSE),

    MARNIE_RANCH("Marine's Ranch", "", "", MapKind.SHOP),
    BLACK_SMITH("blacksmith", "", "", MapKind.SHOP),
    CARPENTER_SHOP("Carpenter's Shop", "", "", MapKind.SHOP),
    FISH_SHOP("fish shop", "", "", MapKind.SHOP),
    JOJA_MART("joja mart", "", "", MapKind.SHOP),
    PIERRE_GENERAL_STORE("Pierre's General Store", "", "", MapKind.SHOP),
    SALOON("The Stardrop Saloon", "", "", MapKind.SHOP);

    private final String name;
    private final String mapPath;
    private final String tmxPath;
    private final MapKind mapKind;

    MapTypes(String name, String mapPath, String tmxPath, MapKind mapKind)
    {
        this.name = name;
        this.mapPath = mapPath;
        this.tmxPath = tmxPath;
        this.mapKind = mapKind;
    }

    public String getName()
    {
        return name;
    }

    public static ArrayList<MapTypes> getFarms()
    {
        ArrayList<MapTypes> mapNames = new ArrayList<>();
        mapNames.add(STANDARD);
        mapNames.add(MINING);
        mapNames.add(FISHING);
        mapNames.add(FISHING);
        mapNames.add(COMBAT);
        return mapNames;
    }

    public String getMapPath()
    {
        return mapPath;
    }

    public static ArrayList<MapTypes> farmTypes()
    {
        ArrayList<MapTypes> farmTypes = new ArrayList<MapTypes>();
        farmTypes.add(STANDARD);
        farmTypes.add(RIVERLAND);
        farmTypes.add(HILL_TOP);
        farmTypes.add(BEACH);
        return farmTypes;
    }

    public MapTypes getFarmType(String name)
    {
        if (name.equalsIgnoreCase(STANDARD.getName()))
        {
            return STANDARD;
        }

        if (name.equalsIgnoreCase(RIVERLAND.getName()))
        {
            return RIVERLAND;
        }

        if (name.equalsIgnoreCase(HILL_TOP.getName()))
        {
            return HILL_TOP;
        }

        if (name.equalsIgnoreCase(BEACH.getName()))
        {
            return BEACH;
        }

        return null;
    }

    public static MapTypes getMapType(int index)
    {
        switch (index)
        {
            case 0:
                return STANDARD;
            case 1:
                return RIVERLAND;
            case 2:
                return HILL_TOP;
            case 3:
                return BEACH;
        }

        return STANDARD;
    }

    public String getTmxPath()
    {
        return tmxPath;
    }

    public MapKind getMapKind()
    {
        return mapKind;
    }

    public static MapTypes getShopMapType(ShopType shopType) {
        switch (shopType) {
            case BLACK_SMITH: return BLACK_SMITH;
            case CARPENTER_SHOP: return CARPENTER_SHOP;
            case FISH_SHOP: return FISH_SHOP;
            case JOJA_MART: return JOJA_MART;
            case MARINE_RANCH: return MARNIE_RANCH;
            case PIERRE_GENERAL_STORE: return PIERRE_GENERAL_STORE;
            case STARDROP_SALOON: return SALOON;
            default: return null;
        }
    }
}
