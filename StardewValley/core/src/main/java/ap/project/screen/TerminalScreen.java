package ap.project.screen;

import ap.project.model.App.CommandInput;
import ap.project.view.AppView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Static-mode terminal that emulates a real console.
 */
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

    public static void println(String s)
    {
        buffer.add(s);
        scroll = 0;
    }

    public static void run()
    {
        awaitingInput = true;
        lineSubmitted = false;
        currentLine.setLength(0);
        Gdx.input.setInputProcessor(INSTANCE);
    }

    public static boolean isSubmitted() { return lineSubmitted; }
    public static void reset() { lineSubmitted = false; }

    private static final TerminalScreen INSTANCE = new TerminalScreen();

    public TerminalScreen()
    {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32; // Adjust size as needed
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void render(float delta)
    {
        viewport.apply();
        Gdx.gl.glClearColor(0x17 / 255f, 0x14 / 255f, 0x21 / 255f, 1f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        int totalLines = buffer.size + (awaitingInput ? 1 : 0);
        int visible = visibleLines();

        // Dynamic offset: if content fits screen, draw from top
        // else, scroll like a terminal
        int firstLine = Math.max(0, totalLines - visible - scroll);

        float lineHeight = font.getLineHeight() * 1.5f;
        float topY = Gdx.graphics.getHeight() - 20;

        float y = topY;

        for (int i = firstLine; i < buffer.size && y >= 0; i++)
        {
            String line = buffer.get(i);

            // Draw prompt in green
            font.setColor(0x63 / 255f, 0xf0 / 255f, 0x64 / 255f, 1); // green
            font.draw(batch, prompt, 20, y);

            layout.setText(font, prompt);
            float promptWidth = layout.width;

            // Draw user input in white
            font.setColor(1, 1, 1, 1);
            font.draw(batch, line, 20 + promptWidth, y);

            y -= lineHeight;
        }

        // Draw the current prompt + input at bottom if in input mode
        if (awaitingInput && y >= 0)
        {
            font.setColor(0x63 / 255f, 0xf0 / 255f, 0x64 / 255f, 1); // green
            font.draw(batch, prompt, 20, y);

            GlyphLayout layout = new GlyphLayout(font, prompt);
            float promptWidth = layout.width;

            font.setColor(1, 1, 1, 1); // white
            font.draw(batch, currentLine, 20 + promptWidth, y);
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
        buffer.add(cmd);

        if (!cmd.trim().isEmpty()) {
            CommandInput.setCommand(cmd);
        }

        currentLine.setLength(0);
        lineSubmitted = true;
        awaitingInput = false;
        AppView.run();
        Gdx.input.setInputProcessor(null);
        scroll = 0;
    }

    @Override
    public boolean scrolled(float amountX, float amountY)
    {
        int maxScroll = Math.max(0, buffer.size + (awaitingInput ? 1 : 0) - visibleLines());
        scroll = Math.max(0, Math.min(scroll + (int) amountY, maxScroll));
        return true;
    }

    @Override
    public boolean keyDown(int keycode)
    {
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
