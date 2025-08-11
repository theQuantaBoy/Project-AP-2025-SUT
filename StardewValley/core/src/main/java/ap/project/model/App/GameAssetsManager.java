package ap.project.model.App;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.*;

public class GameAssetsManager {
    private static GameAssetsManager gameAssetsManager;
    private Texture registerBackground;
    private Texture logo;
    private BitmapFont regularFont;
    private BitmapFont shadowFont;
    private BitmapFont largeTitleFont;
    private FreeTypeFontGenerator generator;
    private Sprite sprite;
    private Skin skin;
    private Array<AvatarOptions> avatars;
    HashMap<String, Music> musicMap = new HashMap<>();

    public GameAssetsManager() {
        skin = new Skin(Gdx.files.internal("skin/NzSkin.json"));
        this.registerBackground = new Texture(Gdx.files.internal("menu/Panorama.png"));
        this.logo = new Texture(Gdx.files.internal("menu/Logo No Background.png"));
        avatars = new Array<>();
        avatars.add(new AvatarOptions("Alex", new Texture(Gdx.files.internal("avatars/Alex-26.png"))));
        avatars.add(new AvatarOptions("Evelin", new Texture(Gdx.files.internal("avatars/Evelyn-3.png"))));
        avatars.add(new AvatarOptions("Leo", new Texture(Gdx.files.internal("avatars/Leo-19.png"))));
        avatars.add(new AvatarOptions("Lewis", new Texture(Gdx.files.internal("avatars/Lewis-21.png"))));
        avatars.add(new AvatarOptions("Linus", new Texture(Gdx.files.internal("avatars/Linus-26.png"))));
        avatars.add(new AvatarOptions("Pam", new Texture(Gdx.files.internal("avatars/Pam-36.png"))));
        avatars.add(new AvatarOptions("Penny", new Texture(Gdx.files.internal("avatars/Penny-79.png"))));
        avatars.add(new AvatarOptions("Sandy", new Texture(Gdx.files.internal("avatars/Sandy-8.png"))));

        // Initialize font generator
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Stardew-Valley-Regular.ttf"));
        this.sprite = new Sprite(new Texture(Gdx.files.internal("menu/Title Screen.png")));
        // Create different font styles
        createFonts();
        loadAllMusic();

        // Load skin with better error handling

        if (this.skin == null) {
            throw new GdxRuntimeException("Skin failed to load");
        }
        Gdx.app.log("GameAssetsManager", "Skin loaded successfully");
    }

    private void loadAllMusic() {
        FileHandle dir = Gdx.files.internal("music/my_music");
        for (FileHandle file : dir.list()) {
            if (file.extension().equalsIgnoreCase("ogg")) {
                Music music = Gdx.audio.newMusic(file);
                music.setLooping(false);
                musicMap.put(file.nameWithoutExtension(), music);
            }
        }
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

    public Drawable createColoredDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
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

    public Sprite getSprite() {
        return sprite;
    }

    public Array<AvatarOptions> getAvatars() {
        return avatars;
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

    public static BitmapFont generateFont(String path, int size, Color color)
    {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.color = color;
        BitmapFont font = generator.generateFont(param);
        generator.dispose();
        return font;
    }
}
