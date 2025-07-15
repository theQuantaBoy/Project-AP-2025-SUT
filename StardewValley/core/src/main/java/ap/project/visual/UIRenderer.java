package ap.project.visual;

import ap.project.model.enums.DayOfWeek;
import ap.project.model.game.Time;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class UIRenderer {
    private final Texture clockTexture;
    private final Texture handleUp, handleDown, handleMid;
    private final Texture journalAlert;

    private final BitmapFont font;
    private final Time time;

    public UIRenderer(Time time) {
        this.time = time;

        clockTexture = new Texture(Gdx.files.internal("clock/clock.png"));
        handleUp = new Texture(Gdx.files.internal("clock/handle_up.png"));
        handleDown = new Texture(Gdx.files.internal("clock/handle_down.png"));
        handleMid = new Texture(Gdx.files.internal("clock/handle_middle.png"));
        journalAlert = new Texture(Gdx.files.internal("clock/journal_alert.png"));

        for (Texture t : new Texture[]{clockTexture, handleUp, handleDown, handleMid, journalAlert}) {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        font = new BitmapFont(Gdx.files.internal("fonts/Stardew_Valley.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    /** Entry point for rendering all UI elements */
    public void renderUI(Batch batch, OrthographicCamera uiCam) {
        int screenW = (int) uiCam.viewportWidth;
        int screenH = (int) uiCam.viewportHeight;

        renderClock(batch, screenW, screenH);
        // In the future:
        // renderEnergyBar(batch, screenW, screenH);
        // renderInventoryBar(batch, screenW, screenH);
    }

    /** Draw the entire clock UI section */
    private void renderClock(Batch batch, int screenW, int screenH) {
        int clockX = screenW - clockTexture.getWidth() - 10;
        int clockY = screenH - clockTexture.getHeight() - 10;

        drawClockBackground(batch, clockX, clockY);
        drawClockHandles(batch, clockX, clockY);
        drawClockText(batch, clockX, clockY);
    }

    private void drawClockBackground(Batch batch, int x, int y) {
        batch.draw(clockTexture, x - 20, y);
        batch.draw(journalAlert, x, y - 55);
    }

    private void drawClockHandles(Batch batch, int x, int y) {
        batch.draw(handleDown, x - 20, y);
        batch.draw(handleMid, x - 22, y + 4);
        batch.draw(handleUp, x - 20, y + 8);
    }

    private void drawClockText(Batch batch, int x, int y) {
        font.getData().setScale(1.5f);

        String dayText = DayOfWeek.getShortDayOfWeek((time.getDay() - 1) % 7) + " " + time.getDay();
        String timeText = time.getHour() + ":00" + ((time.getHour() >= 12) ? " pm" : " am");
        String moneyText = "500000"; // Placeholder — can become dynamic

        font.setColor(Color.BLACK);
        font.draw(batch, dayText, x + 130, y + 210);
        font.draw(batch, timeText, x + 115, y + 120);

        font.setColor(Color.RED);
        font.draw(batch, moneyText, x + 64, y + 38);
    }

    public void dispose() {
        clockTexture.dispose();
        handleUp.dispose();
        handleDown.dispose();
        handleMid.dispose();
        journalAlert.dispose();
        font.dispose();
    }
}
