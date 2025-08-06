package ap.project.network.shared.DTO;

import ap.project.model.enums.Season;
import ap.project.model.enums.resources_enums.ForagingTreeType;
import ap.project.model.resources.ForagingTree;

import java.util.ArrayList;
import java.util.List;

public class ForagingTreeDTO extends GameObjectDTO
{
    public ForagingTreeType foragingTreeType;
    public List<Season> seasons = new ArrayList<>();

    public ForagingTreeDTO() {}

    public ForagingTreeDTO(ForagingTree foragingTree)
    {
        super(foragingTree);

        if (foragingTree != null)
        {
            this.foragingTreeType = foragingTree.getTreeType();
        }
    }
}
