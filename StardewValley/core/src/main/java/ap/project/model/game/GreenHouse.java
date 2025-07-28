package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.TileTexture;

import java.util.ArrayList;
import java.util.Comparator;

public class GreenHouse extends Map
{
    private final String mapPath;
    private boolean isBuilt = false;

    private static final int woodCost = 500;
    private static final int moneyCost = 1000;
    private ArrayList<Tile> tilesWithCraftingItems = new ArrayList<>();

    public GreenHouse()
    {
        super(MapTypes.GREEN_HOUSE);

        this.mapType = MapTypes.GREEN_HOUSE;
        this.mapPath = mapType.getMapPath();
//        initialize();
    }

    private void initialize()
    {
        int[] dims = new int[2];
        this.mapData = MapLoader.loadMap(mapPath, dims);

        if (mapData == null)
        {
            throw new IllegalStateException("Failed to load green house map: " + mapPath);
        }

        this.WIDTH = dims[0];
        this.HEIGHT = dims[1];
        this.tiles = new Tile[HEIGHT][WIDTH];

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                tiles[y][x] = new Tile(new Point(x, y));
                tiles[y][x].setType(TileTexture.LAND);
            }
        }

        applyMap();
        this.startingPoint = findDoor();
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

    public void build()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();
//        player.increaseMoney(-1 * moneyCost);
//        player.removeAmountFromInventory(GameObjectType.WOOD, woodCost);
        isBuilt = true;
    }

    public boolean isBuilt()
    {
        return isBuilt;
    }

    public static int getMoneyCost()
    {
        return moneyCost;
    }

    public static int getWoodCost()
    {
        return woodCost;
    }

    public ArrayList<Tile> getTilesWithCraftingItems()
    {
        return tilesWithCraftingItems;
    }

    public void addToTilesWithCraftingItems(Tile tile)
    {
        tilesWithCraftingItems.add(tile);
        tilesWithCraftingItems.sort(Comparator.comparingInt(Tile::getY));
    }

    public void removeTileObject(Tile tile)
    {
        if (tilesWithCraftingItems.contains(tile))
        {
            tilesWithCraftingItems.remove(tile);
            tilesWithCraftingItems.sort(Comparator.comparingInt(Tile::getY));
        }

        tile.unPlant();
        tile.setObject(null);
    }
}
