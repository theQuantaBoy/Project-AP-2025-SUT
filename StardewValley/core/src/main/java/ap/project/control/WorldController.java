package ap.project.control;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.animal.Animal;
import ap.project.model.animal.AnimalBuilding;
import ap.project.model.building.CraftingItem;
import ap.project.model.enums.*;
import ap.project.model.enums.animal_enums.FarmAnimalsType;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.enums.building_enums.KitchenRecipe;
import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.ResourceItem;
import ap.project.model.enums.resources_enums.TreeType;
import ap.project.model.game.*;
import ap.project.model.player_data.FriendshipWithNpcData;
import ap.project.model.resources.*;
import ap.project.model.resources.Tree;
import ap.project.model.shops.Shop;
import ap.project.model.tools.*;
import ap.project.network.shared.npcDialogLLM;
import ap.project.screen.CommunicationWindow;
import ap.project.screen.ShopWindow;
import ap.project.screen.WorldScreen;
import ap.project.visual.MapVisual;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.sun.source.doctree.UnknownInlineTagTree;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ap.project.model.game.Map.TILE_SIZE;
import static ap.project.screen.ReactionWindow.FONT_SCALE;
import static ap.project.screen.WorldScreen.PLAYER_SPEED;

public class WorldController
{
    private static CommunicationWindow communicationWindow;

    public void setCommunicationWindow(CommunicationWindow communicationWindow) {
        WorldController.communicationWindow = communicationWindow;
    }

    public CommunicationWindow getCommunicationWindow() {
        return communicationWindow;
    }

    public static void processClickLeft(WorldScreen worldScreen, Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        if (tile == null)
        {
            return;
        }

        Point clicked = tile.getPoint();
        Point location = player.getLocation();

        doLightning(tile);
        crowAttack(tile);

        if (processShopWindow(tile))
        {
            return;
        }

        if (!Map.isNearOrOn(location, clicked))
        {
            return;
        }

        if (processBuildings(worldScreen, tile, clicked))
        {
            return;
        }

        if (processToolUse(tile))
        {
            return;
        }

        if (processObjectUse(tile, location,  clicked))
        {
            return;
        }

        if (processMapNavigation(tile))
        {
            return;
        }

        if (processNPCCommunication(tile))
        {
            return;
        }

        if (processAnimalInteraction(tile))
        {
            return;
        }
    }

    public static void processClickRight(WorldScreen worldScreen, Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        if (tile == null)
        {
            return;
        }

        Point clicked = tile.getPoint();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
        {
            if (clicked != null)
            {
                Map map = player.getCurrentMap();

                Vector2 playerPos = player.getPosition();
                Point playerTile = map.worldToTile(playerPos.x, playerPos.y);

                ArrayList<Point> path = map.findShortestPath(playerTile, clicked);

                if (path != null)
                {
                    WorldScreen.getInstance().getCharacterController().moveToPath(path);
                }
            }
        }

        Player nearbyPlayer = findNearbyPlayer(clicked, 1); // radius 1 (8-neighbors)

        if (nearbyPlayer != null) {
            communicationWindow.show(nearbyPlayer);
        } else {
            Point location = player.getLocation();

            if (!Map.isNearOrOn(location, clicked))
            {
                return;
            }

            if (processCraftingStation(worldScreen, tile))
            {
                return;
            }

            if (processNpcWindow(tile))
            {
                return;
            }

            if (processAnimalWindow(tile))
            {
                return;
            }
        }
    }

