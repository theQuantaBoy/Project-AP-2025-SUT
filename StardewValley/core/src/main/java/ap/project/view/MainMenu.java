package ap.project.view;

import ap.project.control.MainMenuController;
import ap.project.model.App.Result;
import ap.project.model.enums.regex_enums.MainCommands;
import ap.project.screen.TerminalScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class MainMenu implements AppMenu
{
    MainMenuController controller = new MainMenuController();

    @Override
    public void check(Scanner scanner)
    {
        String input = scanner.nextLine().trim();
        Matcher matcher;

        if ((matcher = MainCommands.MENU_ENTER.getMatcher(input)) != null)
        {
            String menuName = matcher.group("menuName").trim();
            println(controller.enterMenu(menuName));
        } else if (MainCommands.MENU_EXIT.getMatcher(input) != null)
        {
            println(controller.exit());
        } else if (MainCommands.MENU_BACK.getMatcher(input) != null) {
            println(controller.back());
        } else if (MainCommands.SHOW_CURRENT_MENU.getMatcher(input) != null)
        {
            println(controller.showCurrentMenu());
        } else if (MainCommands.USER_LOGOUT.getMatcher(input) != null)
        {
            println(controller.logout());
        } else if (MainCommands.HELP.getMatcher(input) != null)
        {
            println(controller.help());
        } else
        {
            println("invalid command");
        }
    }

    public static void println(Result result)
    {
        System.out.println(result.toString());
        TerminalScreen.appendOutputLn(result.toString());
    }

    public static void print(Result result)
    {
        System.out.print(result.toString());
        TerminalScreen.appendOutput(result.toString());
    }

    public static void println(String output)
    {
        System.out.println(output);
        TerminalScreen.appendOutputLn(output);
    }

    public static void print(String output)
    {
        System.out.print(output);
        TerminalScreen.appendOutput(output);
    }
}
