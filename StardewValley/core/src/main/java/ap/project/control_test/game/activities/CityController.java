package ap.project.control_test.game.activities;

import ap.project.model.App.App;
import ap.project.model.game.Player;
import ap.project.model.App.Result;
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
