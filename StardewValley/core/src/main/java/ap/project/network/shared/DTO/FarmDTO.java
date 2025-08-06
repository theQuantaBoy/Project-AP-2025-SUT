package ap.project.network.shared.DTO;

import ap.project.model.enums.MapTypes;
import ap.project.model.game.Farm;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;

import java.util.ArrayList;

public class FarmDTO
{
    public MapTypes mapType;

    public ArrayList<TileDTO> tilesWithResources = new ArrayList<>();
    public ArrayList<TileDTO> tilesWithForagingTrees = new ArrayList<>();
    public ArrayList<TileDTO> tilesWithForagingItems = new ArrayList<>();
    public ArrayList<TileDTO> plantingTiles = new ArrayList<>();
    public ArrayList<TileDTO> tilesWIthCraftingItems = new ArrayList<>();
    public ArrayList<TileDTO> lightningTiles = new ArrayList<>();
    public ArrayList<TileDTO> hazardTiles = new ArrayList<>();

    public FarmDTO() {}

    public FarmDTO(Farm farm)
    {
        this.mapType = farm.getMapType();

        for (Tile tile : farm.getTilesWithResources())
        {
            tilesWithResources.add(new TileDTO(tile));
        }

        for (Tile tile : farm.getTilesWithForagingTrees())
        {
            tilesWithForagingTrees.add(new TileDTO(tile));
        }

        for (Tile tile : farm.getTilesWithForagingItems())
        {
            tilesWithForagingItems.add(new TileDTO(tile));
        }

        for (Tile tile : farm.getPlantingTiles())
        {
            plantingTiles.add(new TileDTO(tile));
        }

        for (Tile tile : farm.getTilesWithCraftingItems())
        {
            tilesWIthCraftingItems.add(new TileDTO(tile));
        }

        for (Tile tile : farm.getLightningTiles())
        {
            lightningTiles.add(new TileDTO(tile));
        }

        for (Tile tile : farm.getHazardTiles())
        {
            hazardTiles.add(new TileDTO(tile));
        }
    }
}
