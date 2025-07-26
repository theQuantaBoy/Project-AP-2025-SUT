package ap.project.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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
    private final Array<String> lines = new Array<>(); // Store wrapped lines

    private static final float SCALE = 1f;
    private static final float MARGIN = 16f;
    private static final float MAX_WIDTH = 500f; // Max width for text wrapping

    public TextBox(String text, float fontScale, float screenWidth, float screenHeight)
    {
        this.text = text;
        this.elapsed = 0;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(16 * fontScale);
        this.font = generator.generateFont(param);
        generator.dispose();

        for (int i = 0; i < 9; i++)
        {
            tiles[i] = new Texture(Gdx.files.internal("textbox/textBox_" + i + ".png"));
        }

        // Wrap text into multiple lines
        wrapText();

        // Calculate box dimensions based on wrapped lines
        float maxLineWidth = 0;
        for (String line : lines) {
            GlyphLayout layout = new GlyphLayout(font, line);
            if (layout.width > maxLineWidth) maxLineWidth = layout.width;
        }

        float lineHeight = font.getLineHeight();
        this.boxWidth = Math.min(maxLineWidth + TILE_SIZE * 2, MAX_WIDTH + TILE_SIZE * 2);
        this.boxHeight = lines.size * lineHeight + TILE_SIZE * 2;

        this.duration = Math.max(2.5f, text.length() * 0.12f);
        this.position = new Vector2(MARGIN, screenHeight - boxHeight - MARGIN);
    }

    private void wrapText() {
        GlyphLayout layout = new GlyphLayout();
        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split("\\s+")) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            layout.setText(font, testLine);

            if (layout.width > MAX_WIDTH) {
                if (currentLine.length() == 0) {
                    // Single word is too long, split it
                    lines.add(word);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
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

        // Render each line of text
        float lineY = y + boxHeight - TILE_SIZE - 10; // Start from top with padding
        for (String line : lines) {
            font.draw(batch, line, x + TILE_SIZE, lineY);
            lineY -= font.getLineHeight(); // Move down for next line
        }
    }
}

