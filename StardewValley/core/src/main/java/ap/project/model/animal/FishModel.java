package ap.project.model.animal;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class FishModel {
    private float y;
    private final MiniGameState.FishBehavior behavior;
    private float sineWaveCounter = 0;
    private float targetY;
    private float dartTimer = 0;

    public FishModel(MiniGameState.FishBehavior behavior) {
        this.behavior = behavior;
        reset();
    }

    public void reset() {
        y = MiniGameState.TRACK_HEIGHT / 3f;
        targetY = y;
        sineWaveCounter = 0;
        dartTimer = 0;
    }

    public void update(float delta) {
        switch (behavior) {
            case SMOOTH: updateSmooth(delta); break;
            case SINKER: updateSinker(delta); break;
            case FLOATER: updateFloater(delta); break;
            case DART: updateDart(delta); break;
            case MIXED: updateMixed(delta); break;
        }
        y = MathUtils.clamp(y, 0, MiniGameState.TRACK_HEIGHT - MiniGameState.FISH_SIZE);
    }

    private void updateSmooth(float delta) {
        sineWaveCounter += delta * 2.0f;
        float wave = MathUtils.sin(sineWaveCounter);
        y = (MiniGameState.TRACK_HEIGHT * 0.5f) + (wave * (MiniGameState.TRACK_HEIGHT * 0.35f));
    }

    private void updateSinker(float delta) {
        targetY += (MathUtils.random() - 0.55f) * 20f;
        moveTowardsTarget(delta * 50f);
        if (y > MiniGameState.TRACK_HEIGHT * 0.8f) targetY -= 20f;
    }

    private void updateFloater(float delta) {
        targetY += (MathUtils.random() - 0.45f) * 20f;
        moveTowardsTarget(delta * 50f);
        if (y < MiniGameState.TRACK_HEIGHT * 0.2f) targetY += 20f;
    }

    private void updateDart(float delta) {
        dartTimer -= delta;
        if (dartTimer <= 0) {
            targetY = MathUtils.random(0, MiniGameState.TRACK_HEIGHT - MiniGameState.FISH_SIZE);
            dartTimer = MathUtils.random(0.5f, 1.0f);
        }
        moveTowardsTarget(delta * 150f);
    }

    private void updateMixed(float delta) {
        if (MathUtils.randomBoolean(0.7f)) {
            updateSmooth(delta);
        } else {
            updateDart(delta);
        }
    }

    private void moveTowardsTarget(float speed) {
        if (y < targetY) {
            y = Math.min(y + speed, targetY);
        } else if (y > targetY) {
            y = Math.max(y - speed, targetY);
        }
    }

    public Rectangle getBounds() {
        // Fish is centered horizontally
        float x = (MiniGameState.TRACK_WIDTH - MiniGameState.FISH_SIZE) / 3f + 15f;
        return new Rectangle(x, y, MiniGameState.FISH_SIZE, MiniGameState.FISH_SIZE);
    }

    public float getY() {
        return y;
    }

    public MiniGameState.FishBehavior getBehavior() {
        return behavior;
    }
}
