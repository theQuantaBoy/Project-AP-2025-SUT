package ap.project.network.shared.DTO;

import ap.project.model.enums.MapTypes;
import ap.project.model.game.Farm;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;

import java.util.ArrayList;

public class FarmDTO
{
    public MapTypes mapType;

    public int width;
    public int height;

    public TileDTO[][] tiles;

    public ArrayList<Point> tilesWithResources = new ArrayList<>();
    public ArrayList<Point> tilesWithForagingTrees = new ArrayList<>();
    public ArrayList<Point> tilesWithForagingItems = new ArrayList<>();
    public ArrayList<Point> plantingTiles = new ArrayList<>();
    public ArrayList<Point> tilesWIthCraftingItems = new ArrayList<>();
    public ArrayList<Point> lightningTiles = new ArrayList<>();
    public ArrayList<Point> hazardTiles = new ArrayList<>();

    public FarmDTO() {}

    public FarmDTO(Farm farm)
    {
        this.mapType = farm.getMapType();

        this.width = farm.getWidth();
        this.height = farm.getHeight();

        this.tiles = new TileDTO[this.height][this.width];

        for(int y = 0; y < this.height; y++)
        {
            for (int x =  0; x < this.width; x++)
            {
                this.tiles[y][x] = new TileDTO(farm.getTile(x, y));
            }
        }

        for (Tile tile : farm.getTilesWithResources())
        {
            tilesWithResources.add(tile.getPoint());
        }

        for (Tile tile : farm.getTilesWithForagingTrees())
        {
            tilesWithForagingTrees.add(tile.getPoint());
        }

        for (Tile tile : farm.getTilesWithForagingItems())
        {
            tilesWithForagingItems.add(tile.getPoint());
        }

        for (Tile tile : farm.getPlantingTiles())
        {
            plantingTiles.add(tile.getPoint());
        }

        for (Tile tile : farm.getTilesWithCraftingItems())
        {
            tilesWIthCraftingItems.add(tile.getPoint());
        }

        for (Tile tile : farm.getLightningTiles())
        {
            lightningTiles.add(tile.getPoint());
        }

        for (Tile tile : farm.getHazardTiles())
        {
            hazardTiles.add(tile.getPoint());
        }
    }
}
