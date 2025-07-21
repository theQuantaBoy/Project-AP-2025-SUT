package ap.project.model.enums;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum GameAnimationType
{
    RAIN_TYPE_1(new String[]{"affects/rain/rain_0_0.png",
        "affects/rain/rain_0_1.png",
        "affects/rain/rain_0_2.png",
        "affects/rain/rain_0_3.png"}, 0.15f),

    RAIN_TYPE_2(new String[]{"affects/rain/rain_1_0.png",
        "affects/rain/rain_1_1.png",
        "affects/rain/rain_1_2.png",
        "affects/rain/rain_1_3.png"}, 0.15f),

    SNOW(new String[]{"affects/snow/snow_0.png",
        "affects/snow/snow_1.png",
        "affects/snow/snow_2.png",
        "affects/snow/snow_3.png"}, 0.15f),
    ;

    private final String[] framePaths;
    private final float frameDuration;

    GameAnimationType(String[] framePaths, float frameDuration) {
        this.framePaths = framePaths;
        this.frameDuration = frameDuration;
    }

    public Animation<TextureRegion> createAnimation() {
        TextureRegion[] regions = new TextureRegion[framePaths.length];
        for (int i = 0; i < framePaths.length; i++) {
            Texture texture = new Texture(Gdx.files.internal(framePaths[i]));
            regions[i] = new TextureRegion(texture);
        }
        return new Animation<>(frameDuration, regions);
    }
}

