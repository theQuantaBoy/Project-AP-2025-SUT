package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.enums.MapKind;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.ShopType;
import ap.project.model.enums.TileTexture;
import ap.project.model.shops.*;

import java.util.ArrayList;
import java.util.HashMap;

public class City extends Map
{
    private final String mapPath;
    Point[] playerPoints = new Point[4];
    HashMap<Point, Shop> shopDoors = new HashMap<>();

    private Point blacksmithDoor;
    private Point carpenterDoor;
    private Point fishShopDoor;
    private Point jojamartDoor;
    private Point marnieDoor;
    private Point pierreDoor;
    private Point saloonDoor;

    private final ArrayList<Shop> shops = new ArrayList<>();

    public City()
    {
        super(MapTypes.TOWN);

        this.mapType = MapTypes.TOWN;
        this.mapPath = mapType.getMapPath();

//        initialize();
        initializeShops();
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
        Blacksmith blacksmith = new Blacksmith();
        JojaMart jojaMart = new JojaMart();
        TheStardropSaloon theStardropSaloon = new TheStardropSaloon();
        PierresGeneralStore pierresGeneralStore = new PierresGeneralStore();
        FishShop fishShop = new FishShop();
        MarniesRanch marniesRanch = new MarniesRanch();
        CarpentersShop carpentersShop = new CarpentersShop();

        shops.add (blacksmith);
        shops.add (jojaMart);
        shops.add (theStardropSaloon);
        shops.add (pierresGeneralStore);
        shops.add (fishShop);
        shops.add (marniesRanch);
        shops.add (carpentersShop);

        shopDoors.put(blacksmithDoor, blacksmith);
        shopDoors.put(jojamartDoor, jojaMart);
        shopDoors.put(saloonDoor, theStardropSaloon);
        shopDoors.put(pierreDoor, pierresGeneralStore);
        shopDoors.put(fishShopDoor, fishShop);
        shopDoors.put(marnieDoor, marniesRanch);
        shopDoors.put(carpenterDoor, carpentersShop);
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

    public HashMap<Point, Shop> getShopDoors()
    {
        return shopDoors;
    }

    public Point getExteriorDoor(ShopType type)
    {
        for (java.util.Map.Entry<Point, Shop> entry : shopDoors.entrySet())
        {
            if (entry.getValue().getType() == type)
            {
                return entry.getKey();
            }
        }

        return null;
    }

    public void setBlacksmithDoor(Point blacksmithDoor)
    {
        this.blacksmithDoor = blacksmithDoor;
    }

    public void setCarpenterDoor(Point carpenterDoor)
    {
        this.carpenterDoor = carpenterDoor;
    }

    public void setFishShopDoor(Point fishShopDoor)
    {
        this.fishShopDoor = fishShopDoor;
    }

    public void setJojamartDoor(Point jojamartDoor)
    {
        this.jojamartDoor = jojamartDoor;
    }

    public void setMarnieDoor(Point marnieDoor)
    {
        this.marnieDoor = marnieDoor;
    }

    public void setPierreDoor(Point pierreDoor)
    {
        this.pierreDoor = pierreDoor;
    }

    public void setSaloonDoor(Point saloonDoor)
    {
        this.saloonDoor = saloonDoor;
    }

    public Shop getShopAtLocation(Point location) {
        return shopDoors.get(location);
    }
}
