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
        "affects/rain/rain_0_3.png"}, 0.1f),

    RAIN_TYPE_2(new String[]{"affects/rain/rain_1_0.png",
        "affects/rain/rain_1_1.png",
        "affects/rain/rain_1_2.png",
        "affects/rain/rain_1_3.png"}, 0.1f),

    SNOW(new String[]{"affects/snow/snow_0.png",
        "affects/snow/snow_1.png",
        "affects/snow/snow_2.png",
        "affects/snow/snow_3.png"}, 0.1f),

    LIGHTNING(new String[]{"affects/lightning/lightning_0.png",
        "affects/lightning/lightning_1.png",
        "affects/lightning/lightning_2.png",
        "affects/lightning/lightning_3.png",
        "affects/lightning/lightning_4.png",
        "affects/lightning/lightning_5.png",
        "affects/lightning/lightning_6.png",
        "affects/lightning/lightning_7.png"}, 0.05f),

    LIGHTNING_CHEAT(new String[]{"affects/lightning/lightning_0.png",
        "affects/lightning/lightning_1.png",
        "affects/lightning/lightning_2.png",
        "affects/lightning/lightning_3.png",
        "affects/lightning/lightning_4.png",
        "affects/lightning/lightning_5.png",
        "affects/lightning/lightning_6.png",
        "affects/lightning/lightning_7.png"}, 0.05f),

    NO_CLOUD_LIGHTNING(new String[]{"affects/no-cloud-lightning/no_cloud_lightning_0.png",
        "affects/no-cloud-lightning/no_cloud_lightning_1.png",
        "affects/no-cloud-lightning/no_cloud_lightning_2.png",
        "affects/no-cloud-lightning/no_cloud_lightning_3.png",
        "affects/no-cloud-lightning/no_cloud_lightning_4.png",
        "affects/no-cloud-lightning/no_cloud_lightning_5.png",
        "affects/no-cloud-lightning/no_cloud_lightning_6.png",
        "affects/no-cloud-lightning/no_cloud_lightning_7.png"}, 0.05f),

    NO_CLOUD_LIGHTNING_CHEAT(new String[]{"affects/no-cloud-lightning/no_cloud_lightning_0.png",
        "affects/no-cloud-lightning/no_cloud_lightning_1.png",
        "affects/no-cloud-lightning/no_cloud_lightning_2.png",
        "affects/no-cloud-lightning/no_cloud_lightning_3.png",
        "affects/no-cloud-lightning/no_cloud_lightning_4.png",
        "affects/no-cloud-lightning/no_cloud_lightning_5.png",
        "affects/no-cloud-lightning/no_cloud_lightning_6.png",
        "affects/no-cloud-lightning/no_cloud_lightning_7.png"}, 0.05f),

    EATING(new String[]{"affects/eating/eating_0.png",
        "affects/eating/eating_1.png",
        "affects/eating/eating_2.png",
        "affects/eating/eating_3.png",
        "affects/eating/eating_4.png"}, 0.08f),

    PLOUGH(new String[]{"affects/plough/plough_0.png",
        "affects/plough/plough_1.png",
        "affects/plough/plough_2.png",
        "affects/plough/plough_3.png"}, 0.08f),

    FERTILIZE(new String[]{"affects/fertilize/fertilize_0.png",
        "affects/fertilize/fertilize_1.png",
        "affects/fertilize/fertilize_2.png",
        "affects/fertilize/fertilize_3.png"}, 0.08f),

    WATER(new String[]{"affects/water/water_0.png",
        "affects/water/water_1.png",
        "affects/water/water_2.png",
        "affects/water/water_3.png"}, 0.08f)
    ;

    private final String[] framePaths;
    private final float frameDuration;

    private Animation<TextureRegion> cachedAnimation;

    GameAnimationType(String[] framePaths, float frameDuration) {
        this.framePaths = framePaths;
        this.frameDuration = frameDuration;
    }

    public String[] getFramePaths()
    {
        return framePaths;
    }

    public float getFrameDuration()
    {
        return frameDuration;
    }

    public void setAnimation(Animation<TextureRegion> cachedAnimation)
    {
        this.cachedAnimation = cachedAnimation;
    }

    public Animation<TextureRegion> getAnimation()
    {
        return cachedAnimation;
    }
}

