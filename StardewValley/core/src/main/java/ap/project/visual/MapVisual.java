package ap.project.visual;

import ap.project.model.enums.GameObjectType;
import ap.project.model.game.*;
import ap.project.screen.TestScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import static ap.project.screen.TestScreen.MAP_SCALE;


public class MapVisual
{
    private final Map map;
    private TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer renderer;
    private Texture woodTexture, stoneTexture;

    public MapVisual(Map map, TiledMap tiledMap)
    {
        this.map = map;
        this.tiledMap = tiledMap;
        this.renderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f);

        woodTexture = new Texture(Gdx.files.internal(GameObjectType.WOOD.getSpritePath()));
        woodTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        stoneTexture = new Texture(Gdx.files.internal(GameObjectType.STONE.getSpritePath()));
        stoneTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void updateMap(TiledMap tiledMap)
    {
        this.tiledMap = tiledMap;
    }

    public void render(OrthographicCamera cam)
    {
        renderer.setView(cam);
        renderer.render();
        drawResources(cam);
        renderer.getBatch().setShader(null);
    }

    public void dispose()
    {
        tiledMap.dispose();
        renderer.dispose();
    }

    public void drawResources(OrthographicCamera cam)
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            renderer.getBatch().begin();

            for (Tile tile : farm.getTilesWithResources())
            {
                switch (tile.getObject().getObjectType())
                {
                    case WOOD -> drawTile(tile, woodTexture);
                    case STONE -> drawTile(tile, stoneTexture);
                }
            }

            renderer.getBatch().end();
        }
    }

    private void drawTile(Tile tile)
    {
        Vector2 location = map.tileToWorld(tile);
        renderer.getBatch().draw(new Texture(Gdx.files.internal(tile.getObject().getObjectType().getSpritePath())),
            location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
    }

    private void drawTile(Tile tile, Texture texture)
    {
        Vector2 location = map.tileToWorld(tile);
        renderer.getBatch().draw(texture, location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
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
