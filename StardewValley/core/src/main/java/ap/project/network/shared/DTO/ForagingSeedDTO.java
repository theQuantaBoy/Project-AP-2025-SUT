package ap.project.network.shared.DTO;

import ap.project.model.enums.resources_enums.ForagingSeedType;
import ap.project.model.resources.ForagingSeed;

public class ForagingSeedDTO extends GameObjectDTO
{
    public ForagingSeedType foragingSeedType;

    public ForagingSeedDTO() {}

    public ForagingSeedDTO(ForagingSeed foragingSeed)
    {
        super(foragingSeed);

        if (foragingSeed != null)
        {
            this.foragingSeedType = foragingSeed.getSeedType();
        }
    }
}
