package ap.project.util;

import ap.project.model.enums.MapKind;
import ap.project.model.enums.Season;
import ap.project.model.enums.ShopType;
import ap.project.model.enums.TileTexture;
import ap.project.model.game.Map;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import ap.project.screen.WorldScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;

public final class MapAssetLoader
{
    public static LoadedMap loadFromTmx(String baseMapName, Season season, MapKind mapKind)
    {
        String fileName = "";

        switch (mapKind)
        {
            case FARM -> fileName = String.format("maps/farm/%s/%s_%s.tmx", baseMapName, baseMapName, season.getName());
            case TOWN -> fileName = String.format("maps/general/town/town_%s.tmx", season.getName());
            case HOUSE -> fileName = "maps/general/house/house_interior.tmx";
            case GREEN_HOUSE -> fileName = "maps/general/greenhouse/green_house.tmx";
            case SHOP -> {
                ShopType shopType = ShopType.getShop(baseMapName);
                if (shopType != null) {
                    fileName = shopType.getTmxFileName();
                } else {
                    throw new IllegalArgumentException("No shop found with name: " + baseMapName);
                }
            }
        }

        return new LoadedMap(fileName, mapKind);
    }

    public static class LoadedMap
    {
        public final int width;
        public final int height;
        public final int depth;
        public final TiledMap tiledMap;
        public final Tile[][] tiles;
        public final Tile[][][] layerTiles;
        public final Point startingPoint;
        public Point cabinDoor;
        public Point greenhouseDoor;
        public Point exitPoint;
        public Point refrigeratorPoint;
        public Point ovenPoint;
        public Point blacksmithDoor;
        public Point carpenterDoor;
        public Point fishShopDoor;
        public Point jojamartDoor;
        public Point marnieDoor;
        public Point pierreDoor;
        public Point saloonDoor;

        public LoadedMap(String tmxPath, MapKind mapKind)
        {
            tiledMap = new TmxMapLoader().load(tmxPath);

            width = tiledMap.getProperties().get("width", Integer.class);
            height = tiledMap.getProperties().get("height", Integer.class);
            depth = tiledMap.getLayers().size();

            int startingPointX = tiledMap.getProperties().get("starting_point_x", Integer.class);
            int startingPointY = tiledMap.getProperties().get("starting_point_y", Integer.class);
            startingPoint = new Point(startingPointX, startingPointY);

            if (mapKind == MapKind.FARM)
            {
                int cabinDoorX = tiledMap.getProperties().get("cabin_door_x", Integer.class);
                int cabinDoorY = tiledMap.getProperties().get("cabin_door_y", Integer.class);
                cabinDoor = new Point(cabinDoorX, cabinDoorY);

                int greenhouseDoorX = tiledMap.getProperties().get("greenhouse_door_x", Integer.class);
                int greenhouseDoorY = tiledMap.getProperties().get("greenhouse_door_y", Integer.class);
                greenhouseDoor = new Point(greenhouseDoorX, greenhouseDoorY);

                int exitPointX = tiledMap.getProperties().get("exit_x", Integer.class);
                int exitPointY = tiledMap.getProperties().get("exit_y", Integer.class);
                exitPoint = new Point(exitPointX, exitPointY);
            }

            if (mapKind == MapKind.HOUSE)
            {
                int refrigeratorPointX = tiledMap.getProperties().get("refrigerator_point_x", Integer.class);
                int refrigeratorPointY = tiledMap.getProperties().get("refrigerator_point_y", Integer.class);
                refrigeratorPoint = new Point(refrigeratorPointX, refrigeratorPointY);

                int ovenPointX = tiledMap.getProperties().get("oven_point_x", Integer.class);
                int ovenPointY = tiledMap.getProperties().get("oven_point_y", Integer.class);
                ovenPoint = new Point(ovenPointX, ovenPointY);
            }

            if (mapKind == MapKind.TOWN)
            {
                int blacksmithPointX = tiledMap.getProperties().get("blacksmith_door_x", Integer.class);
                int blacksmithPointY = tiledMap.getProperties().get("blacksmith_door_y", Integer.class);
                blacksmithDoor = new Point(blacksmithPointX, blacksmithPointY);

                int carpenterPointX = tiledMap.getProperties().get("carpenter_door_x", Integer.class);
                int carpenterPointY = tiledMap.getProperties().get("carpenter_door_y", Integer.class);
                carpenterDoor = new Point(carpenterPointX, carpenterPointY);

                int fishShopPointX = tiledMap.getProperties().get("fishshop_door_x", Integer.class);
                int fishShopPointY = tiledMap.getProperties().get("fishshop_door_y", Integer.class);
                fishShopDoor = new Point(fishShopPointX, fishShopPointY);

                int jojamartPointX = tiledMap.getProperties().get("jojamart_door_x", Integer.class);
                int jojamartPointY = tiledMap.getProperties().get("jojamart_door_y", Integer.class);
                jojamartDoor  = new Point(jojamartPointX, jojamartPointY);

                int marniePointX = tiledMap.getProperties().get("marnie_door_x", Integer.class);
                int marniePointY = tiledMap.getProperties().get("marnie_door_y", Integer.class);
                marnieDoor = new Point(marniePointX, marniePointY);

                int pierrePointX = tiledMap.getProperties().get("pierre_door_x", Integer.class);
                int pierrePointY = tiledMap.getProperties().get("pierre_door_y", Integer.class);
                pierreDoor = new Point(pierrePointX, pierrePointY);

                int saloonPointX = tiledMap.getProperties().get("saloon_door_x", Integer.class);
                int saloonPointY = tiledMap.getProperties().get("saloon_door_y", Integer.class);
                saloonDoor = new Point(saloonPointX, saloonPointY);
            }

            tiles = new Tile[height][width]; // [y][x]
            layerTiles = new Tile[depth][height][width];

            for (int i = 0; i < tiledMap.getLayers().size(); i++)
            {
                MapLayer layer = tiledMap.getLayers().get(i);
                if (!(layer instanceof TiledMapTileLayer tiledLayer)) continue;

                for (int x = 0; x < width; x++)
                {
                    for (int y = 0; y < height; y++)
                    {
                        TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                        if (cell == null || cell.getTile() == null) continue;

                        MapProperties props = cell.getTile().getProperties();
                        String typeName = props.get("type", String.class);

                        int flippedY = height - 1 - y;

                        layerTiles[i][flippedY][x] = new Tile(new Point(x, flippedY), typeName);

                        if (typeName == null)
                        {
                            tiles[flippedY][x] = new Tile(TileTexture.EMPTY, new Point(x, flippedY));
                            continue;
                        }

                        TileTexture texture = TileTexture.mapTypeNameToTexture(typeName);
                        if (texture == null)
                        {
                            continue;
                        }

                        tiles[flippedY][x] = new Tile(texture, new Point(x, flippedY));
                    }
                }
            }

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    if (tiles[y][x] == null)
                    {
                        tiles[y][x] = new Tile(TileTexture.EMPTY, new Point(x, y));
                    }
                }
            }
        }
    }
}
