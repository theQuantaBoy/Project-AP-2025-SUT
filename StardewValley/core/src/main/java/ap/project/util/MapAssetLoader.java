package ap.project.util;

import ap.project.model.enums.MapKind;
import ap.project.model.enums.Season;
import ap.project.model.enums.TileTexture;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
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
        }

        return new LoadedMap(fileName);
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

        public LoadedMap(String tmxPath)
        {
            tiledMap = new TmxMapLoader().load(tmxPath);

            width = tiledMap.getProperties().get("width", Integer.class);
            height = tiledMap.getProperties().get("height", Integer.class);
            depth = tiledMap.getLayers().size();

            int startingPointX = tiledMap.getProperties().get("starting_point_x", Integer.class);
            int startingPointY = tiledMap.getProperties().get("starting_point_y", Integer.class);

            startingPoint = new Point(startingPointX, startingPointY);

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
                            Gdx.app.log("MapAssetLoader", "Unknown TileTexture \"" + typeName + "\" @ " + x + "," + y);
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
