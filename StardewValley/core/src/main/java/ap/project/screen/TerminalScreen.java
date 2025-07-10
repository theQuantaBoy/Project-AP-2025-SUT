package ap.project.screen;

import ap.project.model.App.CommandInput;
import ap.project.model.App.TerminalEntry;
import ap.project.view.AppView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public final class TerminalScreen implements Screen, InputProcessor
{
    private static final Array<String> buffer = new Array<>();          // scroll-back
    private static final StringBuilder currentLine = new StringBuilder();

    private static BitmapFont font;

    private static final SpriteBatch batch = new SpriteBatch();
    private static final OrthographicCamera camera = new OrthographicCamera();
    private static final Viewport viewport = new ScreenViewport(camera);

    private static boolean awaitingInput = false;
    private static boolean lineSubmitted = false;

    public static String prompt = "user@root:~$ ";

    private static int scroll = 0;          // +ve scrolls up

    private static final GlyphLayout layout = new GlyphLayout();

    private static final ArrayList<TerminalEntry> history = new ArrayList<>();
    private static TerminalEntry currentEntry = null;
    private static boolean awaitingOutput = false;

    private static final String CURSOR_CHAR = "_";
    private static final float CURSOR_BLINK_INTERVAL = 0.5f; // seconds
    private float cursorTimer = 0f;
    private boolean cursorVisible = true;

    public static void run()
    {
        awaitingInput = true;
        lineSubmitted = false;
        currentLine.setLength(0);
        currentEntry = null;
        Gdx.input.setInputProcessor(INSTANCE);
    }

    public static boolean isSubmitted() { return lineSubmitted; }
    public static void reset() { lineSubmitted = false; }

    private static final TerminalScreen INSTANCE = new TerminalScreen();

    public TerminalScreen()
    {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28; // Adjust size as needed
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void render(float delta)
    {
        // Clear screen manually to keep black background
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Make sure camera & viewport are in sync
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        cursorTimer += delta;
        if (cursorTimer >= CURSOR_BLINK_INTERVAL)
        {
            cursorTimer = 0;
            cursorVisible = !cursorVisible;
        }

        // Dynamically scale font based on window size (optional)
        float baseHeight = 720f; // or any height you designed your font for
        float scale = Gdx.graphics.getHeight() / baseHeight;

        viewport.apply();
        batch.begin();

        Gdx.gl.glClearColor(0x17 / 255f, 0x14 / 255f, 0x21 / 255f, 1f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        /* ---- how many total lines do we need to show? ---- */
        int totalLines = totalLineCount();

        int visible = visibleLines();

        /* scroll offset; 0 = stick to bottom */
        int firstLine = Math.max(0, totalLines - visible - scroll);

        float lineHeight = font.getLineHeight() * 1.5f;
        float topY = Gdx.graphics.getHeight() - 20;

        float y = Gdx.graphics.getHeight() - 20;
        int skipped = 0;                     // how many lines we’ve skipped (scroll)

        for (TerminalEntry e : history) {
            int need = linesOf(e);
            if (skipped + need <= firstLine) { skipped += need; continue; }

            /* prompt (green) + input (white) */
            if (skipped >= firstLine) {
                font.setColor(0x63/255f, 0xf0/255f, 0x64/255f, 1); // green
                font.draw(batch, e.prompt, 20, y);
                layout.setText(font, e.prompt);
                font.setColor(1,1,1,1);
                font.draw(batch, e.input, 20 + layout.width, y);
                y -= lineHeight;
            } else skipped++;        // we skipped the prompt line
            /* output lines (gray) */
            for (String out : e.getOutput().split("\n")) {
                if (skipped < firstLine) { skipped++; continue; }
                font.setColor(.7f,.7f,.7f,1);
                font.draw(batch, out, 20, y);
                y -= lineHeight;
            }
        }

        /* live prompt + typing */
        if (awaitingInput && y >= 0 && currentEntry == null)
        {
            font.setColor(0x63/255f, 0xf0/255f, 0x64/255f, 1);
            font.draw(batch, prompt, 20, y);
            layout.setText(font, prompt);
            font.setColor(1,1,1,1);
            font.draw(batch, currentLine, 20 + layout.width, y);

            if (cursorVisible) {
                layout.setText(font, currentLine);

                layout.setText(font, prompt);
                float promptWidth = layout.width;

                layout.setText(font, currentLine);
                float inputWidth = layout.width;

                font.draw(batch, CURSOR_CHAR, 20 + promptWidth + inputWidth, y);
            }
        }

        batch.end();
    }

    private int visibleLines()
    {
        return (int)((Gdx.graphics.getHeight() - 40) / (font.getLineHeight() * 1.5f));
    }

    @Override
    public boolean keyTyped(char character) {
        if (!awaitingInput) return false;

        switch (character) {
            case '\b':
                if (currentLine.length() > 0) currentLine.deleteCharAt(currentLine.length() - 1);
                break;
            case '\r':
                break;
            case '\n':
                submit();
                break;
            default:
                if (!Character.isISOControl(character)) {
                    currentLine.append(character);
                }
        }
        return true;
    }

    private void submit()
    {
        String cmd = currentLine.toString();

        // Always add user input to buffer (we'll prefix with prompt during rendering)
        currentEntry = new TerminalEntry(prompt, cmd);
        waitForOutput(); // start capturing output

        if (!cmd.trim().isEmpty())
        {
            CommandInput.setCommand(cmd); // run command logic
        }

        currentLine.setLength(0);
        lineSubmitted = true;
        awaitingInput = false;
        AppView.run();
        Gdx.input.setInputProcessor(null);
        scroll = 0;
    }

    public static void appendOutput(String text)
    {
        if (currentEntry != null && awaitingOutput)
        {
            currentEntry.appendOutput(text);
        }
    }

    public static void appendOutputLn(String text)
    {
        if (currentEntry != null && awaitingOutput)
        {
            currentEntry.appendOutputLn(text);
        }
    }

    public static void waitForOutput()
    {
        awaitingOutput = true;
    }

    public static void endCommand()
    {
        if (currentEntry != null)
        {
            history.add(currentEntry);
            currentEntry = null;
        }

        awaitingOutput = false;
        awaitingInput = true;
        Gdx.input.setInputProcessor(INSTANCE);
    }

    private static int linesOf(TerminalEntry e)
    {
        int lines = 1;
        if (!e.getOutput().isEmpty())
        {
            lines += e.getOutput().split("\n").length;
        }
        return lines;
    }

    private static int totalLineCount()
    {
        int total = 0;
        for (TerminalEntry e : history) total += linesOf(e);
        if (awaitingInput) total++;
        return total;
    }

    @Override public boolean scrolled(float amountX,float amountY)
    {
        int max = Math.max(0, totalLineCount() - visibleLines());  // ✅ use method
        scroll = Math.max(0, Math.min(scroll - (int)amountY, max));
        return true;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Input.Keys.V && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
        {
            String clip = Gdx.app.getClipboard().getContents();
            if (clip != null && !clip.isEmpty()) {
                currentLine.append(clip.replace("\r", "").replace("\n", " ")); // flatten multiline
            }
            return true;
        }

        switch (keycode)
        {
            case Input.Keys.PAGE_UP:
                scroll = Math.min(scroll + visibleLines(), buffer.size - visibleLines());
                break;
            case Input.Keys.PAGE_DOWN:
                scroll = Math.max(scroll - visibleLines(), 0);
                break;
        }
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show()
    {
        currentLine.setLength(0);
        Gdx.input.setInputProcessor(this);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        font.dispose();
        batch.dispose();
    }

    public static TerminalScreen getInstance() {
        return INSTANCE;
    }
}
