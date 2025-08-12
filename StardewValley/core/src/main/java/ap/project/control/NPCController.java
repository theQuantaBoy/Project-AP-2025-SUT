package ap.project.control;

import ap.project.model.App.App;
import ap.project.model.enums.NpcDetails;
import ap.project.model.game.*;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class NPCController
{
    private final NPCCharacter npc;
    private final Map map;
    private final float speed;
    private final float tileSize;
    private final Random random = new Random();
    private final NpcDetails details;

    private ArrayList<Point> path = null;
    private int pathIndex = 0;
    private NPCState state = NPCState.AT_HOME;
    private float wanderTimer = 0f;
    private static final float WANDER_CHECK_INTERVAL = 10f;

    public enum NPCState
    {
        AT_HOME,
        GOING_TO_WORK,
        AT_WORK,
        AFTER_WORK
    }

    public NPCController(NPCCharacter npc, Map map, float speed, float tileSize, NpcDetails details)
    {
        this.npc = npc;
        this.map = map;
        this.speed = speed;
        this.tileSize = tileSize;
        this.details = details;

        // Initialize NPC at home
        npc.setPosition(map.tileToWorld(map.getTile(details.getHomePoint().getX(), details.getHomePoint().getY())));
    }

    public void update(float delta)
    {
        int currentHour = App.getCurrentGame().getCurrentTime().getHour();

        // Handle schedule-based state transitions
        if (currentHour < details.getWorkStartHour())
        {
            handleHomeState();
        }

        else if (currentHour == details.getWorkStartHour())
        {
            handleGoingToWork();
        }

        else if (currentHour > details.getWorkStartHour() && currentHour <= details.getWorkEndHour())
        {
            handleAtWork();
        }

        else
        {
            handleAfterWork(delta);
        }

        // Handle movement if needed
        if (path != null && pathIndex < path.size())
        {
            moveAlongPath(delta);
        } else
        {
            npc.setMoving(false);
        }
    }

    private void handleHomeState()
    {
        if (state != NPCState.AT_HOME)
        {
            state = NPCState.AT_HOME;
            setTarget(details.getHomePoint());
        }
    }

    private void handleGoingToWork()
    {
        if (state != NPCState.GOING_TO_WORK)
        {
            state = NPCState.GOING_TO_WORK;
            setTarget(details.getWorkPoint());
        }
    }

    private void handleAtWork()
    {
        if (state != NPCState.AT_WORK)
        {
            state = NPCState.AT_WORK;
            npc.setDirection(AbstractCharacter.Direction.DOWN);
            npc.resetAnimation();
            setTarget(details.getWorkPoint());
        }
    }

    private void handleAfterWork(float delta)
    {
        if (state != NPCState.AFTER_WORK)
        {
            state = NPCState.AFTER_WORK;
            wanderTimer = 0f;
        }

        // Wander periodically
        wanderTimer += delta;
        if (wanderTimer >= WANDER_CHECK_INTERVAL)
        {
            wanderTimer = 0f;
            if (path == null || pathIndex >= path.size())
            {
                generateRandomWanderPoint();
            }
        }
    }

    private void setTarget(Point target)
    {
        Point currentTile = map.worldToTile(npc.getPosition().x, npc.getPosition().y);
        this.path = map.findShortestPath(currentTile, target);
        this.pathIndex = 1;
    }

    private void generateRandomWanderPoint()
    {
        Point currentTile = map.worldToTile(npc.getPosition().x, npc.getPosition().y);

        // Generate random point within 20 tile radius
        int randX = currentTile.getX() + random.nextInt(41) - 20;
        int randY = currentTile.getY() + random.nextInt(41) - 20;

        // Clamp to map bounds
        randX = Math.max(0, Math.min(randX, map.getWidth() - 1));
        randY = Math.max(0, Math.min(randY, map.getHeight() - 1));

        setTarget(new Point(randX, randY));
    }

    private void moveAlongPath(float delta)
    {
        npc.setMoving(true);
        Point targetTile = path.get(pathIndex);
        Vector2 targetWorld = map.tileToWorld(map.getTile(targetTile.getX(), targetTile.getY()));
        Vector2 pos = npc.getPosition();

        float dx = targetWorld.x - pos.x;
        float dy = targetWorld.y - pos.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < 2f)
        {
            pathIndex++;
            if (pathIndex >= path.size())
            {
                path = null;
                npc.setMoving(false);
                return;
            }
        }

        // Update direction
        if (Math.abs(dx) > Math.abs(dy))
        {
            npc.setDirection(dx > 0 ?
                AbstractCharacter.Direction.RIGHT :
                AbstractCharacter.Direction.LEFT);
        } else
        {
            npc.setDirection(dy > 0 ?
                AbstractCharacter.Direction.UP :
                AbstractCharacter.Direction.DOWN);
        }

        npc.updateAnimation(delta);

        // Move towards target
        float nx = dx / dist;
        float ny = dy / dist;
        pos.x += nx * speed * delta;
        pos.y += ny * speed * delta;
    }
}
