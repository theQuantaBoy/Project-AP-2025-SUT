package ap.project.model.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


public class MapVisual
{
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer renderer;

    public MapVisual(TiledMap tiledMap)
    {
        this.tiledMap = tiledMap;
        this.renderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f);
    }

    public void render(OrthographicCamera cam, ShaderProgram shader)
    {
        renderer.setView(cam);
        renderer.getBatch().setShader(shader);
        renderer.render();
        renderer.getBatch().setShader(null);
    }

    public void dispose()
    {
        tiledMap.dispose();
        renderer.dispose();
    }

    public TiledMap getTiledMap()
    {
        return tiledMap;
    }

    public OrthogonalTiledMapRenderer getRenderer()
    {
        return renderer;
    }
}
