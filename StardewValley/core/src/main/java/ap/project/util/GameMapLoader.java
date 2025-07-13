package ap.project.util;

import ap.project.model.enums.TileTexture;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.utils.Array;

public final class GameMapLoader {

    /** Load a TMX and build a 2‑D Tile array */
    public static Tile[][] load(String internalPath) {
        TiledMap map = new TmxMapLoader().load(internalPath);

        int mapWidth  = map.getProperties().get("width",  Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);

        Tile[][] tiles = new Tile[mapHeight][mapWidth]; // [y][x]

        for (MapLayer layer : map.getLayers()) {
            if (!(layer instanceof TiledMapTileLayer tiledLayer)) continue;

            for (int x = 0; x < mapWidth; x++) {
                for (int y = 0; y < mapHeight; y++) {
                    TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                    if (cell == null || cell.getTile() == null) continue;

                    MapProperties props = cell.getTile().getProperties();
                    String typeName = props.get("type", String.class);

                    if (typeName == null) {
                        tiles[mapHeight - 1 - y][x] = new Tile(TileTexture.EMPTY, new Point(x, y));
                        continue;
                    }

                    TileTexture texture = TileTexture.mapTypeNameToTexture(typeName);
                    if (texture == null) {
                        Gdx.app.log("GameMapLoader", "Unknown TileTexture \"" + typeName + "\" @ " + x + "," + y);
                        continue;
                    }

                    tiles[mapHeight - 1 - y][x] = new Tile(texture, new Point(x, y));
                }
            }
        }

        return tiles;
    }


    private GameMapLoader() {}  // utility class
}
