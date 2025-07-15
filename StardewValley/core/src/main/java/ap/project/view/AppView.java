package ap.project.view;

import ap.project.Main;
import ap.project.model.App.App;
import ap.project.model.App.CommandInput;
import ap.project.model.App.TerminalEntry;
import ap.project.model.enums.Menu;
import ap.project.screen.TerminalScreen;
import com.badlogic.gdx.Gdx;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppView
{
    public void runInConsole()
    {
        Scanner scanner = new Scanner(System.in);
        do
        {
            App.getCurrentMenu().checkCommand(scanner);
        } while(App.getCurrentMenu() != Menu.ExitMenu);
    }

    public static void run()
    {
        if (App.getCurrentMenu() != Menu.ExitMenu)
        {
            TerminalScreen.waitForOutput();
            App.getCurrentMenu().checkCommand(CommandInput.getScanner());
            TerminalScreen.endCommand();
        } else
        {
            Gdx.app.exit();
        }
    }

    // SINGLE executor for all command jobs
    private static final ExecutorService COMMAND_EXEC =
        Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Command-Thread");
            t.setDaemon(true);
            return t;
        });

//    public static void run() {
//        if (App.getCurrentMenu() == Menu.ExitMenu) {
//            Gdx.app.exit();
//            return;
//        }
//
//        // push the whole command-handling job off the render thread
//        COMMAND_EXEC.execute(() -> {
//            TerminalScreen.waitForOutput();                    // unchanged
//            App.getCurrentMenu().checkCommand(
//                CommandInput.getScanner());               // unchanged
//            TerminalScreen.endCommand();                      // unchanged
//        });
//    }

}
