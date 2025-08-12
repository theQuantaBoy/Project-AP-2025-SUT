package ap.project.control.game.activities;

import ap.project.model.App.App;
import ap.project.model.enums.ShopType;
import ap.project.model.game.City;
import ap.project.model.game.Game;
import ap.project.model.game.Player;
import ap.project.model.App.Result;
import ap.project.model.enums.Menu;
import ap.project.model.shops.Shop;
import ap.project.screen.WorldScreen;

import java.awt.desktop.AboutHandler;

public class CityController
{
    public Result goToPlace(String place)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();
        City city = App.getCurrentGame().getCity();

        if (player.isInCity() && place.equalsIgnoreCase("farm"))
        {
            player.goToFarm();
            App.setCurrentMenu(Menu.GameMenu);
            return new Result(true, "Going to farm...");
        }

        if (player.isInShop() && place.equalsIgnoreCase("city"))
        {
            player.goToCity();
            return new Result(true, "Going back to city...");
        }

        ShopType shopType = ShopType.getShop(place);
        if (shopType == null)
        {
            return new Result(false, "Shop type not found");
        }

        Shop shop = city.getShop(shopType);

        if (shop == null)
        {
            return new Result(false, "Shop not found");
        }

        player.goToShop(shop);

        return new Result(true, "Going to place");
    }
}
