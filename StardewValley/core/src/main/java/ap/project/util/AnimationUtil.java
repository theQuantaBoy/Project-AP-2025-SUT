package ap.project.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationUtil {
    public static Animation<TextureRegion> createAnimationFromRow(Texture sheet, int rowIndex, int colCount, int rowCount, float frameDuration) {
        int frameWidth = sheet.getWidth() / colCount;
        int frameHeight = sheet.getHeight() / rowCount;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < colCount; i++) {
            frames.add(new TextureRegion(sheet, i * frameWidth, rowIndex * frameHeight, frameWidth, frameHeight));
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
}
