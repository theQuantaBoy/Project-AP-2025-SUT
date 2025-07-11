package ap.project.model.App;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GameAssetsManager {
    private static GameAssetsManager gameAssetsManager;
    private Texture registerBackground;
    private Texture logo;
    private BitmapFont regularFont;
    private BitmapFont shadowFont;
    private BitmapFont largeTitleFont;
    private FreeTypeFontGenerator generator;
    private Skin skin;

    public GameAssetsManager() {
        skin = new Skin(Gdx.files.internal("skin/skin/craftacular-ui.json"));
        this.registerBackground = new Texture(Gdx.files.internal("menu/Panorama.png"));
        this.logo = new Texture(Gdx.files.internal("menu/Logo No Background.png"));

        // Initialize font generator
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Stardew-Valley-Regular.ttf"));

        // Create different font styles
        createFonts();

        // Load skin with better error handling

        if (this.skin == null) {
            throw new GdxRuntimeException("Skin failed to load");
        }
        Gdx.app.log("GameAssetsManager", "Skin loaded successfully");

    }



    private void createFonts() {
        // Regular font
        FreeTypeFontParameter regularParam = new FreeTypeFontParameter();
        regularParam.size = 28;
        regularParam.color = Color.GOLD;
        regularFont = generator.generateFont(regularParam);

        // Font with shadow effect
        FreeTypeFontParameter shadowParam = new FreeTypeFontParameter();
        shadowParam.size = 28;
        shadowParam.color = Color.GOLD;
        shadowParam.shadowOffsetX = 2;
        shadowParam.shadowOffsetY = 2;
        shadowParam.shadowColor = new Color(0, 0, 0, 0.75f);
        shadowFont = generator.generateFont(shadowParam);

        // Large title font with outline
        FreeTypeFontParameter titleParam = new FreeTypeFontParameter();
        titleParam.size = 48;
        titleParam.color = new Color(1, 0.8f, 0.1f, 1);
        titleParam.borderWidth = 2;
        titleParam.borderColor = new Color(0.2f, 0.1f, 0, 0.8f);
        titleParam.shadowOffsetX = 3;
        titleParam.shadowOffsetY = 3;
        titleParam.shadowColor = new Color(0, 0, 0, 0.5f);
        largeTitleFont = generator.generateFont(titleParam);
    }

    public static GameAssetsManager getGameAssetsManager() {
        if (gameAssetsManager == null) {
            gameAssetsManager = new GameAssetsManager();
        }
        return gameAssetsManager;
    }

    public Texture getRegisterBackground() {
        return registerBackground;
    }

    public Texture getLogo() {
        return logo;
    }

    public BitmapFont getRegularFont() {
        return regularFont;
    }

    public BitmapFont getShadowFont() {
        return shadowFont;
    }

    public BitmapFont getLargeTitleFont() {
        return largeTitleFont;
    }

    public FreeTypeFontGenerator getGenerator() {
        return generator;
    }

    public Skin getSkin() {
        return skin;
    }

    public void dispose() {
        if (registerBackground != null) registerBackground.dispose();
        if (logo != null) logo.dispose();
        if (regularFont != null) regularFont.dispose();
        if (shadowFont != null) shadowFont.dispose();
        if (largeTitleFont != null) largeTitleFont.dispose();
        if (generator != null) generator.dispose();
        if (skin != null) skin.dispose();
    }
}
