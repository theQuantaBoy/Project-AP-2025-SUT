package ap.project.model.building.CraftingItems;


import ap.project.model.game.GameObject;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;

public abstract class CraftingItem extends GameObject
{
    protected final CraftingRecipeEnums craftType;

    public CraftingItem(CraftingRecipeEnums craftType)
    {
        super(craftType.getType(), 1);
        this.craftType = craftType;
    }

    public abstract void doItsThing();
}
