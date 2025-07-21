package ap.project.control;

import ap.project.model.App.App;
import ap.project.model.App.Result;
import ap.project.model.enums.TileTexture;
import ap.project.model.enums.Weather;
import ap.project.model.game.*;
import ap.project.model.resources.Plant;
import ap.project.model.shops.Shop;
import ap.project.model.tools.Hoe;
import ap.project.model.tools.Tool;
import ap.project.model.tools.WateringCan;
import ap.project.visual.UIRenderer;

public class WorldController
{
    public static void processClickLeft(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();

        Point clicked = tile.getPoint();
        Point location = player.getLocation();

        if (!Map.isNearOrOn(location, clicked))
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

        if (player.isInHome())
        {
            Point door = player.getCabin().getStartingPoint();

            if (Map.isNearOrOn(door, clicked))
            {
                player.goToFarm();
                return;
            }
        }

        if (player.isInGreenHouse())
        {
            Point door = player.getGreenHouse().getStartingPoint();

            if (Map.isNearOrOn(door, clicked))
            {
                player.goToFarm();
                return;
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
                return;
            }

            if (Map.isNearOrOn(greenhouseDoor, clicked))
            {
                player.goToGreenHouse();
                return;
            }

            if (Map.isNearOrOn(exitPoint, clicked))
            {
                player.goToCity();
                return;
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
                    return;
                }
            }

            if (Map.isNearOrOn(startingPoint, clicked))
            {
                player.goToFarm();
                return;
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

        return false;
    }

    private static boolean processObjectUse(Tile tile)
    {
        Game game = App.getCurrentGame();
        Player player = game.getCurrentPlayer();
        Map map = player.getCurrentMap();

        Point clicked = tile.getPoint();
        Point location = player.getLocation();


        return false;
    }
}
