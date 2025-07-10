package ap.project.screen;

import ap.project.model.App.CommandInput;
import ap.project.view.AppView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Static-mode terminal that emulates a real console.
 * <p>
 * Call {@link #run()} to capture exactly one line of input.  After the user
 * presses <code>ENTER</code>, the line is stored via {@link CommandInput} and
 * the screen releases control.  Query {@link #isSubmitted()} from your game
 * loop; once it returns <code>true</code>, read the scanner provided by
 * {@code CommandInput} and then invoke {@link #reset()} before calling
 * {@link #run()} again.
 */
public final class TerminalScreen implements Screen, InputProcessor
{
    private static final Array<String> buffer = new Array<>();          // scroll-back
    private static final StringBuilder currentLine = new StringBuilder();

    private static final BitmapFont font = new BitmapFont();
    private static final SpriteBatch batch = new SpriteBatch();

    private static boolean awaitingInput = false;
    private static boolean lineSubmitted = false;

    /** Header printed before each command – customise if you like. */
    public static String prompt = "user@root$ ";

    /** Programmatically print to the terminal buffer. */
    public static void println(String s)
    {
        buffer.add(s);
    }

    /** Begin accepting exactly one line of user input. */
    public static void run()
    {
        awaitingInput = true;
        lineSubmitted = false;
        currentLine.setLength(0);
        Gdx.input.setInputProcessor(INSTANCE);
    }

    /** Has the user pressed ENTER since the last {@link #run()}? */
    public static boolean isSubmitted() { return lineSubmitted; }

    /** Call after you have consumed the submitted line. */
    public static void reset() { lineSubmitted = false; }

    /* ------------------------------------------------------------ */
    /*  Singleton plumbing                                          */
    /* ------------------------------------------------------------ */

    private static final TerminalScreen INSTANCE = new TerminalScreen();
    public TerminalScreen() { /* singleton */ }

    /* ------------------------------------------------------------ */
    /*  Rendering                                                   */
    /* ------------------------------------------------------------ */

    @Override public void render(float delta)
    {
        batch.begin();

        float y = Gdx.graphics.getHeight() - 10;
        for (int i = buffer.size - 1; i >= 0; i--)
        {
            font.draw(batch, buffer.get(i), 10, y);
            y -= font.getLineHeight();
        }

        if (awaitingInput)
        {
            font.draw(batch, prompt + currentLine, 10, y);
        }

        batch.end();
    }

    /* ------------------------------------------------------------ */
    /*  InputProcessor impl                                         */
    /* ------------------------------------------------------------ */

    @Override
    public boolean keyTyped(char character)
    {
        if (!awaitingInput) return false;

        switch (character)
        {
            case '\b': // backspace
                if (currentLine.length() > 0) currentLine.deleteCharAt(currentLine.length() - 1);
                break;
            case '\r': // ignore CR
                break;
            case '\n': // ENTER
                submit();
                break;
            default:
                currentLine.append(character);
        }
        return true;
    }

    private void submit()
    {
        String cmd = currentLine.toString().trim();
        if (!cmd.isEmpty())
        {
            buffer.add(prompt + cmd);
            CommandInput.setCommand(cmd);
        }
        lineSubmitted = true;
        awaitingInput = false;
        AppView.run();
        Gdx.input.setInputProcessor(null);
    }

    /* Unused InputProcessor methods */
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    /* ------------------------------------------------------------ */
    /*  Screen boilerplate                                          */
    /* ------------------------------------------------------------ */


    @Override
    public void show() {
        currentLine.setLength(0);                   // clear input buffer
        Gdx.input.setInputProcessor(this);          // take input focus
        println("Welcome to the graphical terminal.");
        println(prompt + currentLine.toString());   // show initial prompt line
    }

    @Override public void resize(int width, int height) { /* ignore */ }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() {
        font.dispose();
        batch.dispose();
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    public static TerminalScreen getInstance() {
        return INSTANCE;
    }
}
