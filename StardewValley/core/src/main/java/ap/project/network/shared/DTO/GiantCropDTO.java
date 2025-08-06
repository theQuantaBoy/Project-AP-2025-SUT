package ap.project.network.shared.DTO;

import ap.project.model.game.Tile;
import ap.project.model.resources.GiantCrop;

import java.util.ArrayList;

public class GiantCropDTO extends CropDTO
{
    public Tile rootTile;

    public GiantCropDTO() {}

    public GiantCropDTO(GiantCrop giantCrop)
    {
        super(giantCrop);

        if (giantCrop != null)
        {
            this.rootTile = giantCrop.getRootTile();
        }
    }
}
