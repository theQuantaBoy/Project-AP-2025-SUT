package ap.project.screen;

import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.Game;

public final class FishingGame extends Game {
    public WorldScreen worldScreen;
    public FishingMinigameWindow fishingMinigameWindow;

    @Override
    public void create() {
        fishingMinigameWindow = new FishingMinigameWindow(GameAssetsManager.getGameAssetsManager().getSkin());
        worldScreen = new WorldScreen(); // Pass the game instance
        setScreen(worldScreen);
    }
}
