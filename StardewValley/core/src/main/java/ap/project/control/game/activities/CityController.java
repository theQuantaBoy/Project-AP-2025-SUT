package ap.project.control.game.activities;

import ap.project.model.App;
import ap.project.model.Player;
import ap.project.model.Result;
import ap.project.model.enums.Menu;

public class CityController
{
    public Result goOut()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();
        player.goToFarm();
        App.setCurrentMenu(Menu.GameMenu);
        return new Result(true, "Going to farm...");
    }
}
