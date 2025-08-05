package ap.project.util;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;

/**
 * A TmxMapLoader that never touches Gdx.gl, so it works in HeadlessDesktop.
 */
public class ServerMapLoader extends TmxMapLoader {

    @Override
    public TiledMap load(String fileName, Parameters parameters) {
        // resolve the TMX and parse XML
        FileHandle tmxFile = resolve(fileName);
        this.root = xml.parse(tmxFile);

        // pass in an ImageResolver that returns only empty TextureRegions
        return loadTiledMap(tmxFile, parameters, new ImageResolver() {
            @Override
            public TextureRegion getImage(String name) {
                // no-op: returns an empty region, never calls Gdx.gl
                return new TextureRegion();
            }
        });
    }
}
