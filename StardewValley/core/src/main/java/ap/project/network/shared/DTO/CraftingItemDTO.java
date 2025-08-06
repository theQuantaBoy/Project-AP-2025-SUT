package ap.project.network.shared.DTO;

import ap.project.model.building.CraftingItem;
import ap.project.model.enums.building_enums.ArtisanGoodsType;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.game.GameObject;

import java.util.ArrayList;

public class CraftingItemDTO extends GameObjectDTO
{
    public CraftingItem.ItemType itemType;
    public CraftingRecipeEnums craftingType;

    public ArtisanGoodsType artisanType;
    public boolean isWorking;

    public int startDay;
    public int startHour;

    public int neededDays;
    public int neededHours;

    public ArrayList<GameObjectDTO> craftingIngredients = new ArrayList<>();
    public TreeDTO tileDTO;

    public CraftingItemDTO() {}

    public CraftingItemDTO(CraftingItem craftingItem)
    {
        super(craftingItem);

        if (craftingItem != null)
        {
            this.itemType = craftingItem.getItemType();
            this.craftingType = craftingItem.getCraftingType();

            this.artisanType = craftingItem.getArtisanType();
            this.isWorking = craftingItem.isWorking();

            this.startDay = craftingItem.getStartDay();
            this.startHour = craftingItem.getStartHour();

            this.neededDays = craftingItem.getNeededDays();
            this.neededHours = craftingItem.getNeededHours();

            for (GameObject o : craftingItem.getCraftingIngredients())
            {
                this.craftingIngredients.add(new GameObjectDTO(o));
            }
            this.tileDTO = new TreeDTO();
        }
    }
}
