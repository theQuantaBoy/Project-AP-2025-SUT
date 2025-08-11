package ap.project.control;

import ap.project.model.game.*;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Random;

public class AnimalCharacterController
{
    private final AnimalCharacter animal;
    private final Map map;
    private final float speed;
    private final float tileSize;
    private final Random random = new Random();

    private ArrayList<Point> path = null;
    private int pathIndex = 0;
    private float idleTimer = 0f;
    private static final float MOVE_CHECK_INTERVAL = 2f; // Seconds between move checks
    private static final float MOVE_CHANCE = 0.8f; // 30% chance to move when idle

    public AnimalCharacterController(AnimalCharacter animal, Map map, float speed, float tileSize)
    {
        this.animal = animal;
        this.map = map;
        this.speed = speed;
        this.tileSize = tileSize;
    }

    public void update(float delta)
    {
        // Handle current path movement
        if (path != null && pathIndex < path.size()) {
            animal.setMoving(true);

            Point point = path.get(pathIndex);
            Vector2 targetWorld = map.tileToWorld(map.getTile(point.getX(), point.getY()));
            Vector2 pos = animal.getPosition();

            float dx = targetWorld.x - pos.x;
            float dy = targetWorld.y - pos.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            // Reached current waypoint
            if (dist < 2f) {
                pathIndex++;
                // Reached final destination
                if (pathIndex >= path.size()) {
                    path = null;
                    animal.setMoving(false);
                }
                return;
            }

            // Update direction and animation
            if (Math.abs(dx) > Math.abs(dy)) {
                animal.setDirection(dx > 0 ?
                    AbstractCharacter.Direction.RIGHT :
                    AbstractCharacter.Direction.LEFT);
            } else {
                animal.setDirection(dy > 0 ?
                    AbstractCharacter.Direction.UP :
                    AbstractCharacter.Direction.DOWN);
            }

            animal.updateAnimation(delta);

            // Normalize direction vector
            float nx = dx / dist;
            float ny = dy / dist;

            // Update position
            pos.x += nx * speed * delta;
            pos.y += ny * speed * delta;
        }
        // Handle idle state and random movement
        else {
            animal.setMoving(false);
            idleTimer += delta;

            // Check if we should start a new movement
            if (idleTimer >= MOVE_CHECK_INTERVAL) {
                idleTimer = 0f;

                if (random.nextFloat() < MOVE_CHANCE) {
                    generateRandomPath();
                }
            }
        }
    }

    private void generateRandomPath() {
        Vector2 currentPos = animal.getPosition();
        Point currentTile = map.worldToTile(currentPos.x, currentPos.y);

        // Generate random destination within 5 tiles
        int randX = currentTile.getX() + random.nextInt(11) - 5; // -5 to +5
        int randY = currentTile.getY() + random.nextInt(11) - 5; // -5 to +5

        // Clamp to map bounds
        randX = Math.max(0, Math.min(randX, map.getWidth() - 1));
        randY = Math.max(0, Math.min(randY, map.getHeight() - 1));

        Point destination = new Point(randX, randY);
        ArrayList<Point> newPath = map.findShortestPath(currentTile, destination);

        if (newPath != null && newPath.size() > 1) {
            this.path = newPath;
            this.pathIndex = 0;
        }
    }

    public AnimalCharacter getAnimal() {
        return animal;
    }
}
