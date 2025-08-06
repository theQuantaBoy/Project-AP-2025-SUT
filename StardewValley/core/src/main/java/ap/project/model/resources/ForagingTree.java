package ap.project.model.resources;

import ap.project.model.game.GameObject;
import ap.project.model.enums.resources_enums.ForagingTreeType;
import ap.project.model.enums.Season;

import java.util.List;

public class ForagingTree extends GameObject
{
    private final ForagingTreeType treeType;

    public ForagingTree(ForagingTreeType type)
    {
        this.treeType = type;
        this.ObjectType = type.getType();
    }

    public ForagingTreeType getTreeType()
    {
        return treeType;
    }
}
