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

    public TextBoxSystem() {}

    public void showTextBox(String text)
    {
        int stackIndex = activeTextBoxes.size; // Current size becomes the stack index
        activeTextBoxes.add(new TextBox(text, 3.5f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), stackIndex));
    }

    public void update(float delta)
    {
        for (int i = activeTextBoxes.size - 1; i >= 0; i--)
        {
            TextBox tb = activeTextBoxes.get(i);
            tb.update(delta);
            if (tb.isExpired())
            {
                activeTextBoxes.removeIndex(i);
                // Reposition remaining text boxes to fill the gap
                repositionTextBoxes();
            }
        }
    }

    public void render(Batch batch)
    {
        batch.begin();
        for (TextBox tb : activeTextBoxes)
        {
            tb.render(batch);
        }
        batch.end();
    }

    private void repositionTextBoxes()
    {
        float screenHeight = Gdx.graphics.getHeight();
        float screenWidth = Gdx.graphics.getWidth();

        for (int i = 0; i < activeTextBoxes.size; i++)
        {
            activeTextBoxes.get(i).updateStackPosition(i, screenWidth, screenHeight);
        }
    }

    public void dispose() {}
}
