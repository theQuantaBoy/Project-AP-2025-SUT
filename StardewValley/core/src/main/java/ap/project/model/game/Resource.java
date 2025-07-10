package ap.project.model.game;

import ap.project.model.enums.resources_enums.ResourceItem;

public class Resource extends GameObject
{
    private final ResourceItem resourceType;

    public Resource(ResourceItem type)
    {
        this.resourceType = type;
        this.ObjectType = type.getType();
    }

    public ResourceItem getResourceType()
    {
        return resourceType;
    }
}
