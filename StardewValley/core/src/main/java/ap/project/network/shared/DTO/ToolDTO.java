package ap.project.network.shared.DTO;

import ap.project.model.enums.tool_enums.*;
import ap.project.model.tools.*;

public class ToolDTO extends GameObjectDTO
{
    public boolean initialized;

    public ToolType type;
    public String name;

    public HoeLevel hoeLevel;
    public PickaxeLevel pickaxeLevel;
    public AxeLevel axeLevel;
    public WateringCanLevel wateringCanLevel;
    public FishingPoleLevel fishingPoleLevel;
    public TrashCanLevel trashCanLevel;

    public ToolDTO() {}

    public ToolDTO(Tool tool)
    {
        super(tool);

        if (tool != null)
        {
            this.type = tool.getToolType();
            this.name = tool.getName();

            if (tool instanceof Hoe)
            {
                this.hoeLevel = ((Hoe) tool).getLevel();
            } else if (tool instanceof Pickaxe)
            {
                this.pickaxeLevel = ((Pickaxe) tool).getLevel();
            } else if (tool instanceof Axe)
            {
                this.axeLevel = ((Axe) tool).getLevel();
            } else if (tool instanceof WateringCan)
            {
                this.wateringCanLevel = ((WateringCan) tool).getLevel();
            } else if (tool instanceof FishingPole)
            {
                this.fishingPoleLevel = ((FishingPole) tool).getLevel();
            } else if (tool instanceof TrashCan)
            {
                this.trashCanLevel = ((TrashCan) tool).getLevel();
            }

            this.initialized = true;
        } else
        {
            this.initialized = false;
        }
    }
}
