package ap.project.network.shared.DTO;

import ap.project.model.enums.TileTexture;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import ap.project.network.shared.Mapper.Mapper;

public class TileDTO
{
    public boolean initialized = false;

    public Point point;

    public boolean hitByThunder;
    public TileTexture texture;

    public GameObjectDTO gameObjectDTO;
    public boolean isPloughed;
    public boolean isFertilized;

    public int wateringChance;
    public boolean growFaster;

    public boolean isImmuneFromCrows;
    public boolean isRandomForaging;

    public boolean isInCity;

    public String typeName;
    public boolean shouldBeWateredAutomatically;

    public TileDTO() {}

    public TileDTO(Tile tile)
    {
        if (tile != null)
        {
            this.point = new Point(tile.getX(), tile.getY());

            this.hitByThunder = tile.isHitByThunder();
            this.texture = tile.getTexture();

            this.gameObjectDTO = Mapper.toDTO(tile.getObject());
            this.isPloughed = tile.isPloughed();
            this.isFertilized = tile.isFertilized();

            this.wateringChance = tile.getWateringChance();
            this.growFaster = tile.isGrowFaster();

            this.isImmuneFromCrows = tile.isImmuneFromCrows();
            this.isRandomForaging = tile.isRandomForaging();

            this.isInCity = tile.isInCity();

            this.typeName = tile.getTypeName();
            this.shouldBeWateredAutomatically = tile.isShouldBeWateredAutomatically();

            this.initialized = true;
        }
    }
}
