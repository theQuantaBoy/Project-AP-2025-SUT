package ap.project.model.resources;

import ap.project.model.App.App;
import ap.project.model.game.Map;
import ap.project.model.game.Player;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import ap.project.model.enums.resources_enums.CropType;

import java.util.ArrayList;

public class GiantCrop extends Crop
{
    private final Point rootPoint;

    public GiantCrop(CropType cropType, Point rootPoint, int playerIndex, boolean isGrowFaster)
    {
        super(cropType, rootPoint, playerIndex, isGrowFaster);
        super.setHasStarted();
        this.rootPoint = rootPoint;
    }

    @Override
    public boolean isGiant()
    {
        return true;
    }

    public static ArrayList<Tile> get2x2Tiles(Tile rootTile)
    {
        Map map = App.getCurrentGame().getCurrentPlayer().getCurrentMap();

        int x = rootTile.getX();
        int y = rootTile.getY();

        ArrayList<Tile> tiles = new ArrayList<>();

        for (int dy = 0; dy <= 1; dy++)
        {
            for (int dx = 0; dx <= 1; dx++)
            {
                int nx = x + dx;
                int ny = y + dy;

                if (!map.isInBounds(nx, ny))
                {
                    return null;
                }

                Tile t = map.getTile(nx, ny);
                if (t == null)
                {
                    return null;
                }

                tiles.add(t);
            }
        }

        return tiles;
    }

    public Tile getRootTile()
    {
        Player player = App.getCurrentGame().getPlayers().get(this.playerIndex);
        if (!isInGreenhouse())
        {
            return player.getFarm().getTile(rootPoint.getX(), rootPoint.getY());
        }

        return player.getGreenHouse().getTile(rootPoint.getX(), rootPoint.getY());
    }

    public Point getRootPoint()
    {
        return rootPoint;
    }
}
