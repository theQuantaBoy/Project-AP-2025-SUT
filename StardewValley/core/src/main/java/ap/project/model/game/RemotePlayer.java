package ap.project.model.game;

import com.badlogic.gdx.math.Vector2;

public class RemotePlayer
{
    public final String playerId;
    public final PlayerCharacter character;
    public Vector2 targetPosition = new Vector2();
    public long lastUpdateTime = 0;
    private final Vector2 lastPosition = new Vector2();
    private boolean isMoving = false;

    public RemotePlayer(String playerId, PlayerCharacter character)
    {
        this.playerId = playerId;
        this.character = character;
        this.targetPosition.set(character.getPosition()); // Initialize to current position
    }

    public void updatePosition(float x, float y, AbstractCharacter.Direction direction)
    {
        // Check if position has changed significantly
        float dx = Math.abs(lastPosition.x - x);
        float dy = Math.abs(lastPosition.y - y);
        isMoving = (dx > 1f || dy > 1f); // Threshold of 1 pixel

        if (isMoving)
        {
            targetPosition.set(x, y);
            character.setDirection(direction);
            lastPosition.set(x, y);
        }

        lastUpdateTime = System.currentTimeMillis();
    }

    public boolean isMoving()
    {
        return isMoving;
    }
}
