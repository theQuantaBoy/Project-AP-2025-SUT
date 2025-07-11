package ap.project.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ap.project.Main;

/** Launches the desktop (LWJGL3) application. */

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("StardewValley");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(1800, 960);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
//        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        configuration.setWindowIcon(
            "icons/icon-256.png",
            "icons/icon-128.png",
            "icons/icon-64.png",
            "icons/icon-48.png",
            "icons/icon-32.png",
            "icons/icon-16.png"
        );

        return configuration;
    }
}

//public class Lwjgl3Launcher {
//    public static void main(String[] args) {
//        if (System.getenv("HEADLESS") != null) {
//            System.out.println("Running in headless mode!");
//            new Main().runConsole(); // <-- You define this method below
//        } else {
//            createApplication();
//        }
//    }
//
//    private static Lwjgl3Application createApplication() {
//        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
//    }
//
//    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
//        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
//        config.setTitle("StardewValley");
//        config.useVsync(true);
//        config.setWindowedMode(800, 600);
//        return config;
//    }
//}
