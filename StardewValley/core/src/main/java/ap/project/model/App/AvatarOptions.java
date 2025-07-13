package ap.project.model.App;

import com.badlogic.gdx.graphics.Texture;

public class AvatarOptions {
    public String name;
    public  Texture texture;

    public AvatarOptions(String name, Texture texture) {
        this.name = name; this.texture = texture;
    }

    @Override public String toString() {
        return name;
    }
}
