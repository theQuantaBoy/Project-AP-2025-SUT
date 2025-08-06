package ap.project.network.shared.DTO;

import ap.project.model.enums.resources_enums.ResourceItem;
import ap.project.model.game.Resource;

public class ResourceDTO extends GameObjectDTO
{
    public ResourceItem resourceItem;

    public ResourceDTO() {}

    public ResourceDTO(Resource resource)
    {
        super(resource);

        if (resource != null)
        {
            this.resourceItem = resource.getResourceType();
        }
    }
}
