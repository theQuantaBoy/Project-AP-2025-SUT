package ap.project.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;

/**
 * Reusable text box system for displaying messages across different screens.
 */
public class TextBoxSystem
{
    private final Array<TextBox> activeTextBoxes = new Array<>();
    private final BitmapFont font;
    private final Texture background;

    public TextBoxSystem() {
        // Create font
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);

        // Create background texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.7f)); // Semi-transparent black
        pixmap.fill();
        background = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Show a new text box with the specified message.
     * @param text The message to display
     */
    public void showTextBox(String text) {
        showTextBox(text, 3.5f); // Default duration
    }

    /**
     * Show a new text box with the specified message and duration.
     * @param text The message to display
     * @param duration Duration in seconds
     */
    public void showTextBox(String text, float duration) {
        int stackIndex = activeTextBoxes.size;
        activeTextBoxes.add(new TextBox(text, duration, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), stackIndex));
    }

    /**
     * Update all active text boxes.
     * @param delta Time since last frame in seconds
     */
    public void update(float delta) {
        for (int i = activeTextBoxes.size - 1; i >= 0; i--) {
            TextBox tb = activeTextBoxes.get(i);
            tb.update(delta);
            if (tb.isExpired()) {
                activeTextBoxes.removeIndex(i);
                repositionTextBoxes();
            }
        }
    }

    /**
     * Render all active text boxes.
     * @param batch SpriteBatch to render with
     */
    public void render(Batch batch) {
        for (TextBox tb : activeTextBoxes) {
            tb.render(batch, background, font);
        }
    }

    /**
     * Reposition all text boxes after one is removed.
     */
    private void repositionTextBoxes() {
        float screenHeight = Gdx.graphics.getHeight();
        float screenWidth = Gdx.graphics.getWidth();

        for (int i = 0; i < activeTextBoxes.size; i++) {
            activeTextBoxes.get(i).updateStackPosition(i, screenWidth, screenHeight);
        }
    }

    /**
     * Clean up resources when no longer needed.
     */
    public void dispose() {
        font.dispose();
        background.dispose();
    }

    /**
     * Reposition text boxes when the screen is resized.
     * @param width New screen width
     * @param height New screen height
     */
    public void resize(int width, int height) {
        for (int i = 0; i < activeTextBoxes.size; i++) {
            activeTextBoxes.get(i).updateStackPosition(i, width, height);
        }
    }

    /**
     * Inner class representing a single text box.
     */
    private static class TextBox {
        private final String text;
        private final float duration;
        private float timer;
        private float x, y;
        private float width, height;
        private int stackIndex;

        public TextBox(String text, float duration, float screenWidth, float screenHeight, int stackIndex) {
            this.text = text;
            this.duration = duration;
            this.timer = 0;
            this.stackIndex = stackIndex;
            this.width = screenWidth * 0.8f; // 80% of screen width
            this.height = 100; // Fixed height
            updateStackPosition(stackIndex, screenWidth, screenHeight);
        }

        public void updateStackPosition(int stackIndex, float screenWidth, float screenHeight) {
            this.stackIndex = stackIndex;
            this.x = (screenWidth - width) / 2; // Center horizontally
            this.y = 20 + stackIndex * (height + 10); // Stack with 10px padding
        }

        public void update(float delta) {
            timer += delta;
        }

        public boolean isExpired() {
            return timer >= duration;
        }

        public void render(Batch batch, Texture background, BitmapFont font)
        {
            batch.begin();
            batch.draw(background, x, y, width, height);

            // Draw text - centered both horizontally and vertically
            font.draw(batch, text, x + 20, y + height / 2 + font.getCapHeight() / 2);
            batch.end();
        }
    }
}
