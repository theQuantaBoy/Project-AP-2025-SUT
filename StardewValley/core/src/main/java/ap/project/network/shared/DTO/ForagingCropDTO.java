package ap.project.network.shared.DTO;

import ap.project.model.enums.resources_enums.ForagingCropType;
import ap.project.model.resources.ForagingCrop;

public class ForagingCropDTO extends GameObjectDTO
{
    public ForagingCropDTO() {}

    public ForagingCropDTO(ForagingCrop foragingCrop)
    {
        super(foragingCrop);
    }
}
