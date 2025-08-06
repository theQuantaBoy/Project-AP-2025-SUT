package ap.project.network.shared.DTO;

import ap.project.model.enums.tool_enums.ToolType;
import ap.project.model.tools.Tool;

public class ToolDTO
{
    public boolean initialized;

    public ToolType type;
    public String name;

    public ToolDTO() {}

    public ToolDTO(Tool tool)
    {
        if (tool != null)
        {
            this.type = tool.getToolType();
            this.name = tool.getName();

            this.initialized = true;
        } else
        {
            this.initialized = false;
        }
    }
}
