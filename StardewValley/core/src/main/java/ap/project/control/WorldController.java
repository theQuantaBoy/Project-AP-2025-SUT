package ap.project.control;

import ap.project.model.App.App;
import ap.project.model.building.CraftingItem;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.TileTexture;
import ap.project.model.enums.Weather;
import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.ResourceItem;
import ap.project.model.enums.resources_enums.TreeType;
import ap.project.model.game.*;
import ap.project.model.resources.*;
import ap.project.model.shops.Shop;
import ap.project.model.tools.*;
import ap.project.screen.WorldScreen;
import ap.project.visual.UIRenderer;

public class WorldController
{
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

        if (!Map.isNearOrOn(location, clicked))
        {
            return;
        }

        if (processPickingUpForagingItem(tile))
        {
            return;
        }

        if (processMapNavigation(tile))
        {
            return;
        }

        if (processToolUse(tile))
        {
            return;
        }

        if (processObjectUse(tile))
        {
            return;
        }

        if (processBuildings(worldScreen, tile, clicked))
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
        Point location = player.getLocation();

        if (!Map.isNearOrOn(location, clicked))
        {
            return;
        }

        if (processCraftingStation(worldScreen, tile))
        {
            return;
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
                player.goToGreenHouse();
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
                    player.goToShop(shop);
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

                UIRenderer.showTextBox("you harvested " + (giant ? "ten " : "one ") + crop.getObjectType().toString() + ".");
                return true;
            }

        }

        if (tool instanceof Pickaxe)
        {
            if (!tile.hasPlants() && tile.getObject() != null)
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
                }

                UIRenderer.showTextBox(object.getObjectType().toString() + " added to your inventory");
                return true;
            }

            else if (tile.getObject() instanceof ForagingMineral)
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
            } else if (tile.getTexture().equals(TileTexture.LAND))
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
            } else if (tile.getObject() instanceof ForagingSeed)
            {
                tile.setObject(null);
                player.increaseEnergy((int)(weatherModifier * -((Pickaxe) tool).getLevel().getBaseEnergyUsage()));

                UIRenderer.showTextBox("seed is removed");
                return true;
            } else
            {
                player.increaseEnergy((int)(weatherModifier * -((Pickaxe) tool).getLevel().getFailedEnergyUsage()));

                UIRenderer.showTextBox("you can't use pickaxe on this tile");
                return true;
            }
        }

        return false;
    }

    private static boolean processObjectUse(Tile tile)
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

        if (processCraftingPlacement(tile))
        {
            return true;
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
            return true;
        }

        if (tile.getTexture() != TileTexture.LAND && tile.getTexture() != TileTexture.GRASS)
        {
            UIRenderer.showTextBox("you can't put a crafting item on this type of tile");
            return true;
        }

        tile.setObject(craftingItem);

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

        UIRenderer.showTextBox("successfully put " + object.getObjectType() + " on tile!");
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
            tile.setWateringChance(0);

            UIRenderer.showTextBox("Tile fertilized with fertilizer.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.SPECIAL_FERTILIZER)
        {
            tile.fertilize();
            tile.setWateringChance(0);
            tile.setGrowFaster();

            UIRenderer.showTextBox("Tile fertilized with special fertilizer.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.BASIC_RETAINING_SOIL)
        {
            tile.fertilize();
            tile.setWateringChance(40);

            UIRenderer.showTextBox("Tile fertilized with basic retaining soil.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.QUALITY_RETAINING_SOIL)
        {
            tile.fertilize();
            tile.setWateringChance(70);

            UIRenderer.showTextBox("Tile fertilized with quality retaining soil.");
            return true;
        }

        if (fertilizer.getObjectType() == GameObjectType.DELUXE_RETAINING_SOIL)
        {
            tile.fertilize();
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
            Crop crop = new Crop(cropType, tile);
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
            Tree tree = new Tree(treeType, tile);
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
}
