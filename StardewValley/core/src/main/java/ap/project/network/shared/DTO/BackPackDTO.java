package ap.project.network.shared.DTO;

import ap.project.model.enums.tool_enums.BackPackLevel;
import ap.project.model.game.GameObject;
import ap.project.model.tools.BackPack;
import ap.project.model.tools.Tool;
import ap.project.network.shared.Mapper.Mapper;

import java.util.ArrayList;
import java.util.List;

public class BackPackDTO
{
    public BackPackLevel level;
    public ArrayList<GameObjectDTO> slots = new ArrayList<>();
    public ArrayList<ToolDTO> tools = new ArrayList<>();

    public BackPackDTO() {}

    public BackPackDTO(BackPack backPack)
    {
        this.level = backPack.getLevel();

        for (GameObject o : backPack.getSlots())
        {
            if (o != null)
            {
                slots.add(Mapper.toDTO(o));
            }
        }

        for (Tool tool : backPack.getTools())
        {
            if (tool != null)
            {
                tools.add(new ToolDTO(tool));
            }
        }
    }
}
