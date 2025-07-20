package ap.project.model.game;

import ap.project.model.enums.MapTypes;
import ap.project.model.enums.TileTexture;

public class Cabin extends Map
{
    private final String mapPath;
    private Point bedPoint;

    public Cabin()
    {
        super(MapTypes.HOUSE);

        this.mapType = MapTypes.HOUSE;
        this.mapPath = mapType.getMapPath();

        //        initialize();
    }

    private void initialize()
    {
        int[] dims = new int[2];
        this.mapData = MapLoader.loadMap(mapPath, dims);

        if (mapData == null)
        {
            throw new IllegalStateException("Failed to load cabin map: " + mapPath);
        }

        this.WIDTH = dims[0];
        this.HEIGHT = dims[1];
        this.tiles = new Tile[HEIGHT][WIDTH];

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                tiles[y][x] = new Tile(new Point(x, y));
                tiles[y][x].setType(TileTexture.CABIN_INTERIOR_FLOOR);
            }
        }

        applyMap();
        this.startingPoint = findDoor();
        this.bedPoint = getStartBedPoint();
    }

    private Point findDoor()
    {
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                Tile tile = tiles[y][x];
                if (tile.getTexture().equals(TileTexture.SHOP_DOOR))
                {
                    return tile.getPoint();
                }
            }
        }
        return null;
    }

    private Point getStartBedPoint()
    {
        for (int y = HEIGHT - 1; y >= 0; y--)
        {
            for (int x = WIDTH - 1; x >= 0; x--)
            {
                Tile tile = tiles[y][x];
                if (tile.getTexture().equals(TileTexture.BED_TILE))
                {
                    return tile.getPoint();
                }
            }
        }

        return null;
    }

    public Point getBedPoint()
    {
        return bedPoint;
    }
}
