package ap.project.view;

import ap.project.Main;
import ap.project.model.App.App;
import ap.project.model.App.CommandInput;
import ap.project.model.enums.Menu;

import java.util.Scanner;

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
            App.getCurrentMenu().checkCommand(CommandInput.getScanner());
        } else
        {
            System.exit(0);
        }
    }
}
