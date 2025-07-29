package ap.project.control;

import ap.project.model.App.App;
import ap.project.model.enums.TileTexture;
import ap.project.model.game.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class CharacterController
{

    private final AbstractCharacter character;
    private final Map map;
    private final float speed;
    private final float tileSize;

    private int upKey = Input.Keys.W,
        downKey = Input.Keys.S,
        leftKey = Input.Keys.A,
        rightKey =  Input.Keys.D;

    private ArrayList<Point> path = null;
    private int pathIndex = 0;

    public CharacterController(AbstractCharacter character, Map map, float speed, float tileSize)
    {
        this.character = character;
        this.map = map;
        this.speed = speed;
        this.tileSize = tileSize;
    }

    public void update(float delta)
    {
        if (Gdx.input.isKeyPressed(rightKey) ||
            Gdx.input.isKeyPressed(leftKey) ||
            Gdx.input.isKeyPressed(upKey) ||
            Gdx.input.isKeyPressed(downKey))
        {
            endWalk();
        }

        if (path != null && pathIndex < path.size())
        {
            Point point = path.get(pathIndex);
            Vector2 targetWorld = map.tileToWorld(map.getTile(point.getX(), point.getY()));
            Vector2 pos = character.getPosition();

            float dx = targetWorld.x - pos.x;
            float dy = targetWorld.y - pos.y;

            float dist = (float)Math.sqrt(dx * dx + dy * dy);

            if (dist < 2f)
            {
                pathIndex += 1;
                if (pathIndex >= path.size())
                {
                    path = null;
                }
                return;
            }

            // Direction & animation
            if (Math.abs(dx) > Math.abs(dy))
                character.setDirection(dx > 0 ? AbstractCharacter.Direction.RIGHT : AbstractCharacter.Direction.LEFT);
            else
                character.setDirection(dy > 0 ? AbstractCharacter.Direction.UP : AbstractCharacter.Direction.DOWN);

            character.updateAnimation(delta);

            float nx = dx / dist;
            float ny = dy / dist;

            float moveSpeed = speed;
            pos.x += nx * moveSpeed * delta;
            pos.y += ny * moveSpeed * delta;

            App.getCurrentGame().getCurrentPlayer().increaseEnergy(-0.8f * delta);

            return;
        }

        float rawDx = (Gdx.input.isKeyPressed(rightKey) ? 1 : 0)
                - (Gdx.input.isKeyPressed(leftKey)  ? 1 : 0);
        float rawDy = (Gdx.input.isKeyPressed(upKey)    ? 1 : 0)
                - (Gdx.input.isKeyPressed(downKey)  ? 1 : 0);

        if (rawDx == 0 && rawDy == 0)
        {
            character.resetAnimation();
            return;
        }

        if (Math.abs(rawDx) > Math.abs(rawDy))
            character.setDirection(rawDx > 0 ? AbstractCharacter.Direction.RIGHT
                    : AbstractCharacter.Direction.LEFT);
        else
            character.setDirection(rawDy > 0 ? AbstractCharacter.Direction.UP
                    : AbstractCharacter.Direction.DOWN);

        character.updateAnimation(delta);

        float len = (float)Math.sqrt(rawDx*rawDx + rawDy*rawDy);
        float nx  = rawDx / len;
        float ny  = rawDy / len;

        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        float moveSpeed = speed * (shift ? 2f : 1f);
        float dx = nx * moveSpeed * delta;
        float dy = ny * moveSpeed * delta;

        Vector2 pos = character.getPosition();
        float nextX = pos.x + dx;
        float nextY = pos.y + dy;

        Point tilePos = map.worldToTile(nextX, nextY);
        Tile target  = map.getTile(tilePos.getX(), tilePos.getY());

        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        boolean blocked = target == null ||
                (switch (target.getTexture()) {
                    case LAKE, UNPASSABLE, BUILDING, OBJECT, DOOR -> true;
                    default -> false;
                });

        if (target == null || target.getObject() != null)
        {
            blocked = true;
        }

        if (ctrl)
        {
            blocked = false;
        }

        if (!blocked)
        {
            pos.set(nextX, nextY);
            App.getCurrentGame().getCurrentPlayer().increaseEnergy(-0.8f * delta);
        }
    }

    public void render(Batch batch) {
        Vector2 pos = character.getPosition();
        batch.draw(character.getShadow(), pos.x, pos.y - 3);
        TextureRegion frame = character.getCurrentFrame();
        batch.draw(frame, pos.x, pos.y, 0, 0,
            frame.getRegionWidth(), frame.getRegionHeight(),
            1f, 1f, 0); // scale, rotation
    }

    public AbstractCharacter getCharacter() {
        return character;
    }

    public void chnageMoveKeys(int up, int left, int down, int right)
    {
        upKey = up;
        downKey = down;
        leftKey = left;
        rightKey = right;
    }

    public void moveToPath(ArrayList<Point> path)
    {
        if (path == null || path.size() < 2) {
            return;
        }

        this.path = path;
        this.pathIndex = 1;
    }

    public void endWalk()
    {
        this.path = null;
        this.pathIndex = 0;
    }
}
