package ap.project.visual;
//
import ap.project.model.game.AbstractCharacter;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.model.game.PlayerCharacter;
import ap.project.model.tools.Tool;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import static ap.project.model.game.Map.TILE_SIZE;

public class CharacterRenderer
{
    private final BitmapFont nameBadgeFont;
    private final ShapeRenderer shapeRenderer;
    private final FreeTypeFontGenerator generator;

    public CharacterRenderer(ShapeRenderer shapeRenderer)
    {
        this.shapeRenderer = shapeRenderer;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        parameter.minFilter = Texture.TextureFilter.Nearest;

        nameBadgeFont = generator.generateFont(parameter);
    }

    public void render(Batch batch, AbstractCharacter character, float scale)
    {
        Vector2 pos = character.getPosition();

        batch.draw(character.getShadow(), pos.x, pos.y - 3);

        TextureRegion frame = character.getCurrentFrame();
        batch.draw(frame, pos.x, pos.y, 0, 0,
            frame.getRegionWidth(), frame.getRegionHeight(),
            scale, scale, 0);

        boolean lowEnergy = false;

        if (character instanceof PlayerCharacter)
        {
            PlayerCharacter playerCharacter = (PlayerCharacter) character;
            Player player = playerCharacter.getPlayer();

            if (player.getEnergyPercentage() <= 0.3f)
            {
                batch.draw(playerCharacter.getLowEnergy(), pos.x, pos.y + 26);
                lowEnergy = true;
            }
        }

        String name = character.getNickName();
        if (name == null || name.isEmpty()) return;

        float fontScale = 1f;
        nameBadgeFont.getData().setScale(fontScale);

        float charWidth = 8f * fontScale;
        float textWidth = name.length() * charWidth;
        float textHeight = nameBadgeFont.getCapHeight() * fontScale;

        float maxWidth = 12;
        float maxHeight = 36;

        float padding = 2f;

        float badgeWidth = textWidth +  2 * padding;
        float badgeHeight = textHeight + 2 * padding;

        float badgeX = pos.x + ((maxWidth - badgeWidth) / 2);
        float badgeY = pos.y + maxHeight + (lowEnergy ? 5f : 0f);

        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        shapeRenderer.rect(badgeX, badgeY, badgeWidth, badgeHeight);
        shapeRenderer.end();
        batch.begin();

        float textX = badgeX + ((badgeWidth - textWidth) / 2) + padding;
        float textY = badgeY + textHeight + padding;

        nameBadgeFont.draw(batch, name, textX, textY);
    }

    public void dispose()
    {
        nameBadgeFont.dispose();
        generator.dispose();
    }

    public void renderToolOrObjectAtMouse(Batch batch, PlayerCharacter playerCharacter, float mouseX, float mouseY)
    {
        Player player = playerCharacter.getPlayer();
        Texture texture = null;

        // Check for tool first, then object
        if (player.getCurrentTool() != null)
        {
            texture = player.getCurrentTool().getObjectType().getTexture();
        } else if (player.getCurrentObject() != null)
        {
            texture = player.getCurrentObject().getObjectType().getTexture();
        }

        if (texture != null)
        {
            float width = TILE_SIZE;
            float height = (texture.getHeight() * width) / texture.getWidth();

            // Draw at the mouse position
            batch.draw(texture, mouseX, mouseY + 5, width, height);
        }
    }
}
