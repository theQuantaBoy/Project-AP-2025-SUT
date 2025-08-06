package ap.project.network.shared.DTO;

import ap.project.model.enums.tool_enums.BackPackLevel;
import ap.project.model.game.GameObject;
import ap.project.model.tools.BackPack;
import ap.project.model.tools.Tool;

import java.util.ArrayList;
import java.util.List;

public class BackPackDTO
{
    public BackPackLevel level;
    public List<GameObjectDTO> slots = new ArrayList<>();
    public List<ToolDTO> tools = new ArrayList<>();

    public BackPackDTO() {}

    public BackPackDTO(BackPack backPack)
    {
        this.level = backPack.getLevel();

        for (GameObject o : backPack.getSlots())
        {
            slots.add(new GameObjectDTO(o));
        }

        for (Tool tool : backPack.getTools())
        {
            tools.add(new ToolDTO(tool));
        }
    }
}
