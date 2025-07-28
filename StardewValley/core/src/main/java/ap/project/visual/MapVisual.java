package ap.project.visual;

import ap.project.model.App.App;
import ap.project.model.building.CraftingItem;
import ap.project.model.enums.*;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.TreeType;
import ap.project.model.game.*;
import ap.project.model.resources.Crop;
import ap.project.model.resources.ForagingTree;
import ap.project.model.resources.Plant;
import ap.project.model.resources.Tree;
import ap.project.screen.WorldScreen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    private static Map map;
    private TiledMap tiledMap;
    private static OrthogonalTiledMapRenderer renderer;

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
    }

    public void render(OrthographicCamera cam)
    {
        renderer.setView(cam);
        renderer.render();
        drawPlantingTiles();
        drawResources();
        drawForagingItems();
        renderer.getBatch().setShader(null);
    }

    public void renderInFrontOfCharacter(WorldScreen worldScreen)
    {
        Batch batch = renderer.getBatch();
        batch.begin();

        drawPlants();
        drawForagingTrees();
        drawTilesWithCraftingItems();

        batch.end();

        // Weather effects (draw last)
        drawRainAnimations();
        drawSnowAnimations();
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

    public void drawPlantingTiles()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm)map;
            ArrayList<Tile> plantingTiles = farm.getPlantingTiles();

            renderer.getBatch().begin();

            for (Tile tile : plantingTiles)
            {
                drawTileEffect(tile, EffectType.PLOUGHED_TILE);

                if (tile.isFertilized())
                {
                    drawTileEffect(tile, EffectType.FERTILIZED_TILE);
                }

                if (tile.hasPlants())
                {
                    Plant plant = (Plant) tile.getObject();
                    if (plant.hasBeenWateredToday())
                    {
                        drawTileEffect(tile, EffectType.WATERED_TILE);
                    }
                }
            }

            renderer.getBatch().end();
        }
    }

    public void drawPlants()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm)map;
            ArrayList<Tile> plantingTiles = farm.getPlantingTiles();

            for (Tile tile : plantingTiles)
            {
                if (tile.hasPlants())
                {
                    Plant plant = (Plant) tile.getObject();

                    if (plant instanceof Crop)
                    {
                        Crop crop = (Crop) plant;
                        CropType cropType = crop.getCropType();
                        Texture currentStage = cropType.getStageTextures().get(crop.getCurrentStage());
                        drawTileTexture(tile, currentStage);
                    }

                    if (plant instanceof Tree)
                    {
                        Tree tree = (Tree) plant;
                        int currentStage = tree.getCurrentStage();

                        Texture texture;
                        if (currentStage < 4)
                        {
                            texture = tree.getTreeType().getStageTextures().get(currentStage);
                        } else
                        {
                            if (tree.canHarvest())
                            {
                                texture = tree.getTreeType().getWithProductTexture();
                            } else
                            {
                                Season season = App.getCurrentGame().getCurrentTime().getSeason();
                                texture = tree.getTreeType().getSeasonTextures().get(season.toInteger());
                            }
                        }

                        drawTileTreeTexture(tile, texture);
                    }
                }
            }
        }
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

    public void drawResources()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            renderer.getBatch().begin();

            for (Tile tile : farm.getTilesWithResources())
            {
               drawTileObject(tile);
            }

            renderer.getBatch().end();
        }
    }

    public void drawForagingTrees()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            Season season = App.getCurrentGame().getCurrentTime().getSeason();

            for (Tile tile : farm.getTilesWithForagingTrees())
            {
                ForagingTree tree = (ForagingTree) tile.getObject();
                TreeType type = tree.getTreeType().getTreeType();
                Texture texture = type.getSeasonTextures().get(season.toInteger());
                drawTileTreeTexture(tile, texture);
            }
        }
    }

    public void drawForagingItems()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            renderer.getBatch().begin();

            for (Tile tile : farm.getTilesWithForagingItems())
            {
                drawTileObject(tile);
            }

            renderer.getBatch().end();
        }
    }

    public void drawTilesWithCraftingItems()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            for (Tile tile : farm.getTilesWithCraftingItems())
            {
                drawTileCraftingItem(tile);
            }
        }

        else if (map instanceof GreenHouse)
        {
            GreenHouse greenHouse = (GreenHouse) map;

            for (Tile tile : greenHouse.getTilesWithCraftingItems())
            {
                drawTileCraftingItem(tile);
            }
        }
    }

    public void showAvailableTilesForArtisanEquipment(WorldScreen worldScreen)
    {
        if (map instanceof Farm || map instanceof GreenHouse)
        {
            Game game = App.getCurrentGame();
            Player player = game.getCurrentPlayer();

            if (player.getCurrentObject() != null && player.getCurrentObject() instanceof CraftingItem)
            {
                ArrayList<Point> availablePoint = map.getNeighbors(player.getLocation());

                for (Point p : availablePoint)
                {
                    Tile tile = map.getTile(p.getX(), p.getY());
                    if (tile.getObject() == null &&
                        (tile.getTexture() == TileTexture.LAND || tile.getTexture() == TileTexture.GRASS))
                    {
                        worldScreen.showSelectionOverTile(tile);
                    }
                }
            }
        }
    }

    public static void drawTileObject(Tile tile)
    {
        Vector2 location = map.tileToWorld(tile);
        if (tile.getObject() != null)
        {
            renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(), location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
        }
    }

    public static void drawTileCraftingItem(Tile tile)
    {
        Vector2 location = map.tileToWorld(tile);
        if (tile.getObject() != null && tile.getObject() instanceof CraftingItem)
        {
            CraftingItem craftingItem = (CraftingItem) tile.getObject();
            CraftingRecipeEnums type = craftingItem.getCraftingType();

            if (type.isTall())
            {
                renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(), location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (32 * MAP_SCALE));
            } else
            {
                renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(), location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
            }
        }
    }

    public static void drawTileEffect(Tile tile, EffectType effect)
    {
        Vector2 location = map.tileToWorld(tile);
        renderer.getBatch().draw(effect.getTexture(), location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
    }

    public static void drawTileTexture(Tile tile, Texture texture)
    {
        Vector2 location = map.tileToWorld(tile);
        renderer.getBatch().draw(texture, location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
    }

    public static void drawTileTreeTexture(Tile tile, Texture texture)
    {
        Vector2 location = map.tileToWorld(tile);
        int width = texture.getWidth();
        int height = texture.getHeight();
//        renderer.getBatch().draw(texture, location.x - (1f * (16 * MAP_SCALE)), location.y - (0.75f * (16 * MAP_SCALE)),
//            (0.5f * width), (0.5f * height));
        renderer.getBatch().draw(texture, location.x - (1f * (16 * MAP_SCALE)), location.y - (1f * (16 * MAP_SCALE)),
            (0.5f * width), (0.5f * height));
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
