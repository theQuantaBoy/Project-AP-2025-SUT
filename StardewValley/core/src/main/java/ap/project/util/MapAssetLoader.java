package ap.project.util;

import ap.project.model.enums.TileTexture;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;

public final class MapAssetLoader
{
    public static LoadedMap loadFromTmx(String tmxPath)
    {
        return new LoadedMap(tmxPath);
    }

    public static class LoadedMap
    {
        public final int width;
        public final int height;
        public final TiledMap tiledMap;
        public final Tile[][] tiles;

        public LoadedMap(String tmxPath)
        {
            tiledMap = new TmxMapLoader().load(tmxPath);

            width = tiledMap.getProperties().get("width", Integer.class);
            height = tiledMap.getProperties().get("height", Integer.class);

            tiles = new Tile[height][width]; // [y][x]

            for (MapLayer layer : tiledMap.getLayers())
            {
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
