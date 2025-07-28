package ap.project.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

import static ap.project.model.game.Map.TILE_SIZE;

public class TextBox
{
    private final String text;
    private final float duration;
    private float elapsed;

    private final BitmapFont font;
    private final Texture[] tiles = new Texture[9];

    private final float boxWidth;
    private final float boxHeight;
    private final Vector2 position;
    private int stackIndex; // Track position in stack (mutable for repositioning)

    private static final float SCALE = 1f;
    private static final float MARGIN = 16f;
    private static final float STACK_SPACING = 8f; // Space between stacked boxes

    public TextBox(String text, float fontScale, float screenWidth, float screenHeight, int stackIndex)
    {
        this.text = text;
        this.elapsed = 0;
        this.stackIndex = stackIndex;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(16 * fontScale);
        this.font = generator.generateFont(param);
        generator.dispose();

        for (int i = 0; i < 9; i++)
        {
            tiles[i] = new Texture(Gdx.files.internal("textbox/textBox_" + i + ".png"));
        }

        GlyphLayout layout = new GlyphLayout(font, text);
        this.boxWidth = layout.width + TILE_SIZE * 2;
        this.boxHeight = layout.height + TILE_SIZE * 2;

        this.duration = Math.max(2.5f, text.length() * 0.12f);

        // Calculate position based on stack index
        float yOffset = stackIndex * (boxHeight + STACK_SPACING);
        this.position = new Vector2(MARGIN, screenHeight - boxHeight - MARGIN - yOffset);
    }

    // Backward compatibility constructor
    public TextBox(String text, float fontScale, float screenWidth, float screenHeight)
    {
        this(text, fontScale, screenWidth, screenHeight, 0);
    }

    // Method to update stack position
    public void updateStackPosition(int newStackIndex, float screenWidth, float screenHeight)
    {
        this.stackIndex = newStackIndex;
        float yOffset = stackIndex * (boxHeight + STACK_SPACING);
        this.position.set(MARGIN, screenHeight - boxHeight - MARGIN - yOffset);
    }

    public void update(float delta)
    {
        elapsed += delta;
    }

    public boolean isExpired()
    {
        return elapsed >= duration;
    }

    public void render(Batch batch)
    {
        float x = position.x;
        float y = position.y;

        int horTiles = (int) Math.ceil((boxWidth - 2 * TILE_SIZE) / TILE_SIZE);
        int verTiles = (int) Math.ceil((boxHeight - 2 * TILE_SIZE) / TILE_SIZE);

        // Corners
        batch.draw(tiles[0], x, y + boxHeight - TILE_SIZE, TILE_SIZE, TILE_SIZE); // top-left
        batch.draw(tiles[2], x + boxWidth - TILE_SIZE, y + boxHeight - TILE_SIZE, TILE_SIZE, TILE_SIZE); // top-right
        batch.draw(tiles[6], x, y, TILE_SIZE, TILE_SIZE); // bottom-left
        batch.draw(tiles[8], x + boxWidth - TILE_SIZE, y, TILE_SIZE, TILE_SIZE); // bottom-right

        // Top and bottom edges
        for (int i = 0; i < horTiles; i++) {
            float tileX = x + TILE_SIZE + i * TILE_SIZE;
            batch.draw(tiles[1], tileX, y + boxHeight - TILE_SIZE, TILE_SIZE, TILE_SIZE); // top
            batch.draw(tiles[7], tileX, y, TILE_SIZE, TILE_SIZE); // bottom
        }

        // Left and right edges
        for (int i = 0; i < verTiles; i++) {
            float tileY = y + TILE_SIZE + i * TILE_SIZE;
            batch.draw(tiles[3], x, tileY, TILE_SIZE, TILE_SIZE); // left
            batch.draw(tiles[5], x + boxWidth - TILE_SIZE, tileY, TILE_SIZE, TILE_SIZE); // right
        }

        // Center
        for (int row = 0; row < verTiles; row++) {
            for (int col = 0; col < horTiles; col++) {
                float tileX = x + TILE_SIZE + col * TILE_SIZE;
                float tileY = y + TILE_SIZE + row * TILE_SIZE;
                batch.draw(tiles[4], tileX, tileY, TILE_SIZE, TILE_SIZE);
            }
        }

        // Text
        font.draw(batch, text, x + TILE_SIZE, y + (boxHeight / 2f) + font.getCapHeight() / 2f);
    }

    public int getStackIndex() {
        return stackIndex;
    }
}
