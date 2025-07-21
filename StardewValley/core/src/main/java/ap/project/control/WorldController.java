package ap.project.control;

import ap.project.model.App.App;
import ap.project.model.game.*;
import ap.project.model.shops.Shop;

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
}
