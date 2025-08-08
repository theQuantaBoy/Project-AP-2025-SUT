package ap.project.screen;

import ap.project.screen.FishingMinigameScreen;
import ap.project.screen.WorldScreen;
import com.badlogic.gdx.Game;

// This is your main entry point class that manages screens
public final class FishingGame extends Game {

    // Hold an instance of each screen
    public WorldScreen worldScreen;
    public FishingMinigameScreen fishingMinigameScreen;

    @Override
    public void create() {
        // Create the screen instances, passing 'this' (the Game object) to them
//        worldScreen = new WorldScreen();
        fishingMinigameScreen = new FishingMinigameScreen(this);

        // Set the starting screen
        setScreen(worldScreen);
    }
}
