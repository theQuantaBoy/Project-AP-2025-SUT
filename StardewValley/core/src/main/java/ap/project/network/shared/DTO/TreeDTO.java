package ap.project.network.shared.DTO;

import ap.project.model.enums.resources_enums.FruitType;
import ap.project.model.resources.Tree;

public class TreeDTO extends PlantDTO
{
    public FruitType fruitType;
    public int fruitHarvestCycle;

    public TreeDTO() {}

    public TreeDTO(Tree tree)
    {
        super(tree);

        if (tree != null)
        {
            this.fruitType = tree.getFruit();
            this.fruitHarvestCycle = tree.getFruitHarvestCycle();
        }
    }
}
