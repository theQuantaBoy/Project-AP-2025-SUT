package ap.project.model.enums;

import com.badlogic.gdx.graphics.Texture;

public enum ReactionEmoji
{
    ALIEN("emoji/alien.png"),
    ANGRY_1("emoji/angry_1.png"),
    ANGRY_2("emoji/angry_2.png"),
    ANGRY_3("emoji/angry_3.png"),
    ANGRY_4("emoji/angry_4.png"),
    ANXIOUS("emoji/anxious.png"),
    BAD_GHOST("emoji/bad_ghost.png"),
    CAT("emoji/cat.png"),
    CAT_EYE("emoji/cat_eye.png"),
    CRY("emoji/cry.png"),
    CUTE_GHOST("emoji/cute_ghost.png"),
    DEVELOPER("emoji/developer.png"),
    DEVIL("emoji/devil.png"),
    DISLIKE("emoji/dislike.png"),
    DONT_CARE("emoji/dont_care.png"),
    GOLD("emoji/gold.png"),
    HEART_BREAKING("emoji/heart_breaking.png"),
    HEART_BROKEN("emoji/heart_broken.png"),
    HEART_FACE("emoji/heart_face.png"),
    HEART_GOOD("emoji/heart_good.png"),
    IDK_1("emoji/idk_1.png"),
    IDK_2("emoji/idk_2.png"),
    IDK_3("emoji/idk_3.png"),
    IDK_4("emoji/idk_4.png"),
    JOYFUL("emoji/joyful.png"),
    LIGHTNING("emoji/lightning.png"),
    LIKE("emoji/like.png"),
    MUSIC("emoji/music.png"),
    POINT_DOWN("emoji/point_down.png"),
    POINT_LEFT("emoji/point_left.png"),
    POINT_RIGHT("emoji/point_right.png"),
    POINT_UP("emoji/point_up.png"),
    SAD("emoji/sad.png"),
    SHOCKED("emoji/shocked.png"),
    SHOOOCKED("emoji/shoocked.png"),
    SHY("emoji/shy.png"),
    SKULL("emoji/skull.png"),
    SMILE_0("emoji/smile_0.png"),
    SMILE_1("emoji/smile_1.png"),
    SMILE_2("emoji/smile_2.png"),
    SMILE_3("emoji/smile_3.png"),
    SMILE_4("emoji/smile_4.png"),
    SMILE_5("emoji/smile_5.png"),
    SPICY("emoji/spicy.png"),
    STAR("emoji/star.png"),
    SUNGLASSES("emoji/sunglasses.png"),
    THIEF("emoji/thief.png"),
    VROOM_VROOM("emoji/vroom_vroom.png"),
    WHO_ME("emoji/who_me.png"),
    ZOMBIE("emoji/zombie.png");

    private final String texturePath;
    private Texture texture;

    ReactionEmoji(String path)
    {
        this.texturePath = path;
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    public Texture getTexture()
    {
        return texture;
    }

    public String getTexturePath()
    {
        return texturePath;
    }
}
