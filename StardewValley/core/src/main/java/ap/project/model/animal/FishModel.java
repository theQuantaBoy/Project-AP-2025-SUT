// FishModel.java
package ap.project.model.animal;

import ap.project.model.enums.animal_enums.FishBehavior;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class FishModel {
    private float x, y;
    private float velocityY;
    private FishBehavior behavior;
    private Random random;
    private float movementTimer;
    private int currentDirection; // 0: up, 1: down, 2: stay
    private float consecutiveDirectionTime;
    private final float baseMovementRange = 5f;

    public FishModel(FishBehavior behavior) {
        this.behavior = behavior;
        this.random = new Random();
        reset();
    }

    public void reset() {
        // Start at a random vertical position
        x = (MiniGameState.TRACK_WIDTH - MiniGameState.FISH_SIZE) / 2;
        y = random.nextFloat() * (MiniGameState.TRACK_HEIGHT - MiniGameState.FISH_SIZE);
        velocityY = 0;
        movementTimer = 0;
        consecutiveDirectionTime = 0;
        currentDirection = random.nextInt(3); // Start with random direction
    }

    public void update(float delta) {
        movementTimer += delta;
        consecutiveDirectionTime += delta;

        // Handle behavior-specific movement logic
        switch (behavior) {
            case MIXED:
                // Change direction randomly every 0.5 seconds
                if (movementTimer >= 0.5f) {
                    currentDirection = random.nextInt(3);
                    movementTimer = 0;
                }
                break;

            case SMOOTH:
                // 70% chance to keep the same direction, 30% to change
                if (movementTimer >= 0.5f) {
                    if (random.nextFloat() > 0.7f) {
                        currentDirection = random.nextInt(3);
                    }
                    movementTimer = 0;
                }
                break;

            case SINKER:
                // Accelerate downward movement when consistently moving down
                if (movementTimer >= 0.5f) {
                    if (random.nextFloat() > 0.7f) {
                        currentDirection = random.nextInt(3);
                    }
                    movementTimer = 0;
                }
                // Apply downward acceleration
                if (currentDirection == 1) { // Down
                    velocityY = -baseMovementRange * (1 + consecutiveDirectionTime * 0.5f);
                }
                break;

            case FLOATER:
                // Accelerate upward movement when consistently moving up
                if (movementTimer >= 0.5f) {
                    if (random.nextFloat() > 0.7f) {
                        currentDirection = random.nextInt(3);
                    }
                    movementTimer = 0;
                }
                // Apply upward acceleration
                if (currentDirection == 0) { // Up
                    velocityY = baseMovementRange * (1 + consecutiveDirectionTime * 0.5f);
                }
                break;

            case DART:
                // More frequent direction changes with larger movement range
                if (movementTimer >= 0.3f) {
                    currentDirection = random.nextInt(3);
                    movementTimer = 0;
                }
                break;
        }

        // Set velocity based on direction

        if (behavior != FishBehavior.SINKER && behavior != FishBehavior.FLOATER) {
            switch (currentDirection) {
                case 0: velocityY = baseMovementRange; break; // Up
                case 1: velocityY = -baseMovementRange; break; // Down
                case 2: velocityY = 0; break; // Stay
            }
        }

        // Apply larger movement range for DART behavior
        if (behavior == FishBehavior.DART) {
            velocityY *= 1.8f; // 9/5 ≈ 1.8
        }

        // Update position
        y += velocityY * delta;

        // Reset consecutive time if direction changes
        if (consecutiveDirectionTime > 0 &&
            random.nextInt(10) == 0 &&
            currentDirection != random.nextInt(3)) {
            consecutiveDirectionTime = 0;
        }

        // Keep fish within track bounds
        if (y < 0) y = 0;
        if (y > MiniGameState.TRACK_HEIGHT - MiniGameState.FISH_SIZE) {
            y = MiniGameState.TRACK_HEIGHT - MiniGameState.FISH_SIZE;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, MiniGameState.FISH_SIZE, MiniGameState.FISH_SIZE);
    }

    public FishBehavior getBehavior() {
        return behavior;
    }
}
