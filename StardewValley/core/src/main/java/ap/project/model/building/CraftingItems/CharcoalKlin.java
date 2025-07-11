package ap.project.model.building.CraftingItems;

import ap.project.model.App;
import ap.project.model.GameObject;
import ap.project.model.Player;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.view.HomeMenu;

public class CharcoalKlin extends CraftingItem
{
    public CharcoalKlin()
    {
        super(CraftingRecipeEnums.CHARCOAL_KILN_RECIPE);
    }

    @Override
    public void doItsThing()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();
        GameObject wood = player.getItemInInventory(GameObjectType.WOOD);
        if (wood != null && (player.hasEnoughInInventory(GameObjectType.COAL, 1) ||
                (player.getInventoryCapacity() == -1 || player.getInventoryCapacity() >= 0)))
        {
            int coalAmount = Math.min(10, wood.getNumber());
            player.removeAmountFromInventory(GameObjectType.WOOD, coalAmount);
            player.addToInventory(GameObjectType.COAL, coalAmount);
        } else
        {
            HomeMenu.println("You can't use this device right now!");
        }
    }
}
