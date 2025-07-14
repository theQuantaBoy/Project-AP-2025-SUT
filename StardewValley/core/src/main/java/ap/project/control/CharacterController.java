package ap.project.control;

import ap.project.model.enums.TileTexture;
import ap.project.model.game.AbstractCharacter;
import ap.project.model.game.Farm;
import ap.project.model.game.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CharacterController
{

    private final AbstractCharacter character;
    private final Farm farm;
    private final float speed;
    private final float tileSize;

    public CharacterController(AbstractCharacter character, Farm farm, float speed, float tileSize) {
        this.character = character;
        this.farm = farm;
        this.speed = speed;
        this.tileSize = tileSize;
    }

    public void update(float delta) {
        float dx = (Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0)
            - (Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0);
        float dy = (Gdx.input.isKeyPressed(Input.Keys.W) ? 1 : 0)
            - (Gdx.input.isKeyPressed(Input.Keys.S) ? 1 : 0);

        Vector2 pos = character.getPosition();

        if (dx != 0 || dy != 0) {
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            dx = dx / len * speed * delta;
            dy = dy / len * speed * delta;

            float nextX = pos.x + dx;
            float nextY = pos.y + dy;

            int x = (int)(nextX / 16);
            int y = (int)((1912 - nextY) / 16);

            Tile target = farm.getTile(x, y);

            if (target == null) return;
            TileTexture texture = target.getTexture();

            if (texture == TileTexture.LAKE || texture == TileTexture.UNPASSABLE
                || texture == TileTexture.BUILDING || texture == TileTexture.OBJECT) {
                return; // blocked
            }

            pos.set(nextX, nextY);
            character.updateAnimation(delta);

            // set animation
            if (Math.abs(dx) > 0) {
                character.setDirection(dx > 0 ? AbstractCharacter.Direction.RIGHT : AbstractCharacter.Direction.LEFT);
            } else
            {
                character.setDirection(dy > 0 ? AbstractCharacter.Direction.UP : AbstractCharacter.Direction.DOWN);
            }

        } else {
            character.resetAnimation();
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
}
