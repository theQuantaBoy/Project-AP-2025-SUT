package ap.project.visual;

import ap.project.model.App.App;
import ap.project.model.animal.AnimalBuilding;
import ap.project.model.building.CraftingItem;
import ap.project.model.enums.*;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.TreeType;
import ap.project.model.game.*;
import ap.project.model.resources.*;
import ap.project.screen.WorldScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ap.project.model.game.Map.TILE_SIZE;
import static ap.project.screen.WorldScreen.MAP_SCALE;


public class MapVisual
{
    private static MapVisual currentInstance;

    private static Map map;
    private TiledMap tiledMap;
    private static OrthogonalTiledMapRenderer renderer;

    private ArrayList<GameAnimation> rainAnimations = new ArrayList<>();
    private final int RAIN_AMOUNT = 400;
    private double rainSpawnTimer = 0f;

    private ArrayList<GameAnimation> snowAnimations = new ArrayList<>();
    private final int SNOW_AMOUNT = 400;
    private double snowSpawnTimer = 0f;

    private ArrayList<GameAnimation> lightningAnimations = new ArrayList<>();
    private static final int MAX_LIGHTNING = 4;
    private static final float LIGHTNING_CHANCE = 0.01f;
    private static final float MIN_LIGHTNING_INTERVAL = 3.0f;
    private float lightningTimer = MIN_LIGHTNING_INTERVAL;

    private final Pool<GameAnimation> weatherPool = Pools.get(GameAnimation.class);

    private final ConcurrentLinkedQueue<GameAnimation> generalAnimations = new ConcurrentLinkedQueue<>();
    private final Pool<GameAnimation> animationPool = Pools.get(GameAnimation.class);

    private long lightningStartTime = 0;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ArrayList<Long> lightningFlashTimings = new ArrayList<>();
    private int currentFlashIndex = 0;

