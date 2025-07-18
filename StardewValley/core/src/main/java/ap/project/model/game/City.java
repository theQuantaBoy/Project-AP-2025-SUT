package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.enums.MapKind;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.ShopType;
import ap.project.model.enums.TileTexture;
import ap.project.model.shops.*;

import java.util.ArrayList;

public class City extends Map
{
    private final String mapPath;
    Point[] playerPoints = new Point[4];

    private final ArrayList<Shop> shops = new ArrayList<>();

    public City()
    {
        super(MapTypes.TOWN);

        this.mapType = MapTypes.TOWN;
        this.mapPath = mapType.getMapPath();

//        initialize();
//        initializeShops();
    }

    private void initialize()
    {
        int[] dims = new int[2];
        this.mapData = MapLoader.loadMap(mapPath, dims);

        if (mapData == null)
        {
            throw new IllegalStateException("Failed to load city map: " + mapPath);
        }

        this.WIDTH = dims[0];
        this.HEIGHT = dims[1];
        this.tiles = new Tile[HEIGHT][WIDTH];

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                tiles[y][x] = new Tile(new Point(x, y));
                tiles[y][x].setType(TileTexture.VILLAGE_GRASS);
            }
        }

        applyMap();
        this.startingPoint = findFreeStartingPoint();

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                Tile tile = tiles[y][x];
                tile.setInCity();
            }
        }
    }

    public Point findFreeStartingPoint()
    {
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                Tile tile = tiles[y][x];
                if (tile.getTexture().equals(TileTexture.ROAD))
                {
                    boolean found = true;

                    for (Point point : playerPoints)
                    {
                        if (point != null && point.getX() == x && point.getY() == y)
                        {
                            found = false;
                            break;
                        }
                    }

                    if (found)
                    {
                        return tile.getPoint();
                    }
                }
            }
        }
        return null;
    }

    public Point[] getPlayerPoints()
    {
        return playerPoints;
    }

    public ArrayList<Point> getNpcLocations()
    {
        ArrayList<Point> npcLocations = new ArrayList<>();
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                Tile tile = tiles[y][x];
                if (tile.getTexture().equals(TileTexture.NPC_BLACKSMITH))
                {
                    npcLocations.add(tile.getPoint());
                }
            }
        }
        return npcLocations;
    }

    public boolean isNearShop(ShopType type)
    {
        TileTexture texture = getShopTileTexture(type);

        if (texture == null)
        {
            return false;
        }

        Player player = App.getCurrentGame().getCurrentPlayer();
        Point location = player.getLocation();

        for (int dy = -1; dy <= 1; dy++)
        {
            for (int dx = -1; dx <= 1; dx++)
            {
                if (isInBounds(location.getX() + dx, location.getY() + dy))
                {
                    Tile tile = tiles[location.getY() + dy][location.getX() + dx];
                    if (tile.getTexture().equals(texture))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private TileTexture getShopTileTexture(ShopType type)
    {
        switch (type)
        {
            case CARPENTER_SHOP ->
            {
                return TileTexture.SHOP_CARPENTER;
            }

            case BLACK_SMITH ->
            {
                return TileTexture.SHOP_BLACKSMITH;
            }

            case JOJA_MART ->
            {
                return TileTexture.SHOP_JOJAMART;
            }

            case STARDROP_SALOON ->
            {
                return TileTexture.SHOP_SALOON;
            }

            case PIERRE_GENERAL_STORE ->
            {
                return TileTexture.SHOP_PIERRE;
            }

            case FISH_SHOP ->
            {
                return TileTexture.SHOP_FISH;
            }

            case MARINE_RANCH ->
            {
                return TileTexture.SHOP_MARNIE;
            }

            default ->
            {
                return null;
            }
        }
    }

    public ArrayList<Tile> getShopTiles (ShopType type)
    {
        ArrayList<Tile> shopTiles = new ArrayList<>();
        TileTexture texture = getShopTileTexture(type);

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                Tile tile = tiles[y][x];
                if (tile.getTexture().equals(texture))
                {
                    shopTiles.add(tile);
                }
            }
        }

        return shopTiles;
    }

    private void initializeShops()
    {
        shops.add (new Blacksmith());
        shops.add (new JojaMart());
        shops.add (new TheStardropSaloon());
        shops.add (new PierresGeneralStore());
        shops.add (new FishShop());
        shops.add (new MarniesRanch());
        shops.add (new CarpentersShop());
    }

    public Shop getShop(ShopType type)
    {
        for (Shop shop : shops)
        {
            if (shop.getType() == type)
            {
                return shop;
            }
        }

        return null;
    }
}
