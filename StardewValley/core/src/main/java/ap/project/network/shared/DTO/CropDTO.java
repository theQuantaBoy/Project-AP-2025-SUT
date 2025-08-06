package ap.project.network.shared.DTO;

import ap.project.model.resources.Crop;

public class CropDTO extends PlantDTO
{
    public boolean oneTime;
    public int growthTime;
    public boolean canBecomeGiant;

    public CropDTO() {}

    public CropDTO(Crop crop)
    {
        super(crop);

        if (crop != null)
        {
            this.oneTime = crop.isOneTime();
            this.growthTime = crop.getGrowthTime();
            this.canBecomeGiant = crop.isCanBecomeGiant();
        }
    }
}
