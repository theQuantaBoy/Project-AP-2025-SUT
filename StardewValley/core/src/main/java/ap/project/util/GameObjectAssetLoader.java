package ap.project.util;

import ap.project.model.enums.EffectType;
import ap.project.model.enums.GameObjectType;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class GameObjectAssetLoader
{
    private static final AssetManager assetManager = new AssetManager();

    public static void queueAllTextures()
    {
        System.out.println("[AssetLoader] Queuing textures...");

        for (GameObjectType type : GameObjectType.values()) {
            if (!type.getPath().equals(""))
            {
                System.out.println(" - Loading: " + type.getPath());
                assetManager.load(type.getPath(), Texture.class);
            }
        }

        for (EffectType type : EffectType.values()) {
            if (!type.getPath().equals(""))
            {
                System.out.println(" - Loading: " + type.getPath());
                assetManager.load(type.getPath(), Texture.class);
            }
        }
    }

    public static void finishLoadingAndAssign()
    {
        System.out.println("[AssetLoader] Waiting for all textures to load...");
        assetManager.finishLoading(); // blocks until done
        System.out.println("[AssetLoader] All textures loaded.");

        for (GameObjectType type : GameObjectType.values())
        {
            if (!type.getPath().equals(""))
            {
                Texture texture = assetManager.get(type.getPath(), Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                type.setTexture(texture);
                System.out.println(" ✔ Assigned texture to " + type.toString());
            }
        }

        for (EffectType type : EffectType.values())
        {
            if (!type.getPath().equals(""))
            {
                Texture texture = assetManager.get(type.getPath(), Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                type.setTexture(texture);
                System.out.println(" ✔ Assigned texture to " + type.toString());
            }
        }
    }

    public static AssetManager getAssetManager()
    {
        return assetManager;
    }

    public static void dispose()
    {
        System.out.println("[AssetLoader] Disposing AssetManager...");
        assetManager.dispose();
    }
}
