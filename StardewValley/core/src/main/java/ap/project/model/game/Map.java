package ap.project.model.game;

import ap.project.model.enums.MapKind;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.Season;
import ap.project.model.enums.TileTexture;
import ap.project.model.resources.ForagingCrop;
import ap.project.model.resources.ForagingTree;
import ap.project.model.resources.Tree;
import ap.project.model.shops.Shop;
import ap.project.screen.WorldScreen;
import ap.project.util.MapAssetLoader;
import ap.project.visual.MapVisual;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

public abstract class Map
{
    protected MapTypes mapType;
    protected Tile[][] tiles;
    protected int WIDTH, HEIGHT;
    protected Point startingPoint;
    protected java.util.Map<String, List<Point>> mapData;

    protected MapVisual visual;
    protected TiledMap tiledMap;

    protected int depth;

    public static final float TILE_SIZE = 16f * WorldScreen.MAP_SCALE;

    public Map() {}

    public Map(MapTypes mapType)
    {
        this.mapType = mapType;

        MapAssetLoader.LoadedMap loaded = MapAssetLoader.loadFromTmx(mapType.getName(), Season.Spring, mapType.getMapKind());

        this.WIDTH = loaded.width;
        this.HEIGHT = loaded.height;
        this.tiledMap = loaded.tiledMap;
        this.tiles = loaded.tiles;
        this.depth = loaded.depth;
        this.startingPoint = loaded.startingPoint;

        if (this instanceof Farm)
        {
            Farm farm = (Farm)this;
            farm.setHomePoint(loaded.cabinDoor);
            farm.setGreenhousePoint(loaded.greenhouseDoor);
            farm.setExitPoint(loaded.exitPoint);
        }

        if (this instanceof Shop)
        {
            Shop shop = (Shop)this;
            shop.setExteriorDoor(startingPoint);
        }

        if (this instanceof Cabin)
        {
            Cabin cabin = (Cabin) this;
            cabin.setRefrigeratorPoint(loaded.refrigeratorPoint);
            cabin.setOvenPoint(loaded.ovenPoint);
        }

        if (this instanceof City)
        {
            City city = (City)this;
            city.setBlacksmithDoor(loaded.blacksmithDoor);
            city.setCarpenterDoor(loaded.carpenterDoor);
            city.setFishShopDoor(loaded.fishShopDoor);
            city.setJojamartDoor(loaded.jojamartDoor);
            city.setPierreDoor(loaded.pierreDoor);
            city.setMarnieDoor(loaded.marnieDoor);
            city.setSaloonDoor(loaded.saloonDoor);
        }

        if (Gdx.app.getType() != Application.ApplicationType.HeadlessDesktop)
        {
            this.visual = new MapVisual(this, loaded.tiledMap);
        } else
        {
            this.visual = null;
        }
    }

    public int getDepth()
    {
        return depth;
    }

    public void setVisual(MapVisual visual)
    {
        this.visual = visual;
    }

    public MapTypes getMapType()
    {
        return mapType;
    }

    public Vector2 screenToWorld(float screenX, float screenY, OrthographicCamera cam)
    {
        Vector3 tmp3 = new Vector3(screenX, screenY, 0);
        cam.unproject(tmp3);
        return new Vector2(tmp3.x, tmp3.y);
    }

    public Point worldToTile(float worldX, float worldY)
    {
        int tileX = (int) (worldX / TILE_SIZE);
        int tileY = (int) ((HEIGHT * TILE_SIZE - worldY) / TILE_SIZE);

        return new Point(tileX, tileY);
    }

    public Vector2 tileToWorld(Tile tile)
    {
        if (tile == null) return null;

        int tileX = tile.getX();
        int tileY = tile.getY();

        float worldX = tileX * TILE_SIZE;
        float worldY = HEIGHT * TILE_SIZE - tileY * TILE_SIZE;

        return new Vector2(worldX, worldY);
    }

    public Point screenToTile(float screenX, float screenY, OrthographicCamera cam)
    {
        Vector2 world = screenToWorld(screenX, screenY, cam);
        return worldToTile(world.x, world.y);
    }

    public MapVisual getMapVisual()
    {
        return visual;
    }

    public void setTile(int x, int y, Tile tile)
    {
        this.tiles[y][x] = tile;
    }

