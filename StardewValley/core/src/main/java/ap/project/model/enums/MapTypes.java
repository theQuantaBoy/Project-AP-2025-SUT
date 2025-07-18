package ap.project.model.enums;

import java.util.ArrayList;

public enum MapTypes
{
//    STANDARD("standard", "map/standard.json", ""),
    RIVERLAND("riverland", "map/river.json", "", null),
    HILL_TOP("hilltop", "map/hilltop.json", "", null),
    BEACH("four_corners", "map/beach.json", "", null),

    CABIN("cabin", "map/cabin.json", "", null),
    GREEN_HOUSE("greenhouse", "map/greenhouse.json", "", null),
    CITY("city", "map/city.json", "", null),
    SHOP("shop", "map/shop.json", "", null),

    FOREST("forest", "", "maps/Forest.tmx", null),

    STANDARD("standard", "", "", MapKind.FARM),
    TOWN("town", "", "", MapKind.TOWN)
    ;

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
        mapNames.add(FOREST);
//        mapNames.add(HILL_TOP.getName());
//        mapNames.add(BEACH.getName());
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
}
