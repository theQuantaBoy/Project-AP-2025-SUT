package ap.project.visual;

import ap.project.model.game.AbstractCharacter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CharacterRenderer
{
    public void render(Batch batch, AbstractCharacter character, float scale) {
        Vector2 pos = character.getPosition();

        batch.draw(character.getShadow(), pos.x, pos.y - 3);

        TextureRegion frame = character.getCurrentFrame();
        batch.draw(frame, pos.x, pos.y, 0, 0,
            frame.getRegionWidth(), frame.getRegionHeight(),
            scale, scale, 0);
    }
}