    public static void doLightning(Tile tile)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (Gdx.input.isKeyPressed(Input.Keys.L) && (player.isInFarm() || player.isInCity()))
        {
            if (player.isInFarm())
            {
                tile.hitByThunder();
                Farm farm = player.getFarm();
                farm.getLightningTiles().add(tile);
            }
            MapVisual.playAnimationAt(GameAnimationType.NO_CLOUD_LIGHTNING_CHEAT, tile);
        }
    }

    private static boolean processFishing(Tile tile)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();



        return false;
    }

    private static void crowAttack(Tile tile)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (Gdx.input.isKeyPressed(Input.Keys.C) && player.isInFarm())
        {
            Farm farm = player.getFarm();

            if (tile.hasPlants())
            {
                Plant plant = (Plant) tile.getObject();
                if (!tile.isImmuneFromCrows())
                {
                    plant.getAttacked();
                }
            }

            MapVisual.playAnimationAt(GameAnimationType.CROW_ATTACK, farm.getTile(tile.getX(), tile.getY() - 4));
        }
    }

    private static boolean processMapNavigation(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        Point clicked = tile.getPoint();
        Point location = player.getLocation();

        if (player.isInHome())
        {
            Point door = player.getCabin().getStartingPoint();

            if (Map.isNearOrOn(door, clicked))
            {
                player.goToFarm();
                return true;
            }
        }

        if (player.isInGreenHouse())
        {
            Point door = player.getGreenHouse().getStartingPoint();

            if (Map.isNearOrOn(door, clicked))
            {
                player.goToFarm();
                return true;
            }
        }

        if (player.isInFarm())
        {
            Farm farm = player.getFarm();
            Point homeDoor = farm.getHomePoint();
            Point greenhouseDoor = farm.getGreenhousePoint();
            Point exitPoint = farm.getExitPoint();

            if (Map.isNearOrOn(homeDoor, clicked))
            {
                player.goHome();
                return true;
            }

            if (Map.isNearOrOn(greenhouseDoor, clicked))
            {
                GreenHouse greenhouse = player.getGreenHouse();

                if (!greenhouse.isBuilt())
                {
                    WorldScreen.getInstance().toggleGreenHouseBuildWindow();
                } else
                {
                    player.goToGreenHouse();
                }

                return true;
            }

            if (Map.isNearOrOn(exitPoint, clicked))
            {
                player.goToCity();
                return true;
            }
        }

        if (player.isInCity())
        {
            City city = App.getCurrentGame().getCity();
            Point startingPoint = city.getStartingPoint();

            for (java.util.Map.Entry<Point, Shop> entry : city.getShopDoors().entrySet())
            {
                Point door = entry.getKey();
                Shop shop = entry.getValue();

                if (Map.isNearOrOn(door, clicked))
                {
                    if (shop.isOpen(App.getCurrentGame().getCurrentTime()))
                    {
                        player.goToShop(shop);
                    } else
                    {
                        UIRenderer.showTextBox("Work Hours: " + shop.getStartWork() + " to " + shop.getEndWork());
                    }
                    return true;
                }
            }

            if (Map.isNearOrOn(startingPoint, clicked))
            {
                player.goToFarm();
                return true;
            }
        }

        if (player.isInShop())
        {
            City city = App.getCurrentGame().getCity();
            Shop shop = (Shop) player.getCurrentMap();
            Point startingPoint = shop.getStartingPoint();
            Point exteriorDoor = city.getExteriorDoor(shop.getType());

            if (Map.isNearOrOn(startingPoint, clicked))
            {
                player.goToCity(exteriorDoor);
            }
        }

        return false;
    }

    private static boolean processToolUse(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        Map map = player.getCurrentMap();

        Point clicked = tile.getPoint();
        Point location = player.getLocation();

        Tool tool = player.getCurrentTool();

        if (tool == null)
        {
            return false;
        }

        Double weatherModifier = 1.0;
        Weather weather = App.getCurrentGame().getCurrentTime().getCurrentWeather();
        if (weather.equals(Weather.Rain) || weather.equals(Weather.Storm))
        {
            weatherModifier = 1.5;
        } else if (weather.equals(Weather.Snow))
        {
            weatherModifier = 2.0;
        }

        if (tool instanceof Hoe)
        {
            if (!tile.getTexture().equals(TileTexture.LAND) && !tile.getTexture().equals(TileTexture.GRASS))
            {
                player.increaseEnergy((int) (weatherModifier * -((Hoe) tool).getLevel().getFailedEnergyUsage()));
                UIRenderer.showTextBox("you can't use hoe on this tile");
                return true;
            }

            if (tile.getObject() == null && !tile.isPloughed())
            {
                if (player.getEnergy() <= (int) (weatherModifier * ((Hoe) tool).getLevel().getFailedEnergyUsage()))
                {
                    UIRenderer.showTextBox("you don't have enough energy");
                    return true;
                }

                tile.plough();
                MapVisual.playAnimationAt(GameAnimationType.PLOUGH, tile);
                player.increaseEnergy((int) (weatherModifier * -((Hoe) tool).getLevel().getBaseEnergyUsage()));
                UIRenderer.showTextBox("hoe used");
                return true;
            }

            return false;
        }

        if (tool instanceof WateringCan)
        {
            if (player.getEnergy() <= (int) (weatherModifier * ((WateringCan) tool).getLevel().getBaseEnergyUsage()))
            {
                UIRenderer.showTextBox("you don't have enough energy");
                return true;
            }

            player.increaseEnergy((int) (weatherModifier * -((WateringCan) tool).getLevel().getBaseEnergyUsage()));

            if (tile.getTexture().equals(TileTexture.LAND) || tile.getTexture().equals(TileTexture.GRASS))
            {
                if (tile.hasPlants())
                {
                    Plant plant = (Plant) tile.getObject();

                    if (plant.hasBeenWateredToday())
                    {
                        UIRenderer.showTextBox("You have already watered this plant today.");
                        return true;
                    }

                    if (((WateringCan) tool).getCurrentVolume() == 0)
                    {
                        UIRenderer.showTextBox("You should refill your watering can.");
                        return true;
                    }

                    ((WateringCan) tool).decreaseVolume(1);
                    plant.water();
                    MapVisual.playAnimationAt(GameAnimationType.WATER, tile);

                    UIRenderer.showTextBox("Plant has been watered.");
                    return true;
                } else if (!tile.hasPlants())
                {
                    UIRenderer.showTextBox("There are no plants in this tile.");
                    return true;
                }
            } else if (tile.getTexture().equals(TileTexture.LAKE))
            {
                WateringCan can = (WateringCan) tool;
                if (can.getCurrentVolume() == can.getLevel().getCapacity())
                {
                    UIRenderer.showTextBox("Watering can is already full.");
                    return true;
                }
                can.addVolume(5);
                UIRenderer.showTextBox("added water into watering can.");
                return true;
            } else
            {
                UIRenderer.showTextBox("you can't use watering can on this type of tile.");
                return true;
            }

            return false;
        }

        if (tool instanceof Seythe)
        {
            if (player.getEnergy() <= (int)(weatherModifier * ((Seythe) tool).getEnergyUsage())){
                UIRenderer.showTextBox("you don't have enough energy");
                return true;
            }

            player.increaseEnergy((int)(weatherModifier * -((Seythe) tool).getEnergyUsage()));

            if (tile.getTexture().equals(TileTexture.GRASS) &&
                tile.getObject() == null) {
                tile.setTexture(TileTexture.LAND);

                if (player.isInFarm())
                {
                    player.getFarm().getHazardTiles().add(tile);
                }

                UIRenderer.showTextBox("alaf is harzed successfully");
                return true;
            }

            if (!tile.hasPlants())
            {
                UIRenderer.showTextBox("There are no plants in this tile :(");
                return true;
            }

            Plant plant = (Plant) tile.getObject();
            if (!plant.canHarvest())
            {
                UIRenderer.showTextBox("You can't harvest this plant yet :(");
                return true;
            }

            if (plant instanceof Tree)
            {
                Tree tree = (Tree) plant;
                if (tree.getTreeType().equals(TreeType.MAPLE_TREE) ||
                    tree.getTreeType().equals(TreeType.MYSTIC_TREE)) {

                    UIRenderer.showTextBox("you should use axe for this tree");
                    return true;
                }

                GameObject fruit = new GameObject(tree.getFruit().getType(), 1);
                player.addToInventory(fruit);
                tree.harvest();
                player.getFarmingSkill().changeUnit(5);
                MapVisual.playAnimationAt(GameAnimationType.HARVEST, tile);

                UIRenderer.showTextBox("you harvested one " + fruit.getObjectType().toString() + ".");
                return true;
            } else if (plant instanceof Crop)
            {
                boolean giant = false;

                Crop crop = (Crop) plant;

                if (plant instanceof GiantCrop)
                {
                    giant = true;
                    GameObject cropResult = new GameObject(crop.getCropType().getType(), 10);
                    player.addToInventory(cropResult);
                } else
                {
                    GameObject cropResult = new GameObject(crop.getCropType().getType(), 1);
                    player.addToInventory(cropResult);
                }

                player.getFarmingSkill().changeUnit(5);
                if (crop.harvest())
                {
                    tile.unPlant();
                }

                MapVisual.playAnimationAt(GameAnimationType.HARVEST, tile);

                UIRenderer.showTextBox("you harvested " + (giant ? "ten " : "one ") + crop.getObjectType().toString() + ".");
                return true;
            }

            return false;
        }

        if (tool instanceof Pickaxe)
        {
            if (tile.getObject() != null && tile.getObject() instanceof ForagingMineral)
            {
                if (player.getEnergy() <= (int)(weatherModifier * ((Pickaxe) tool).getLevel().getBaseEnergyUsage()))
                {
                    UIRenderer.showTextBox("you don't have enough energy");
                    return true;
                }

                player.addToInventory(tile.getObject());
                player.increaseEnergy(-((Pickaxe) tool).getLevel().getBaseEnergyUsage());
                player.getForagingSkill().changeUnit(5);
                tile.setObject(null);

                UIRenderer.showTextBox("pickaxe used on mineral");
                return true;
            }

            else if (tile.getTexture().equals(TileTexture.QUARRY) && tile.getObject() == null)
            {
                if (player.getEnergy() <= (int)(weatherModifier * ((Pickaxe) tool).getLevel().getBaseEnergyUsage()))
                {
                    UIRenderer.showTextBox("you don't have enough energy");
                    return true;
                }

                if (player.getMiningSkill().getLevel() > 1)
                {
                    player.addToInventory(ResourceItem.STONE.getType(), 5);
                } else
                {
                    player.addToInventory(ResourceItem.STONE.getType(), 3);
                }

                player.increaseEnergy((int)(weatherModifier * -((Pickaxe) tool).getLevel().getBaseEnergyUsage()));
                player.getMiningSkill().changeUnit(10);

                UIRenderer.showTextBox("pickaxe used on query");
                return true;
            }

            else if (tile.getTexture().equals(TileTexture.LAND))
            {
                if (player.getEnergy() <= (int)(weatherModifier * ((Pickaxe) tool).getLevel().getBaseEnergyUsage()))
                {
                    UIRenderer.showTextBox("you don't have enough energy");
                    return true;
                }

                if (tile.isPloughed())
                {
                    if (tile.hasPlants())
                    {
                        tile.unPlant();
                    }

                    tile.ploghInverse();
                    player.increaseEnergy((int)(weatherModifier * -((Pickaxe) tool).getLevel().getBaseEnergyUsage()));

                    UIRenderer.showTextBox("tile is not ploughed anymore");
                    return true;
                } else
                {
                    if (player.getEnergy() <= (int)(weatherModifier * ((Pickaxe) tool).getLevel().getFailedEnergyUsage()))
                    {
                        UIRenderer.showTextBox("you don't have enough energy");
                        return true;
                    }

                    player.increaseEnergy((int)(weatherModifier * -((Pickaxe) tool).getLevel().getFailedEnergyUsage()));

                    UIRenderer.showTextBox("tile is not ploughed");
                    return true;
                }
            }

            else if (tile.getObject() != null &&tile.getObject() instanceof ForagingSeed)
            {
                tile.setObject(null);
                player.increaseEnergy((int)(weatherModifier * -((Pickaxe) tool).getLevel().getBaseEnergyUsage()));

                UIRenderer.showTextBox("seed is removed");
                return true;
            }

            if (tile.getObject() != null && (tile.getObject() instanceof Tree || tile.getObject() instanceof ForagingTree))
            {
                return false;
            }

            else if (!tile.hasPlants() && tile.getObject() != null)
            {
                GameObject object = tile.getObject();
                if (!player.inventoryHasCapacity())
                {
                    UIRenderer.showTextBox("you don't have enough space in your inventory");
                    return true;
                }

                player.addToInventory(object);
                tile.setObject(null);

                if (tile.isHitByThunder())
                {
                    tile.unHitByThunder();
                    player.getFarm().getThunderedTiles().remove(tile);
                    return true; //??
                }

                UIRenderer.showTextBox(object.getObjectType().toString() + " added to your inventory");
                return true;
            }

            else
            {
                player.increaseEnergy((int)(weatherModifier * -((Pickaxe) tool).getLevel().getFailedEnergyUsage()));

                UIRenderer.showTextBox("you can't use pickaxe on this tile");
                return true;
            }
        }

        if (tool instanceof Axe)
        {
            if (tile.getObject() == null)
            {
                return false;
            }

            if (tile.getObject() instanceof Tree || tile.getObject() instanceof ForagingTree ||
                tile.getObject().getObjectType().equals(ResourceItem.WOOD.getType()))
            {
                if (player.getEnergy() <= (int)(weatherModifier * ((Axe) tool).getLevel().getBaseEnergyUsage()))
                {
                    UIRenderer.showTextBox("you don't have enough energy");
                    return true;
                }

                player.increaseEnergy((int)(weatherModifier * -((Axe) tool).getLevel().getBaseEnergyUsage()));
                player.addToInventory(ResourceItem.WOOD.getType(), 3);

                UIRenderer.showTextBox("3 units of wood added to inventory.");

                if (tile.getObject() instanceof Tree)
                {
                    if (((Tree) tile.getObject()).getTreeType().equals(TreeType.MAPLE_TREE))
                    {
                        Tree tree = (Tree) tile.getObject();
                        tree.harvest();
                        player.addToInventory(new GameObject(GameObjectType.MAPLE_SYRUP, 3));
                        UIRenderer.showTextBox("3 units of wood added to inventory.");
                    } else if (((Tree) tile.getObject()).getTreeType().equals(TreeType.MYSTIC_TREE))
                    {
                        Tree tree = (Tree) tile.getObject();
                        tree.harvest();
                        player.addToInventory(new GameObject(GameObjectType.MYSTIC_SYRUP, 3));
                    }
                }

                tile.setObject(null);

                UIRenderer.showTextBox("axe used successfully");
                return true;
            }

            return false;
        }

        if (tool instanceof MilkPail)
        {
            if (player.getEnergy() <= (int)(weatherModifier * ((MilkPail) tool).getEnergyUsage()))
            {
                UIRenderer.showTextBox("you don't have enough energy!");
                return true;
            }

            Animal animal = player.getTileAnimal(tile);
            if (animal != null)
            {
                if (animal.getAnimalType().equals(FarmAnimalsType.SHEEP) ||
                    animal.getAnimalType().equals(FarmAnimalsType.COW))
                {
                    player.addToInventory(GameObjectType.MILK, 1);
                    player.increaseEnergy((int)(weatherModifier * -((MilkPail) tool).getEnergyUsage()));

                    UIRenderer.showTextBox("milk added to your inventory!");
                    return true;
                } else if (animal.getAnimalType().equals(FarmAnimalsType.GOAT))
                {
                    player.addToInventory(GameObjectType.GOAT_MILK, 1);
                    player.increaseEnergy((int)(weatherModifier * -((MilkPail) tool).getEnergyUsage()));

                    UIRenderer.showTextBox("goat milk added to your inventory!");
                    return true;
                }

                UIRenderer.showTextBox("no Daam animal found in this barn");
                return true;
            }

            return false;
        }

        else if (tool instanceof Shear)
        {
            if (player.getEnergy() <= (int)(weatherModifier * ((Shear) tool).getEnergyUsage()))
            {
                UIRenderer.showTextBox("you don't have enough energy!");
                return true;
            }

            Animal animal = player.getTileAnimal(tile);
            if (animal != null)
            {
                if (animal.getAnimalType().equals(FarmAnimalsType.SHEEP))
                {
                    player.addToInventory(GameObjectType.WOOL, 3);
                    player.increaseEnergy((int)(weatherModifier * -((Shear) tool).getEnergyUsage()));

                    UIRenderer.showTextBox("3 wools added to your inventory!");
                    return true;
                }

                UIRenderer.showTextBox("no sheep found in this barn");
                return true;
            }

            return false;
        }

        else if (tool instanceof FishingPole)
        {
            if (player.isInFarm())
            {
                ArrayList<Point> neighbors = player.getFarm().getSquareNeighbors(tile.getPoint(), 2);
                for (Point p : neighbors)
                {
                    Tile t = player.getFarm().getTile(p.getX(), p.getY());
                    if (t != null && t.getTexture() == TileTexture.LAKE)
                    {
                        WorldScreen.getInstance().toggleFishMiniGame();
                        player.increaseEnergy((int)(weatherModifier * -((FishingPole) tool).getLevel().getBaseEnergyUsage()));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean processObjectUse(Tile tile, Point location, Point clicked)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        GameObject object = player.getCurrentObject();

        if (object == null)
        {
            return false;
        }

        if (processFertilizerUse(tile))
        {
            return true;
        }

        if (processPlantingUse(tile))
        {
            return true;
        }

        if (processAnimalBuildingPlacement(tile))
        {
            return true;
        }

        if (processCraftingPlacement(tile))
        {
            return true;
        }

        if (processAnimalPlacementTile(tile))
        {
            return true;
        }

        if (processFeedingAnimal(tile))
        {
            return true;
        }

        if (processEatingFood(tile, location, clicked))
        {
            return true;
        }

        return false;
    }

    private static boolean processFeedingAnimal(Tile tile)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (!player.isInFarm())
        {
            return false;
        }

        Farm farm = player.getFarm();
        Animal animal = player.getTileAnimal(tile);

        if (animal == null)
        {
            return false;
        }

        GameObject object = player.getCurrentObject();
        if (object == null)
        {
            return false;
        }

        if (object.getObjectType() == GameObjectType.HAY)
        {
            if (!animal.isFed())
            {
                UIRenderer.showTextBox("Your friendship level was increased 8 units!");
            }

            animal.feed();
            UIRenderer.showTextBox("You fed " + animal.getName() + "!");
            MapVisual.playAnimationAt(GameAnimationType.EATING, tile);
            player.removeAmountFromInventory(GameObjectType.HAY, 1);
            return true;
        }

        return false;
    }

    private static boolean processEatingFood(Tile tile, Point location, Point clicked)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        GameObject object = player.getCurrentObject();

        if (object == null)
        {
            return false;
        }

        GameObjectType type = object.getObjectType();
        if (!KitchenRecipe.isEdible(type))
        {
            UIRenderer.showTextBox("This item is not edible :(");
            return true;
        }

        KitchenRecipe food = KitchenRecipe.getKitchenRecipe(type.toString());
        if (food == null)
        {
            UIRenderer.showTextBox("This item is not edible :(");
            return true;
        } else
        {
            player.removeAmountFromInventory(type, 1);
            player.increaseEnergy(food.getEnergy());

            Vector2 loc = new Vector2(player.getPosition().x - 7, player.getPosition().y);
            MapVisual.playAnimationAt(GameAnimationType.EATING, loc);

            if (food.getBuffType() != null)
            {
                int startHour = game.getCurrentTime().getTotalHoursPassed();
                player.setBuff(new Buff(food.getBuffType(), startHour));
            }

            UIRenderer.showTextBox("Yum Yum, you just ate " + food.getType());
        }

        return false;
    }

    private static boolean processCraftingPlacement(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        Map map = player.getCurrentMap();

        if (!player.isInFarm() && !player.isInGreenHouse())
        {
            return false;
        }

        GameObject object = player.getCurrentObject();
        if (object == null)
        {
            return false;
        }

        if (!(object instanceof CraftingItem))
        {
            return false;
        }

        CraftingItem craftingItem = (CraftingItem) object;
        if (tile.getObject() != null)
        {
            UIRenderer.showTextBox("this tile is not empty");
            return false;
        }

        if (tile.getTexture() != TileTexture.LAND && tile.getTexture() != TileTexture.GRASS)
        {
            UIRenderer.showTextBox("you can't put a crafting item on this type of tile");
            return false;
        }

        CraftingItem newCraftedItem = new CraftingItem(craftingItem.getCraftingType(), tile.getPoint());
        tile.setObject(newCraftedItem);

        if (object.getNumber() == 1)
        {
            player.setCurrentObject(null);
        }
        player.removeAmountFromInventory(object.getObjectType(), 1);

        if (player.isInFarm())
        {
            Farm farm = (Farm) player.getFarm();
            farm.addToTilesWithCraftingItems(tile);
        } else if (player.isInGreenHouse())
        {
            GreenHouse greenHouse = (GreenHouse) player.getGreenHouse();
            greenHouse.addToTilesWithCraftingItems(tile);
        }

        if (newCraftedItem.getCraftingType().getItemType() == CraftingItem.ItemType.PERMANENT)
        {
            if (newCraftedItem.getCraftingType() == CraftingRecipeEnums.SPRINKLER_RECIPE)
            {
                ArrayList<Point> neighbors = map.getDirectNeighbors(tile.getPoint());
                for (Point neighbor : neighbors)
                {
                    Tile t = map.getTile(neighbor.getX(), neighbor.getY());
                    if (t != null) t.setShouldBeWateredAutomatically(true);
                }
            } else if (newCraftedItem.getCraftingType() == CraftingRecipeEnums.QUALITY_SPRINKLER_RECIPE)
            {
                ArrayList<Point> neighbors = map.getSquareNeighbors(tile.getPoint(), 1);
                for (Point neighbor : neighbors)
                {
                    Tile t = map.getTile(neighbor.getX(), neighbor.getY());
                    if (t != null) t.setShouldBeWateredAutomatically(true);
                }
            } else if (newCraftedItem.getCraftingType() == CraftingRecipeEnums.IRIDIUM_SPRINKLER_RECIPE)
            {
                ArrayList<Point> neighbors = map.getSquareNeighbors(tile.getPoint(), 2);
                for (Point neighbor : neighbors)
                {
                    Tile t = map.getTile(neighbor.getX(), neighbor.getY());
                    if (t != null) t.setShouldBeWateredAutomatically(true);
                }
            }

            else if (newCraftedItem.getCraftingType() == CraftingRecipeEnums.SCARECROW_RECIPE)
            {
                ArrayList<Point> neighbors = map.getCircularNeighbors(tile.getPoint(), 8);
                for (Point neighbor : neighbors)
                {
                    Tile t = map.getTile(neighbor.getX(), neighbor.getY());
                    if (t != null) t.makeImmuneFromCrows();
                }
            } else if (newCraftedItem.getCraftingType() == CraftingRecipeEnums.DELUXE_SCARECROW_RECIPE)
            {
                ArrayList<Point> neighbors = map.getCircularNeighbors(tile.getPoint(), 12);
                for (Point neighbor : neighbors)
                {
                    Tile t = map.getTile(neighbor.getX(), neighbor.getY());
                    if (t != null) t.makeImmuneFromCrows();
                }
            }
        }

        UIRenderer.showTextBox("successfully put " + object.getObjectType() + " on tile!");
        return true;
    }

    private static boolean processAnimalBuildingPlacement(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        Map map = player.getCurrentMap();

        if (!player.isInFarm())
        {
            return false;
        }

        GameObject object = player.getCurrentObject();
        if (object == null)
        {
            return false;
        }

        FarmBuildingType farmBuildingType = FarmBuildingType.isAnimalBuilding(object);
        if (farmBuildingType == null)
        {
            return false;
        }

        if (tile.getObject() != null)
        {
            UIRenderer.showTextBox("this tile is not empty");
            return false;
        }

        int width = farmBuildingType.getWidth();
        int height = farmBuildingType.getHeight();
        Point point = tile.getPoint();

        if (!map.getMapVisual().isGoodForAnimalBuilding(point, width, height))
        {
            UIRenderer.showTextBox("you can't build a building on this type of tile");
            return false;
        }

        AnimalBuilding animalBuilding = new AnimalBuilding(tile, farmBuildingType);
        tile.setObject(animalBuilding);

        if (object.getNumber() == 1)
        {
            player.setCurrentObject(null);
        }
        player.removeAmountFromInventory(object.getObjectType(), 1);

        Farm farm = player.getFarm();
        farm.addAnimalBuildingTile(tile);
        return true;
    }

    private static boolean processFertilizerUse(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        Map map = player.getCurrentMap();

        if (!(map instanceof Farm) && !(map instanceof GreenHouse))
        {
            return false;
        }

        GameObject fertilizer = player.getCurrentObject();

        if (!isFertilizer(fertilizer.getObjectType()))
        {
            return false;
        }

        if (!tile.isPloughed())
        {
            UIRenderer.showTextBox("You must first use the Hoe to plough this tile.");
            return true;
        }

        if (tile.isFertilized())
        {
            UIRenderer.showTextBox("This tile has already been fertilized.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.FERTILIZER)
        {
            tile.fertilize();
            MapVisual.playAnimationAt(GameAnimationType.FERTILIZE, tile);
            player.removeAmountFromInventory(fertilizer.getObjectType(), 1);
            tile.setWateringChance(0);

            UIRenderer.showTextBox("Tile fertilized with fertilizer.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.SPECIAL_FERTILIZER)
        {
            tile.fertilize();
            player.removeAmountFromInventory(fertilizer.getObjectType(), 1);
            tile.setWateringChance(0);
            tile.setGrowFaster();

            UIRenderer.showTextBox("Tile fertilized with special fertilizer.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.BASIC_RETAINING_SOIL)
        {
            tile.fertilize();
            player.removeAmountFromInventory(fertilizer.getObjectType(), 1);
            tile.setWateringChance(40);

            UIRenderer.showTextBox("Tile fertilized with basic retaining soil.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.QUALITY_RETAINING_SOIL)
        {
            tile.fertilize();
            player.removeAmountFromInventory(fertilizer.getObjectType(), 1);
            tile.setWateringChance(70);

            UIRenderer.showTextBox("Tile fertilized with quality retaining soil.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.DELUXE_RETAINING_SOIL)
        {
            tile.fertilize();
            player.removeAmountFromInventory(fertilizer.getObjectType(), 1);
            tile.setWateringChance(100);

            UIRenderer.showTextBox("Tile fertilized with deluxe retaining soil.");
            return true;
        }

        return false;
    }

    private static boolean processPickingUpForagingItem(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        if (tile.getObject() == null)
        {
            return false;
        }

        if (tile.isRandomForaging())
        {
            GameObject object = tile.getObject();
            player.addToInventory(object.getObjectType(), 1);
            tile.setObject(null);
            tile.setRandomForaging(false);
            return true;
        }

        return false;
    }

    private static boolean processPlantingUse(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        Map map = player.getCurrentMap();

        if (!(map instanceof Farm) && !(map instanceof GreenHouse))
        {
            return false;
        }

        GameObject object = player.getCurrentObject();
        GameObjectType objectType = object.getObjectType();

        CropType cropType = CropType.getCropBySeed(objectType);
        TreeType treeType = TreeType.getTreeBySeed(objectType);

        if (cropType == null &&  treeType == null)
        {
            return false;
        }

        if (!tile.isPloughed())
        {
            UIRenderer.showTextBox("This tile is not ploughed.");
            return true;
        }

        if (!tile.isFertilized())
        {
            UIRenderer.showTextBox("This tile is not fertilized.");
            return true;
        }

        if (!tile.getTexture().equals(TileTexture.LAND) && !tile.getTexture().equals(TileTexture.GRASS))
        {
            UIRenderer.showTextBox("You can't plant in this type of tile.");
            return true;
        }

        if (tile.getObject() != null)
        {
            UIRenderer.showTextBox("This tile is not empty.");
            return true;
        }


        if (cropType != null)
        {
            int index =  App.getCurrentGame().getPlayerIndex();
            Crop crop = new Crop(cropType, tile.getPoint(), index, tile.isGrowFaster());
            tile.setObject(crop);

            Tile rootTile;
            boolean giant = false;

            if ((rootTile = crop.canBecomeGiant(0)) != null)
            {
                giant = crop.becomeGiant(rootTile);
            } else if ((rootTile = crop.canBecomeGiant(1)) != null)
            {
                giant = crop.becomeGiant(rootTile);
            } else if ((rootTile = crop.canBecomeGiant(2)) != null)
            {
                giant = crop.becomeGiant(rootTile);
            } else if ((rootTile = crop.canBecomeGiant(3)) != null)
            {
                giant = crop.becomeGiant(rootTile);
            }

            if (!giant)
            {
                if (player.isInGreenHouse())
                {
                    crop.putInGreenhouse();
                }
            }

            player.removeAmountFromInventory(objectType, 1);

            if (giant)
            {
                UIRenderer.showTextBox("Successfully planted giant " + cropType.getName() + ".");
                return true;
            } else
            {
                UIRenderer.showTextBox("Successfully planted " + cropType.getName() + ".");
                return true;
            }
        }

        if (treeType != null)
        {
            int index = App.getCurrentGame().getPlayerIndex();
            Tree tree = new Tree(treeType, tile.getPoint(), index, tile.isGrowFaster());
            tile.setObject(tree);
            if (player.isInGreenHouse())
            {
                tree.putInGreenhouse();
            }
            player.removeAmountFromInventory(objectType, 1);

            UIRenderer.showTextBox("Successfully planted " + treeType.getName() + ".");
            return true;
        }

        return false;
    }

    private static boolean isFertilizer(GameObjectType type)
    {
        return (type == GameObjectType.FERTILIZER || type == GameObjectType.SPECIAL_FERTILIZER ||
            type == GameObjectType.BASIC_RETAINING_SOIL || type == GameObjectType.QUALITY_RETAINING_SOIL ||
            type == GameObjectType.DELUXE_RETAINING_SOIL);
    }

    private static boolean processBuildings(WorldScreen worldScreen, Tile tile, Point clicked)
    {
        if (processOvenMechanism(worldScreen, tile, clicked))
        {
            return true;
        }

        return processOvenMechanism(worldScreen, tile, clicked);
    }

    private static boolean processOvenMechanism(WorldScreen worldScreen, Tile tile, Point clicked)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        if (player.isInHome())
        {
            Cabin cabin = player.getCabin();
            if (cabin.getOvenPoint().equals(clicked))
            {
                worldScreen.toggleCookBookWindow();
                return true;
            }

            if (cabin.getRefrigeratorPoint().equals(clicked))
            {
                worldScreen.toggleRefrigeratorWindow();
                return true;
            }
        }

        return false;
    }

    private static boolean processCraftingStation(WorldScreen worldScreen, Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        if (player.isInFarm() || player.isInGreenHouse())
        {
            if (tile.getObject() != null && tile.getObject() instanceof CraftingItem)
            {
                CraftingItem craftingItem = (CraftingItem) tile.getObject();
                worldScreen.toggleCraftingWindow(craftingItem);
                return true;
            }
        }

        return false;
    }

    private static Player findNearbyPlayer(Point center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int tx = center.getX() + dx;
                int ty = center.getY() + dy;

                Player found = App.getCurrentGame().getPlayerByLocation(new Point(tx, ty));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean processNPCCommunication(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        NPC npc = game.getNpcFromLocation(tile.getPoint());
        if (npc == null)
        {
            return false;
        }

        FriendshipWithNpcData friendship = player.getNpcFriendship(npc);

        if (!friendship.isHasTalked())
        {
            friendship.talk();

            UIRenderer.showTextBox("You received 20 xp.");
        }

        String dialogue = npcDialogLLM.generateDynamicDialogue(npc);
        UIRenderer.showTextBox(npc.getName() + ": " + dialogue);

        return true;
    }

    private static boolean processAnimalInteraction(Tile tile)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (!player.isInFarm())
        {
            return false;
        }

        Farm farm = player.getFarm();
        Animal animal = player.getTileAnimal(tile);

        if (animal == null)
        {
            return false;
        }

        if (!animal.isPet())
        {
            UIRenderer.showTextBox("Your friendship level was increased 15 units!");
        }

        animal.pet();
        UIRenderer.showTextBox("You just pet " + animal.getName() + "!");
        MapVisual.playAnimationAt(GameAnimationType.PET, tile);

        return true;
    }

    private static boolean processNpcWindow(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        NPC npc = game.getNpcFromLocation(tile.getPoint());
        if (npc == null)
        {
            return false;
        }

        WorldScreen.getInstance().toggleNpcWindow(npc);
        return true;
    }

    private static boolean processAnimalWindow(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

       Animal animal = player.getTileAnimal(tile);
       if (animal == null)
       {
           return false;
       }

       WorldScreen.getInstance().toggleAnimalWindow(animal);
       return true;
    }

    private static boolean processAnimalPlacementTile(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        Map map = player.getCurrentMap();

        if (map.getMapType().getMapKind() != MapKind.FARM)
        {
            return false;
        }

        Farm farm = player.getFarm();
        GameObject object = player.getCurrentObject();

        if (object == null)
        {
            return false;
        }

        GameObjectType type = object.getObjectType();
        FarmAnimalsType animalsType = FarmAnimalsType.getAnimalType(type);

        if (animalsType == null)
        {
            return false;
        }

        if (tile.getTexture() != TileTexture.LAND && tile.getTexture() != TileTexture.GRASS)
        {
            UIRenderer.showTextBox("You can't put an animal on this tile");
            return true;
        }

        if (!farm.hasPlaceForAnimal(animalsType.getAnimalType()))
        {
            UIRenderer.showTextBox("you must first build a home for your pet!");
            return true;
        }

        showAnimalNameTextDialog(name ->
        {
            Vector2 loc = player.getCurrentMap().tileToWorld(tile);
            Animal pet = new Animal(name, animalsType, loc);
            AnimalCharacterController controller = new AnimalCharacterController(pet.getCharacter(), map, PLAYER_SPEED, TILE_SIZE);
            player.getAnimals().add(pet);
            player.getAnimalCharacterControllers().add(controller);
            UIRenderer.showTextBox("You placed " + name + " on this tile");
        });

        return true;
    }

    private static void showAnimalNameTextDialog(Consumer<String> onConfirm)
    {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();
        Dialog dialog = new Dialog("Animal Name", skin);
        dialog.getTitleLabel().setFontScale(FONT_SCALE);
        dialog.getTitleLabel().setAlignment(Align.center);

        final TextField textField = new TextField("", skin);
        textField.setMessageText("choose your pet's name");
        textField.setAlignment(Align.center);
        textField.getStyle().font.getData().setScale(FONT_SCALE); // Larger font

        Table contentTable = new Table();
        contentTable.add(textField).width(400).height(60).pad(20); // Larger field
        dialog.getContentTable().add(contentTable);

        // Create buttons with result objects
        Table buttonTable = new Table();
        buttonTable.defaults().pad(10).minWidth(150).height(60);

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.getLabel().setFontScale(FONT_SCALE);

        buttonTable.add(confirmButton).padRight(20);

        dialog.getButtonTable().add(buttonTable).padBottom(20);

        // Handle button clicks
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                String text = textField.getText().trim();
                if (!text.isEmpty())
                {
                    boolean isValid = App.getCurrentGame().getCurrentPlayer().validAnimalName(text);

                    if (isValid)
                    {
                        dialog.hide();
                        onConfirm.accept(text); // Pass name to callback
                    } else
                    {
                        UIRenderer.showTextBox("you already have a pet with this name!");
                    }
                } else
                {
                    UIRenderer.showTextBox("Name cannot be empty");
                }
            }
        });

        Stage stage = WorldScreen.getInstance().getUiStage();

        dialog.show(stage);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) / 2,
            (stage.getHeight() - dialog.getHeight()) / 2
        );
    }

    private static boolean processShopWindow(Tile tile) {
        Player player = App.getCurrentGame().getCurrentPlayer();
        ShopWindow shopWindow = WorldScreen.getInstance().getShopWindow();
        if (player.getCurrentMap() instanceof Shop) {
            Shop shop =  (Shop) player.getCurrentMap();

            if (Map.isNearOrOn(tile.getPoint(), shop.getCounterPoint()))
            {
                shopWindow.setShop(shop);
                shopWindow.toggleVisibility();
                return true;
            }
        }
        return false;
    }
}