    public Tile getTile(int x, int y)
    {
        if (isInBounds(x, y))
        {
            return tiles[y][x];
        }
        return null;
    }

    public boolean isInBounds(int x, int y)
    {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    public Point getStartingPoint()
    {
        return startingPoint;
    }

    public String getMapString(Point heroLocation, Point location, int HEIGHT, int WIDTH)
    {
        StringBuilder output = new StringBuilder();
        for (int y = 0; y < this.HEIGHT; y++)
        {
            for (int x = 0; x < this.WIDTH; x++)
            {
                if (isInBounds(x, y))
                {
                    Tile tile = tiles[y][x];
                    output.append(tile.getAppearance());
                }
            }
            output.append("\n");
        }
        return output.toString();
    }

    public boolean isWalkable(Tile tile)
    {
        GameObject object = tile.getObject();
        if (object != null)
        {
            return false;
        }

        switch (tile.getTexture())
        {
            case LAND:
            case GRASS:
            case QUARRY:
            case CABIN_INTERIOR_FLOOR:
            case GREEN_HOUSE_FLOOR:
            case GREEN_HOUSE_WOOD:
            case DECOR_TILE:
            case SHOP_DOOR:
            case VILLAGE_GRASS:
            case ROAD:
            case GARDEN:
            case FLOWER:
            case FLOOR:
            case BED_TILE:
            case EMPTY:
            case PATH:
                return true;

            case LAKE:
            case CABIN:
            case GREEN_HOUSE:
            case CABIN_WALL:
            case GREEN_HOUSE_WALL:
            case FENCE:
            case BUILDING:
            case CITY_BOARD:
            case TREE:
//            case EMPTY:
            case BOOK:
            case LAMP:
            case TABLE:
            case COMPUTER:
            case WALL:
            case SHOP_BLACKSMITH:
            case NPC_BLACKSMITH:
            case SHOP_JOJAMART:
            case NPC_JOJAMART:
            case SHOP_PIERRE:
            case NPC_PIERRE:
            case SHOP_CARPENTER:
            case NPC_CARPENTER:
            case SHOP_FISH:
            case NPC_FISH:
            case SHOP_MARNIE:
            case NPC_MARNIE:
            case SHOP_SALOON:
            case NPC_SALOON:
            case UNPASSABLE:
                return false;
        }

        return true;
    }

    public ArrayList<Point> getNeighbors(Point p)
    {
        ArrayList<Point> neighbors = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        for (int dir = 0; dir < 4; dir++)
        {
            int newX = p.getX() + dx[dir];
            int newY = p.getY() + dy[dir];

            if (isInBounds(newX, newY) && isWalkable(getTile(newX, newY)))
            {
                neighbors.add(new Point(newX, newY));
            }
        }

        return neighbors;
    }

    /**
     * Returns at most 4 direct neighbors (top, bottom, left, right) of the given point.
     * Similar to getNeighbors but with a different name for clarity.
     * @param p The center point to get neighbors for
     * @return List of neighboring points (max 4)
     */
    public ArrayList<Point> getDirectNeighbors(Point p) {
        ArrayList<Point> neighbors = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        for (int dir = 0; dir < 4; dir++) {
            int newX = p.getX() + dx[dir];
            int newY = p.getY() + dy[dir];

            if (isInBounds(newX, newY))
            {
                neighbors.add(new Point(newX, newY));
            }
        }

        return neighbors;
    }

    /**
     * Returns all tiles in a square around the original point with given radius.
     * For radius=1: 8 surrounding tiles (3x3 square)
     * For radius=2: 24 surrounding tiles (5x5 square minus center)
     * @param p The center point
     * @param radius The radius of the square (1 or 2)
     * @return List of surrounding points
     */
    public ArrayList<Point> getSquareNeighbors(Point p, int radius) {
        ArrayList<Point> neighbors = new ArrayList<>();

        if (radius < 1) {
            return neighbors; // Only support radius 1 or 2
        }

        for (int x = p.getX() - radius; x <= p.getX() + radius; x++) {
            for (int y = p.getY() - radius; y <= p.getY() + radius; y++) {
                // Skip the center point
                if (x == p.getX() && y == p.getY()) continue;

                if (isInBounds(x, y)) {
                    neighbors.add(new Point(x, y));
                }
            }
        }

        return neighbors;
    }

    /**
     * Returns all tiles within a circular radius of the original point.
     * Uses Euclidean distance to determine inclusion.
     * @param p The center point
     * @param radius The radius of the circle
     * @return List of points within the circular radius
     */
    public ArrayList<Point> getCircularNeighbors(Point p, float radius) {
        ArrayList<Point> neighbors = new ArrayList<>();

        if (radius <= 0) {
            return neighbors;
        }

        // Calculate bounds to check (square area that contains the circle)
        int minX = (int) Math.floor(p.getX() - radius);
        int maxX = (int) Math.ceil(p.getX() + radius);
        int minY = (int) Math.floor(p.getY() - radius);
        int maxY = (int) Math.ceil(p.getY() + radius);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Skip the center point
                if (x == p.getX() && y == p.getY()) continue;

                if (isInBounds(x, y)) {
                    // Calculate Euclidean distance
                    double distance = Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
                    if (distance <= radius) {
                        neighbors.add(new Point(x, y));
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Returns walkable tiles in a circular radius of the original point.
     * Similar to getCircularNeighbors but only includes walkable tiles.
     * @param p The center point
     * @param radius The radius of the circle
     * @return List of walkable points within the circular radius
     */
    public ArrayList<Point> getWalkableCircularNeighbors(Point p, float radius) {
        ArrayList<Point> neighbors = new ArrayList<>();

        if (radius <= 0) {
            return neighbors;
        }

        // Calculate bounds to check (square area that contains the circle)
        int minX = (int) Math.floor(p.getX() - radius);
        int maxX = (int) Math.ceil(p.getX() + radius);
        int minY = (int) Math.floor(p.getY() - radius);
        int maxY = (int) Math.ceil(p.getY() + radius);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Skip the center point
                if (x == p.getX() && y == p.getY()) continue;

                if (isInBounds(x, y)) {
                    // Calculate Euclidean distance
                    double distance = Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
                    if (distance <= radius && isWalkable(getTile(x, y))) {
                        neighbors.add(new Point(x, y));
                    }
                }
            }
        }

        return neighbors;
    }

    public ArrayList<Point> findShortestPath(Point from, Point to)
    {
        if (!isInBounds(from.getX(), from.getY()) ||
            !isInBounds(to.getX(), to.getY()) ||
            !isWalkable(getTile(from.getX(), from.getY())) ||
            !isWalkable(getTile(to.getX(), to.getY()))) {
            return null;
        }

        Queue<Point> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        java.util.Map<Point, Point> parentMap = new java.util.HashMap<>();

        queue.add(from);
        visited.add(from);

        while (!queue.isEmpty())
        {
            Point current = queue.poll();

            // Path found
            if (current.equals(to))
            {
                break;
            }

            // Get all possible neighbors (adjacent tiles)
            for (Point neighbor : getNeighbors(current))
            {
                // Skip if out of bounds
                if (!isInBounds(neighbor.getX(), neighbor.getY())) {
                    continue;
                }

                // Skip if not walkable
                Tile neighborTile = getTile(neighbor.getX(), neighbor.getY());
                if (!isWalkable(neighborTile))
                {
                    continue;
                }

                // Process new valid neighbor
                if (!visited.contains(neighbor))
                {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct path if destination was reached
        if (!parentMap.containsKey(to)) {
            return null; // No path found
        }

        ArrayList<Point> path = new ArrayList<>();
        for (Point at = to; at != null; at = parentMap.get(at)) {
            path.add(at);
        }

        Collections.reverse(path);
        return path;
    }

    public int calculateEnergy(Point from, Point to)
    {
        List<Point> path = findShortestPath(from, to);
        if (path == null) return -1;

        int distance = path.size() - 1;
        int turns = countTurns(path);

        return (distance + (10 * turns)) / 20;
    }

    protected int countTurns(List<Point> path)
    {
        if (path.size() < 3) return 0;

        int turns = 0;
        for (int i = 2; i < path.size(); i++)
        {
            int dx1 = path.get(i - 1).getX() - path.get(i - 2).getX();
            int dy1 = path.get(i - 1).getY() - path.get(i - 2).getY();
            int dx2 = path.get(i).getX() - path.get(i - 1).getX();
            int dy2 = path.get(i).getY() - path.get(i - 1).getY();

            if (dx1 != dx2 || dy1 != dy2)
            {
                turns += 1;
            }
        }
        return turns;
    }

    public Point findFurthestAvailablePoint(Point origin, Point destination, float availableEnergy)
    {
        ArrayList<Point> path = findShortestPath(origin, destination);

        if (path == null || path.isEmpty()) return null;

        Point lastReachable = path.getFirst();

        int usedEnergy = 0;

        for (int i = 1; i < path.size(); i++)
        {
            Point prev = path.get(i - 1);
            Point curr = path.get(i);

            int dx = curr.getX() - prev.getX();
            int dy = curr.getY() - prev.getY();

            usedEnergy += 1;

            if (i >= 2)
            {
                Point beforePrev = path.get(i - 2);
                int oldDx = prev.getX() - beforePrev.getX();
                int oldDy = prev.getY() - beforePrev.getY();

                if (dx != oldDx || dy != oldDy)
                {
                    usedEnergy += 10;
                }
            }

            int totalEnergyCost = usedEnergy / 20;

            if (totalEnergyCost > availableEnergy)
            {
                break;
            } else
            {
                lastReachable = curr;
            }
        }

        return lastReachable;
    }

    public String showAround(Point location)
    {
        StringBuilder output = new StringBuilder();

        output.append("\n");

        int x = location.getX();
        int y = location.getY();

        // Column headers
        output.append(" "); // 2 spaces for top-left corner
        for (int j = -2; j <= 2; j++)
        {
            int col = x + j;
            if (isInBounds(col, y))
            {
                output.append(String.format("%02d", col));
            }
            else
            {
                output.append("  "); // 2 spaces for missing column label
            }
            output.append(" "); // space between columns (always)
        }
        output.append("\n");

        // Rows
        for (int i = -2; i <= 2; i++)
        {
            int row = y + i;

            if (isInBounds(x, row))
            {
                output.append(String.format("%02d", row)).append(" ");
            }
            else
            {
                output.append("   "); // 2 for missing label, 1 for space
            }

            for (int j = -2; j <= 2; j++)
            {
                int col = x + j;

                if (isInBounds(col, row))
                {
                    Tile tile = tiles[row][col];
                        output.append(tile.getAppearance());
                }
                else
                {
                    output.append("⬛");
                }
            }

            output.append("\n");
        }

        return output.toString();
    }


    public int getWidth()
    {
        return WIDTH;
    }

    public int getHeight()
    {
        return HEIGHT;
    }

    public String getMapWithPath(Point from, Point to)
    {
        ArrayList<Point> path = findShortestPath(from, to);

        StringBuilder output = new StringBuilder();

        for (int y = 0; y < this.HEIGHT; y++)
        {
            for (int x = 0; x < this.WIDTH; x++)
            {
                Point current = new Point(x, y);
                Tile tile = tiles[y][x];

                if (path != null && isInPath(path, current) && !current.equals(from))
                {
                    output.append("\uD83D\uDFE7"); // Path
                }
                else
                {
                    output.append(tile.getAppearance());
                }
            }
            output.append("\n");
        }

        return output.toString().trim();
    }

    public boolean isInPath(ArrayList<Point> path, Point point)
    {
        for (Point at : path)
        {
            if (at.equals(point))
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Tile> getAllPlantTiles()
    {
        ArrayList<Tile> allPlantTiles = new ArrayList<>();
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                Tile tile = tiles[y][x];
                if (tile.getTexture().equals(TileTexture.LAND) || tile.getTexture().equals(TileTexture.GRASS))
                {
                    if (tile.hasPlants())
                    {
                        allPlantTiles.add(tile);
                    }
                }
            }
        }
        return allPlantTiles;
    }

    public Tile[][] getTiles()
    {
        return tiles;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Map map)) return false;
        return WIDTH == map.WIDTH && HEIGHT == map.HEIGHT && depth == map.depth && mapType == map.mapType;
    }

    public static boolean isNear(Point point, Point other)
    {

        int dx = Math.abs(point.getX() - other.getX());
        int dy = Math.abs(point.getY() - other.getY());

        return (dx <= 2 && dy <= 2) && !(dx == 0 && dy == 0);
    }

    public static boolean isNearOrOn(Point point, Point other)
    {
        int dx = Math.abs(point.getX() - other.getX());
        int dy = Math.abs(point.getY() - other.getY());

        return (dx <= 1 && dy <= 1);
    }
}

