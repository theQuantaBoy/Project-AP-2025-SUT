package ap.project.model.game;

import ap.project.model.enums.MapTypes;
import ap.project.model.enums.TileTexture;

public class Cabin extends Map
{
    private final String mapPath;
    private Point refrigeratorPoint;
    private Point ovenPoint;

    public Cabin()
    {
        super(MapTypes.HOUSE);

        this.mapType = MapTypes.HOUSE;
        this.mapPath = mapType.getMapPath();

        //        initialize();
    }

    public Point getRefrigeratorPoint()
    {
        return refrigeratorPoint;
    }

    public Point getOvenPoint()
    {
        return ovenPoint;
    }

    public void setRefrigeratorPoint(Point refrigeratorPoint)
    {
        this.refrigeratorPoint = refrigeratorPoint;
    }

    public void setOvenPoint(Point ovenPoint)
    {
        this.ovenPoint = ovenPoint;
    }
}
