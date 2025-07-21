package ap.project.visual;

import ap.project.model.App.App;
import ap.project.model.enums.GameAnimationType;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.Weather;
import ap.project.model.game.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;

import static ap.project.model.game.Map.TILE_SIZE;
import static ap.project.screen.WorldScreen.MAP_SCALE;


public class MapVisual
{
    private final Map map;
    private TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer renderer;
    private Texture woodTexture, stoneTexture;

    private ArrayList<GameAnimation> rainAnimations = new ArrayList<>();
    private final int RAIN_AMOUNT = 40;
    private double rainSpawnTimer = 0f;

    private ArrayList<GameAnimation> snowAnimations = new ArrayList<>();
    private final int SNOW_AMOUNT = 40;
    private double snowSpawnTimer = 0f;

    public MapVisual(Map map, TiledMap tiledMap)
    {
        this.map = map;
        this.tiledMap = tiledMap;
        this.renderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f);

        woodTexture = new Texture(Gdx.files.internal(GameObjectType.WOOD.getPath()));
        woodTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        stoneTexture = new Texture(Gdx.files.internal(GameObjectType.STONE.getPath()));
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
        drawRainAnimations();
        drawSnowAnimations();
        renderer.getBatch().setShader(null);
    }

    public void dispose()
    {
        tiledMap.dispose();
        renderer.dispose();
    }

    public void update(float delta)
    {
        rain(delta);
        snow(delta);
    }

    public void rain(float delta)
    {
        rainSpawnTimer += delta;

        if (shouldRain())
        {
            if (rainSpawnTimer >= 0.45f)
            {
                for (int i = 0; i < RAIN_AMOUNT; i++)
                {
                    int width = (int) (map.getWidth() * TILE_SIZE);
                    int height = (int) (map.getHeight() * TILE_SIZE);

                    int x = MathUtils.random(0, width);
                    int y = MathUtils.random(0, height);
                    Vector2 position = new Vector2(x, y);

                    if (Math.random() < 0.5f)
                    {
                        rainAnimations.add(new GameAnimation(GameAnimationType.RAIN_TYPE_1, position));
                    } else
                    {
                        rainAnimations.add(new GameAnimation(GameAnimationType.RAIN_TYPE_2, position));
                    }
                }

                rainSpawnTimer = 0f;
            }
        }

        for (Iterator<GameAnimation> it = rainAnimations.iterator(); it.hasNext();)
        {
            GameAnimation a = it.next();
            a.update(delta);
            if (a.isFinished()) it.remove();
        }

        if (!shouldRain())
        {
            rainAnimations.clear();
            rainSpawnTimer = 0f;
        }
    }

    public void snow(float delta)
    {
        snowSpawnTimer += delta;

        if (shouldSnow())
        {
            if (snowSpawnTimer >= 0.45f)
            {
                for (int i = 0; i < SNOW_AMOUNT; i++)
                {
                    int width = (int) (map.getWidth() * TILE_SIZE);
                    int height = (int) (map.getHeight() * TILE_SIZE);

                    int x = MathUtils.random(0, width);
                    int y = MathUtils.random(0, height);
                    Vector2 position = new Vector2(x, y);


                    snowAnimations.add(new GameAnimation(GameAnimationType.SNOW, position));
                }

                snowSpawnTimer = 0f;
            }
        }

        for (Iterator<GameAnimation> it = snowAnimations.iterator(); it.hasNext();)
        {
            GameAnimation a = it.next();
            a.update(delta);
            if (a.isFinished()) it.remove();
        }

        if (!shouldSnow())
        {
            snowAnimations.clear();
            snowSpawnTimer = 0f;
        }
    }

    public void drawRainAnimations()
    {
        renderer.getBatch().begin();

        for (GameAnimation a : rainAnimations)
        {
            a.render(renderer.getBatch());
        }

        renderer.getBatch().end();
    }

    public void drawSnowAnimations()
    {
        renderer.getBatch().begin();

        for (GameAnimation a : snowAnimations)
        {
            a.render(renderer.getBatch());
        }

        renderer.getBatch().end();
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
        renderer.getBatch().draw(new Texture(Gdx.files.internal(tile.getObject().getObjectType().getPath())),
            location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
    }

    private void drawTile(Tile tile, Texture texture)
    {
        Vector2 location = map.tileToWorld(tile);
//        renderer.getBatch().draw(texture, location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
        if (tile.getObject() != null)
        {
            renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(), location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
        }
    }

    public TiledMap getTiledMap()
    {
        return tiledMap;
    }

    public OrthogonalTiledMapRenderer getRenderer()
    {
        return renderer;
    }

    public boolean shouldRain()
    {
        Game game = App.getCurrentGame();
        Time time = game.getCurrentTime();
        Player player = game.getCurrentPlayer();

        return ((time.getCurrentWeather() == Weather.Rain) && (player.isInCity() || player.isInFarm()));
    }

    public boolean shouldSnow()
    {
        Game game = App.getCurrentGame();
        Time time = game.getCurrentTime();
        Player player = game.getCurrentPlayer();

        return ((time.getCurrentWeather() == Weather.Snow) && (player.isInCity() || player.isInFarm()));
    }
}
