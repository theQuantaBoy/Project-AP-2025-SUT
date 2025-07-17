package ap.project.visual;

import ap.project.model.App.App;
import ap.project.model.enums.DayOfWeek;
import ap.project.model.game.Player;
import ap.project.model.game.PlayerCharacter;
import ap.project.model.game.Time;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class UIRenderer {
    private final Texture clockTexture;
    private final Texture handleUp, handleDown, handleMid;
    private final Texture journalAlert;
    private final Texture statusRain, statusSnow, statusStorm, statusSun, statusWedding;
    private final Texture springBanner, summerBanner, fallBanner, winterBanner;

    private final BitmapFont font;
    private final Time time;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();;
    Player player;

    public UIRenderer(Time time) {
        this.time = time;
        shapeRenderer.setAutoShapeType(true);
        clockTexture = new Texture(Gdx.files.internal("clock/clock.png"));
        handleUp = new Texture(Gdx.files.internal("clock/handle_up.png"));
        handleDown = new Texture(Gdx.files.internal("clock/handle_down.png"));
        handleMid = new Texture(Gdx.files.internal("clock/handle_middle.png"));
        journalAlert = new Texture(Gdx.files.internal("clock/journal_alert.png"));

        statusRain = new Texture(Gdx.files.internal("clock/status/StatusRain.png"));
        statusSnow = new Texture(Gdx.files.internal("clock/status/StatusSnow.png"));
        statusStorm = new Texture(Gdx.files.internal("clock/status/StatusStorm.png"));
        statusSun = new Texture(Gdx.files.internal("clock/status/StatusSun.png"));
        statusWedding = new Texture(Gdx.files.internal("clock/status/StatusWedding.png"));

        springBanner = new Texture(Gdx.files.internal("clock/season/Spring.png"));
        summerBanner = new Texture(Gdx.files.internal("clock/season/Summer.png"));
        fallBanner = new Texture(Gdx.files.internal("clock/season/Fall.png"));
        winterBanner = new Texture(Gdx.files.internal("clock/season/Winter.png"));

        for (Texture t : new Texture[]{clockTexture, handleUp, handleDown, handleMid, journalAlert,
            statusRain, statusSnow, statusStorm, statusSun, statusWedding,
            springBanner, summerBanner, fallBanner, winterBanner}) {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        font = new BitmapFont(Gdx.files.internal("fonts/Stardew_Valley.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.player = App.getCurrentGame().getCurrentPlayer();

    }

    /** Entry point for rendering all UI elements */
    public void renderUI(Batch batch, OrthographicCamera uiCam) {
        int screenW = (int) uiCam.viewportWidth;
        int screenH = (int) uiCam.viewportHeight;

        renderClock(batch, screenW, screenH);
        renderEnergyBar(uiCam, screenW, screenH);
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
        drawStatus(batch, clockX, clockY);
        drawSeasonBanner(batch, clockX, clockY);
        drawClockText(batch, clockX, clockY);
    }

    private void drawClockBackground(Batch batch, int x, int y) {
        batch.draw(clockTexture, x - 20, y);
//        batch.draw(journalAlert, x, y - 55);
    }

    private void drawClockHandles(Batch batch, int x, int y)
    {
        int hour = time.getHour();

        if (hour >= 9 && hour <= 12)
        {
            batch.draw(handleDown, x - 20, y);
        } else if (hour >= 13 && hour <= 18)
        {
            batch.draw(handleMid, x - 22, y + 4);
        } else if (hour >= 19 && hour <= 23)
        {
            batch.draw(handleUp, x - 20, y + 8);
        }
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

    private void renderEnergyBar(OrthographicCamera uiCam, int screenW, int screenH) {
        // Set up for vertical bar at bottom left
        int barWidth = 20; // Width of the bar
        int barHeight = 150; // Height of the bar
        int margin = 20; // Margin from edges
        int barX = screenW - barWidth - margin;
        int barY = margin;

        // Calculate filled portion based on energy
        float filledHeight = barHeight * player.getEnergyPercentage();

        // Begin shape rendering
        shapeRenderer.setProjectionMatrix(uiCam.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw background (empty portion)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.8f); // Dark gray background
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Draw filled portion (energy level)
        if (player.getEnergyPercentage() > 0.7f) {
            shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 0.8f); // Green when high
        } else if (player.getEnergyPercentage() > 0.3f) {
            shapeRenderer.setColor(0.8f, 0.8f, 0.2f, 0.8f); // Yellow when medium
        } else {
            shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 0.8f); // Red when low
        }
        shapeRenderer.rect(barX, barY, barWidth, filledHeight);

        // Draw border
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1); // White border
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw energy text
        Batch batch = new SpriteBatch();
        batch.begin();
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        String energyText = Math.round(player.getEnergy()) + "/" + Math.round(player.getMaxEnergy());
        int textX = barX - 100;
        int textY = barY + barHeight/2 + 5;
        font.draw(batch, energyText, textX, textY);
        batch.end();
    }

    private void drawStatus(Batch batch, int x, int y)
    {
        Texture status;

        switch (time.getCurrentWeather())
        {
            case Sunny ->  status = statusSun;
            case Rain -> status = statusRain;
            case Storm -> status = statusStorm;
            case Snow -> status = statusSnow;
            default -> status = statusWedding;
        }

        batch.draw(status, x + 108, y + 136);
    }

    private void drawSeasonBanner(Batch batch, int x, int y)
    {
        Texture status;

        switch (time.getSeason())
        {
            case Summer -> status = summerBanner;
            case Fall -> status = fallBanner;
            case Winter -> status = winterBanner;
            default -> status = springBanner;
        }

        batch.draw(status, x + 204, y + 136);
    }

    public void renderDarkOverlay(OrthographicCamera uiCam)
    {
        float darknessLevel = 0f;

        if (time.getHour() >= 18)
        {
            darknessLevel = (time.getHour() - 17) * 0.15f;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(uiCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, darknessLevel);
        shapeRenderer.rect(0, 0, uiCam.viewportWidth, uiCam.viewportHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        clockTexture.dispose();
        handleUp.dispose();
        handleDown.dispose();
        handleMid.dispose();
        journalAlert.dispose();

        statusSun.dispose();
        statusRain.dispose();
        statusStorm.dispose();
        statusSnow.dispose();
        statusWedding.dispose();

        springBanner.dispose();
        summerBanner.dispose();
        fallBanner.dispose();
        winterBanner.dispose();

        font.dispose();
        shapeRenderer.dispose();
    }
}
