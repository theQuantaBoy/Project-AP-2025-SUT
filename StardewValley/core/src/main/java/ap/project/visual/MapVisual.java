package ap.project.visual;

import ap.project.model.game.Map;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


public class MapVisual
{
    private final Map map;
    private TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer renderer;

    public MapVisual(Map map, TiledMap tiledMap)
    {
        this.map = map;
        this.tiledMap = tiledMap;
        this.renderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f);
    }

    public void updateMap(TiledMap tiledMap)
    {
        this.tiledMap = tiledMap;
    }

    public void render(OrthographicCamera cam)
    {
        renderer.setView(cam);
        renderer.render();
        renderer.getBatch().setShader(null);
    }

    public void dispose()
    {
        tiledMap.dispose();
        renderer.dispose();
    }

    public void drawResources()
    {

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
