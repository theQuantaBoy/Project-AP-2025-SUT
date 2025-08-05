package ap.project.util;

import ap.project.model.enums.*;
import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.TreeType;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public class GameObjectAssetLoader
{
    private static final AssetManager assetManager = new AssetManager();

    public static void queueAllTextures()
    {
        for (GameObjectType type : GameObjectType.values()) {
            if (!type.getPath().equals(""))
            {
                assetManager.load(type.getPath(), Texture.class);
            }
        }

        for (EffectType type : EffectType.values()) {
            if (!type.getPath().equals(""))
            {
                assetManager.load(type.getPath(), Texture.class);
            }
        }

        for (CropType type : CropType.values()) {
            List<String> paths = type.getStagePaths();
            for (String path : paths)
            {
                assetManager.load(path, Texture.class);
            }

            if (!type.getGiantModePath().equals(""))
            {
                assetManager.load(type.getGiantModePath(), Texture.class);
            }
        }

        for (TreeType type : TreeType.values()) {
            List<String> paths = type.getStagePaths();
            for (String path : paths)
            {
                assetManager.load(path, Texture.class);
            }

            List<String> seasonPaths = type.getSeasonPaths();
            for (String path : seasonPaths)
            {
                assetManager.load(path, Texture.class);
            }

            String withFruitPath = type.getWithProductPath();

            if(!withFruitPath.equals(""))
            {
                assetManager.load(withFruitPath, Texture.class);
            }
        }

        for (GameAnimationType type : GameAnimationType.values())
        {
            for (String path : type.getFramePaths())
            {
                if (!assetManager.isLoaded(path))
                {
                    assetManager.load(path, Texture.class);
                }
            }
        }

        for (BuffType type : BuffType.values())
        {
            assetManager.load(type.getTexturePath(), Texture.class);
        }

        for (CharacterType type : CharacterType.values())
        {
            assetManager.load(type.getAvatarPath(), Texture.class);
        }
    }

    public static void finishLoadingAndAssign()
    {
        assetManager.finishLoading(); // blocks until done

        for (GameObjectType type : GameObjectType.values())
        {
            if (!type.getPath().equals(""))
            {
                Texture texture = assetManager.get(type.getPath(), Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                type.setTexture(texture);
            }
        }

        for (EffectType type : EffectType.values())
        {
            if (!type.getPath().equals(""))
            {
                Texture texture = assetManager.get(type.getPath(), Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                type.setTexture(texture);
            }
        }

        for (CropType type : CropType.values())
        {
            List<String> paths = type.getStagePaths();
            for (String path : paths)
            {
                Texture texture = assetManager.get(path, Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                type.addStage(texture);
            }

            if (type.isCanBecomeGiant())
            {
                Texture texture = assetManager.get(type.getGiantModePath(), Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                type.setGiantModeTexture(texture);
            }
        }

        for (TreeType type : TreeType.values())
        {
            List<String> paths = type.getStagePaths();
            for (String path : paths)
            {
                if(!path.equals(""))
                {
                    Texture texture = assetManager.get(path, Texture.class);
                    texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    type.addStage(texture);
                }
            }

            List<String> seasonPaths = type.getSeasonPaths();
            for (String path : seasonPaths)
            {
                if(!path.equals(""))
                {
                    Texture texture = assetManager.get(path, Texture.class);
                    texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    type.addSeasonTexture(texture);
                }
            }

            String withFruitPath = type.getWithProductPath();

            if(!withFruitPath.equals(""))
            {
                Texture texture = assetManager.get(withFruitPath, Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                type.setWithProductTexture(texture);
            }
        }

        for (GameAnimationType type : GameAnimationType.values())
        {
            TextureRegion[] regions = new TextureRegion[type.getFramePaths().length];
            for (int i = 0; i < type.getFramePaths().length; i++)
            {
                Texture texture = assetManager.get(type.getFramePaths()[i], Texture.class);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                regions[i] = new TextureRegion(texture);
            }
            type.setAnimation(new Animation<>(type.getFrameDuration(), regions));
        }

        for (BuffType type : BuffType.values())
        {
            Texture texture = assetManager.get(type.getTexturePath(), Texture.class);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            type.setTexture(texture);
        }

        for (CharacterType type : CharacterType.values())
        {
            Texture texture = assetManager.get(type.getAvatarPath(), Texture.class);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            type.setAvatarTexture(texture);
        }
    }

    public static AssetManager getAssetManager()
    {
        return assetManager;
    }

    public static void dispose()
    {
        assetManager.dispose();
    }
}
