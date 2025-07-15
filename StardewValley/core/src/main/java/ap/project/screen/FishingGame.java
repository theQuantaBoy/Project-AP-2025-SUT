package ap.project.screen;

// FishingGame.java
// This is the main entry point for the libGDX application. It extends the Game class,
// which helps in managing multiple screens.

import ap.project.control.RegisterController;
import com.badlogic.gdx.Game;

public class FishingGame extends Game {
    RegisterController registerController =  new RegisterController();

    // We create public instances of the screens so we can easily access them
    // from any other screen to switch between them. For example, the menu needs
    // to be able to switch to the fishingScreen.
    public FishingMinigameScreen fishingScreen;
    public RegisterScreen menuScreen;

    /**
     * This method is called once when the application is created.
     */
    @Override
    public void create () {
        // Instantiate the screens, passing a reference to this Game instance
        // (using the `this` keyword). This allows each screen to call back to the main
        // game class to trigger a screen change.
        fishingScreen = new FishingMinigameScreen(this);
        menuScreen = new RegisterScreen(registerController);

        // Set the RegisterMenuScreen as the starting screen for the application.
        setScreen(menuScreen);
    }

    // The Game class handles the render loop, so we don't need a render() method here.
    // It will automatically call the render() method of whichever screen is currently active.

    /**
     * The dispose method is called when the application is closing.
     * We should dispose of our screens here to free up memory.
     */
    @Override
    public void dispose() {
        if (fishingScreen != null) fishingScreen.dispose();
        if (menuScreen != null) menuScreen.dispose();
    }
}

