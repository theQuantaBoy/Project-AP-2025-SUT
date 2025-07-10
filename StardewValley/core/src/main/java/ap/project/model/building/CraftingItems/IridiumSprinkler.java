package ap.project.model.building.CraftingItems;

import ap.project.model.App.App;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.game.Map;
import ap.project.model.game.Player;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import ap.project.model.resources.Plant;

public class IridiumSprinkler extends CraftingItem
{
    public IridiumSprinkler()
    {
        super(CraftingRecipeEnums.IRIDIUM_SPRINKLER_RECIPE);
    }

    @Override
    public void doItsThing()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();
        Map map = player.getCurrentMap();
        Point location = player.getLocation();
        int x = location.getX();
        int y = location.getY();

        for (int i = -2; i <= 2; i++)
        {
            for (int j = -2; j <= 2; j++)
            {
                if (i != 0 && j != 0 && map.isInBounds(x + i, y + j))
                {
                    Tile tile = map.getTile(x + i, y + j);
                    if (tile.hasPlants())
                    {
                        Plant plant = (Plant) tile.getObject();
                        plant.water();
                    }
                }
            }
        }
    }
}