    public MapVisual(Map map, TiledMap tiledMap)
    {
        this.map = map;
        this.tiledMap = tiledMap;
        this.renderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f);
        currentInstance = this;
        this.shapeRenderer.setAutoShapeType(true);
    }

    public void render(OrthographicCamera cam)
    {
        renderer.setView(cam);
        renderer.render();
        drawHazardTiles();
        drawPlantingTiles();
        drawLightningTiles();
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
        drawTilesWithCraftingItems(worldScreen.getCamera());
        drawTilesWithAnimalBuildings(worldScreen.getCamera());

        batch.end();

        // Weather effects (draw last)
        drawRainAnimations();
        drawSnowAnimations();
        drawLightningAnimations();
        drawGeneralAnimations();
    }

    public void dispose()
    {
        tiledMap.dispose();
        renderer.dispose();
        currentInstance = null;
    }

    public void update(float delta)
    {
        rain(delta);
        snow(delta);
        lightning(delta);
        updateGeneralAnimations(delta);
        updateLightningEffect();
    }

    public void drawHazardTiles()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;
            ArrayList<Tile> hazardTiles = farm.getHazardTiles();

            renderer.getBatch().begin();
            for (Tile tile : hazardTiles)
            {
                drawTileEffect(tile, EffectType.PLAIN_TILE);
            }
            renderer.getBatch().end();
        }
    }

    public void drawPlantingTiles()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm)map;
            ArrayList<Tile> plantingTiles = farm.getPlantingTiles();

            renderer.getBatch().begin();
            ArrayList<Tile> removed = new ArrayList<>();

            for (Tile tile : plantingTiles)
            {
                if (!tile.isPloughed()) {
                    drawTileEffect(tile, EffectType.PLAIN_TILE);
                    tile.unPlant();
                    removed.add(tile);
                }

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
            farm.getPlantingTiles().removeAll(removed);

            renderer.getBatch().end();
        }

        if (map instanceof GreenHouse)
        {
            GreenHouse greenHouse = (GreenHouse)map;
            ArrayList<Tile> plantingTiles = greenHouse.getPlantingTiles();

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

    public void drawLightningTiles()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm)map;
            ArrayList<Tile> lightningTiles = farm.getLightningTiles();

            renderer.getBatch().begin();

            for (Tile tile : lightningTiles)
            {
                if (tile.isHitByThunder())
                {
                    drawTileEffect(tile, EffectType.LIGHTNING_STROKE_TILE);
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

                        if (plant instanceof GiantCrop)
                        {
                            drawGiantCrop(tile);
                        } else
                        {
                            drawTileTexture(tile, currentStage);
                        }
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

        if (map instanceof GreenHouse)
        {
            GreenHouse greenHouse = (GreenHouse)map;
            ArrayList<Tile> plantingTiles = greenHouse.getPlantingTiles();

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

                        if (plant instanceof GiantCrop)
                        {
                            drawGiantCrop(tile);
                        } else
                        {
                            drawTileTexture(tile, currentStage);
                        }
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
            if (rainSpawnTimer >= 0.15f && rainAnimations.size() < RAIN_AMOUNT)
            {
                int toAdd = Math.min(60, RAIN_AMOUNT - rainAnimations.size());

                for (int i = 0; i < toAdd; i++)
                {
                    GameAnimation anim = initWeatherParticle(
                        MathUtils.randomBoolean() ?
                            GameAnimationType.RAIN_TYPE_1 :
                            GameAnimationType.RAIN_TYPE_2
                    );
                    rainAnimations.add(anim);
                }

                rainSpawnTimer = 0f;
            }

            for (Iterator<GameAnimation> it = rainAnimations.iterator(); it.hasNext();)
            {
                GameAnimation a = it.next();
                a.update(delta);
                if (a.isFinished())
                {
                    weatherPool.free(a);
                    it.remove();
                }
            }
        }

        else
        {
            for (GameAnimation anim : rainAnimations)
            {
                weatherPool.free(anim);
            }
            rainAnimations.clear();
            rainSpawnTimer = 0f;
        }
    }

    public void snow(float delta)
    {
        snowSpawnTimer += delta;

        if (shouldSnow())
        {
            if (snowSpawnTimer >= 0.15f && snowAnimations.size() < SNOW_AMOUNT)
            {
                int toAdd = Math.min(60, SNOW_AMOUNT - snowAnimations.size());

                for (int i = 0; i < toAdd; i++)
                {
                    GameAnimation anim = initWeatherParticle(GameAnimationType.SNOW);
                    snowAnimations.add(anim);
                }

                snowSpawnTimer = 0f;
            }
        }

        for (Iterator<GameAnimation> it = snowAnimations.iterator(); it.hasNext();)
        {
            GameAnimation a = it.next();
            a.update(delta);
            if (a.isFinished())
            {
                weatherPool.free(a);
                it.remove();
            }
        }

        if (!shouldSnow())
        {
            for (GameAnimation anim : snowAnimations)
            {
                weatherPool.free(anim);
            }
            snowAnimations.clear();
            snowSpawnTimer = 0f;
        }
    }

    public void lightning(float delta)
    {
        lightningTimer += delta;

        if (shouldDoLightning() &&
            lightningTimer >= MIN_LIGHTNING_INTERVAL &&
            lightningAnimations.size() < MAX_LIGHTNING)
        {
            if (MathUtils.random() < LIGHTNING_CHANCE)
            {
                int strikes = MathUtils.random(1, 3);

                for (int i = 0; i < strikes; i++)
                {
                    Vector2 position = new Vector2(
                        MathUtils.random(map.getWidth() * TILE_SIZE),
                        MathUtils.random(map.getHeight() * TILE_SIZE)
                    );

                    GameAnimation anim = initWeatherParticle(GameAnimationType.LIGHTNING);

                    Player player =  App.getCurrentGame().getCurrentPlayer();
                    if (player.isInFarm())
                    {
                        Farm farm = player.getFarm();
                        Point location = farm.worldToTile(position.x, position.y);
                        Tile tile = farm.getTile(location.getX(), location.getY());

                        if (tile != null && (tile.getTexture() == TileTexture.LAND || tile.getTexture() == TileTexture.GRASS))
                        {
                            tile.hitByThunder();
                            farm.getLightningTiles().add(tile);
                        }
                    }

                    lightningAnimations.add(anim);
                }

                lightningTimer = 0f;
            }
        }

        Iterator<GameAnimation> it = lightningAnimations.iterator();
        while (it.hasNext())
        {
            GameAnimation anim = it.next();
            anim.update(delta);
            if (anim.isFinished())
            {
                weatherPool.free(anim);
                it.remove();
            }
        }
    }

    public boolean shouldDoLightning()
    {
        Game game = App.getCurrentGame();
        Time time = game.getCurrentTime();
        Player player = game.getCurrentPlayer();

        return (time.getCurrentWeather() == Weather.Storm) && (player.isInCity() || player.isInFarm());
    }

    public void drawLightningAnimations()
    {
        if (lightningAnimations.isEmpty()) return;

        renderer.getBatch().begin();
        for (GameAnimation anim : lightningAnimations)
        {
            anim.render(renderer.getBatch());
        }
        renderer.getBatch().end();
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
                if (tile.getObject() instanceof ForagingTree)
                {
                    ForagingTree tree = (ForagingTree) tile.getObject();
                    TreeType type = tree.getTreeType().getTreeType();
                    Texture texture = type.getSeasonTextures().get(season.toInteger());
                    drawTileTreeTexture(tile, texture);
                }
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

    public void drawTilesWithCraftingItems(OrthographicCamera cam)
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            for (Tile tile : farm.getTilesWithCraftingItems())
            {
                drawTileCraftingItem(tile, cam);
            }
        }

        else if (map instanceof GreenHouse)
        {
            GreenHouse greenHouse = (GreenHouse) map;

            for (Tile tile : greenHouse.getTilesWithCraftingItems())
            {
                drawTileCraftingItem(tile, cam);
            }
        }
    }

    public void drawTilesWithAnimalBuildings(OrthographicCamera cam)
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            for (Tile tile : farm.getAnimalBuildingTiles())
            {
                drawTileAnimalBuilding(tile, cam);
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

    public void drawTileCraftingItem(Tile tile, OrthographicCamera cam)
    {
        Vector2 location = map.tileToWorld(tile);
        if (tile.getObject() != null && tile.getObject() instanceof CraftingItem)
        {
            CraftingItem craftingItem = (CraftingItem) tile.getObject();
            CraftingRecipeEnums type = craftingItem.getCraftingType();
            boolean isTall = type.isTall();

            if (type.isTall())
            {
                renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(), location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (32 * MAP_SCALE));
            } else
            {
                renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(), location.x, location.y - (16 * MAP_SCALE), (16 * MAP_SCALE), (16 * MAP_SCALE));
            }
        }
    }

    public void drawTileAnimalBuilding(Tile tile, OrthographicCamera cam)
    {
        Vector2 location = map.tileToWorld(tile);
        if (tile.getObject() != null && tile.getObject() instanceof AnimalBuilding)
        {
            AnimalBuilding animalBuilding = (AnimalBuilding) tile.getObject();
            switch (animalBuilding.getFarmBuildingType())
            {
                case BARN:
                case BIG_BARN:
                case DELUXE_BARN:
                    renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(),
                        location.x, location.y - (32 * MAP_SCALE), (7 * 16 * MAP_SCALE), (8 * 16 * MAP_SCALE));
                    break;
                case COOP:
                case BIG_COOP:
                case DELUXE_COOP:
                    renderer.getBatch().draw(tile.getObject().getObjectType().getTexture(),
                        location.x, location.y - (32 * MAP_SCALE), (6 * 16 * MAP_SCALE), (8 * 16 * MAP_SCALE));
                    break;
            }
        }
    }

    public void drawCraftingProgressBars()
    {
        if (map instanceof Farm)
        {
            Farm farm = (Farm) map;

            for (Tile tile : farm.getTilesWithCraftingItems())
            {
                if (tile.getObject() != null && tile.getObject() instanceof CraftingItem)
                {
                    Vector2 location = map.tileToWorld(tile);
                    CraftingItem craftingItem = (CraftingItem) tile.getObject();
                    CraftingRecipeEnums type = craftingItem.getCraftingType();
                    boolean isTall = type.isTall();

                    if (craftingItem.getItemType() == CraftingItem.ItemType.PERIODIC && craftingItem.isWorking())
                    {
                        float progress = craftingItem.getHowMuchDone();
                        WorldScreen.getInstance().drawProgressBar(location, progress, isTall);
                    }
                }
            }
        }

        else if (map instanceof GreenHouse)
        {
            GreenHouse greenHouse = (GreenHouse) map;

            for (Tile tile : greenHouse.getTilesWithCraftingItems())
            {
                if (tile.getObject() != null && tile.getObject() instanceof CraftingItem)
                {
                    Vector2 location = map.tileToWorld(tile);
                    CraftingItem craftingItem = (CraftingItem) tile.getObject();
                    CraftingRecipeEnums type = craftingItem.getCraftingType();
                    boolean isTall = type.isTall();

                    if (craftingItem.getItemType() == CraftingItem.ItemType.PERIODIC && craftingItem.isWorking())
                    {
                        float progress = craftingItem.getHowMuchDone();
                        WorldScreen.getInstance().drawProgressBar(location, progress, isTall);
                    }
                }
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

    public static void drawGiantCrop(Tile tile)
    {
        if (tile.getObject() != null && tile.getObject() instanceof GiantCrop)
        {
            GiantCrop giantCrop = (GiantCrop) tile.getObject();
            if (tile.equals(giantCrop.getRootTile()))
            {
                Vector2 location = map.tileToWorld(tile);
                Texture texture = giantCrop.getCropType().getGiantModeTexture();
                renderer.getBatch().draw(texture, location.x, location.y - (2 * 16 * MAP_SCALE), (32 * MAP_SCALE), (38 * MAP_SCALE));
            }
        }
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

    private GameAnimation initWeatherParticle(GameAnimationType type)
    {
        int width = (int) (map.getWidth() * TILE_SIZE);
        int height = (int) (map.getHeight() * TILE_SIZE);
        Vector2 position = new Vector2(
            MathUtils.random(0, width),
            MathUtils.random(0, height)
        );

        GameAnimation anim = weatherPool.obtain();
        if (anim != null)
        {
            anim.init(type, position);
        }
        return anim;
    }

    private void updateGeneralAnimations(float delta)
    {
        Iterator<GameAnimation> it = generalAnimations.iterator();
        while (it.hasNext())
        {
            GameAnimation anim = it.next();
            anim.update(delta);
            if (anim.isFinished())
            {
                animationPool.free(anim);
                if (anim.getType() == GameAnimationType.NO_CLOUD_LIGHTNING_CHEAT)
                {
                    triggerLightningEffect();
                }
                it.remove();
            }
        }
    }

    public void drawGeneralAnimations()
    {
        if (generalAnimations.isEmpty()) return;

        renderer.getBatch().begin();
        for (GameAnimation anim : generalAnimations)
        {
            anim.render(renderer.getBatch());
        }
        renderer.getBatch().end();
    }

    public static void playAnimationAt(GameAnimationType type, Tile tile)
    {
        Vector2 position = map.tileToWorld(tile);

        if (currentInstance != null)
        {
            currentInstance.addAnimation(type, position);
        }
    }

    public static void playAnimationAt(GameAnimationType type, Vector2 position)
    {
        if (currentInstance != null)
        {
            currentInstance.addAnimation(type, position);
        }
    }

    private void addAnimation(GameAnimationType type, Vector2 position)
    {
        GameAnimation anim = animationPool.obtain();
        anim.setType(type);
        if (anim != null)
        {
            anim.init(type, position);
            generalAnimations.add(anim);
        }
    }

    public void triggerLightningEffect()
    {
        lightningFlashTimings.clear();
        long now = TimeUtils.millis();

        int flickers = MathUtils.random(1, 3); // 1 to 3 flickers
        for (int i = 0; i < flickers; i++)
        {
            lightningFlashTimings.add(now + MathUtils.random(0, 300)); // random delay between flickers
        }

        lightningStartTime = lightningFlashTimings.get(0);
        currentFlashIndex = 0;
    }

    public void updateLightningEffect()
    {
        if (lightningFlashTimings.isEmpty()) return;

        long now = TimeUtils.millis();
        long currentStart = lightningFlashTimings.get(currentFlashIndex);
        long elapsed = now - currentStart;

        if (elapsed >= 200)
        {
            currentFlashIndex++;
            if (currentFlashIndex >= lightningFlashTimings.size())
            {
                lightningFlashTimings.clear();
                lightningStartTime = 0;
            }
            else
            {
                lightningStartTime = lightningFlashTimings.get(currentFlashIndex);
            }
        }
    }

    public void renderLightningEffect()
    {
        if (lightningStartTime == 0) return;

        long elapsed = TimeUtils.timeSinceMillis(lightningStartTime);

        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float alpha;

        if (elapsed < 100) {
            // Phase 1: ramp up quickly
            alpha = (elapsed / 100f) * 0.8f;
        } else if (elapsed < 150) {
            // Phase 2: hold full flash
            alpha = 0.8f;
        } else if (elapsed < 400) {
            // Phase 3: fade out
            float t = (elapsed - 150f) / 250f;
            alpha = (1f - t) * 0.8f;
        } else {
            lightningStartTime = 0;
            shapeRenderer.end();
            return;
        }

        shapeRenderer.setColor(1, 1, 1, alpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
    }

    public boolean isExhaustionAnimationPlaying()
    {
        for (GameAnimation anim : generalAnimations)
        {
            if (anim.getType() == GameAnimationType.EXHAUSTION)
            {
                return true;
            }
        }

        return false;
    }
}
