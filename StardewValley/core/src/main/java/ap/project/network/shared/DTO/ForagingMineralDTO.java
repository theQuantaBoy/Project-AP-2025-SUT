package ap.project.network.shared.DTO;

import ap.project.model.enums.resources_enums.ForagingMineralType;
import ap.project.model.resources.ForagingMineral;

public class ForagingMineralDTO extends GameObjectDTO
{
    public ForagingMineralType foragingMineralType;

    public ForagingMineralDTO() {}

    public ForagingMineralDTO(ForagingMineral foragingMineral)
    {
        super(foragingMineral);

        if (foragingMineral != null)
        {
            this.foragingMineralType = foragingMineral.getMineralType();
        }
    }
}
