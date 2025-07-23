package ap.project.screen;

import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.Game;

// This is your main entry point class that manages screens
public final class FishingGame extends Game {

    // Hold an instance of each screen
    public WorldScreen worldScreen;
    public FishingMinigameWindow fishingMinigameWindow;

    @Override
    public void create() {
        // Create the screen instances, passing 'this' (the Game object) to them
        worldScreen = new WorldScreen();
        fishingMinigameWindow = new FishingMinigameWindow(GameAssetsManager.getGameAssetsManager().getSkin());

        // Set the starting screen
        setScreen(worldScreen);
    }
}
