package ap.project.view;

import ap.project.control.PreGameController;
import ap.project.model.App.Result;
import ap.project.model.enums.regex_enums.PreGameCommands;
import ap.project.screen.TerminalScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class PreGameMenu implements AppMenu
{
    PreGameController controller = new PreGameController();

    @Override
    public void check(Scanner scanner)
    {
        String input = scanner.nextLine().trim();
        Matcher matcher;

        if ((matcher = PreGameCommands.NEW_GAME.getMatcher(input)) != null)
        {
            String[] usernames = new String[3];
            usernames[0] = matcher.group("user1") == null ? "" : matcher.group("user1").trim();
            usernames[1] = matcher.group("user2") == null ? "" : matcher.group("user2").trim();
            usernames[2] = matcher.group("user3") == null ? "" : matcher.group("user3").trim();

            controller.newGame(usernames);
        } else if ((matcher = PreGameCommands.LOAD_GAME.getMatcher(input)) != null)
        {
            println(controller.loadGame());
        } else if ((matcher = PreGameCommands.MENU_ENTER.getMatcher(input)) != null)
        {
            String menuName = scanner.nextLine().trim();
            println(controller.enterMenu(menuName));
        }else if ((matcher = PreGameCommands.MENU_EXIT.getMatcher(input)) != null)
        {
            println(controller.exit());
        }else if ((matcher = PreGameCommands.MENU_BACK.getMatcher(input)) != null)
        {
            println(controller.back());
        }else if ((matcher = PreGameCommands.HELP.getMatcher(input)) != null)
        {
            println(controller.help());
        }else if ((matcher = PreGameCommands.SHOW_CURRENT_MENU.getMatcher(input)) != null)
        {
            println(controller.showCurrentMenu());
        } else
        {
            println("invalid command");
        }
    }

    public static String scan() {
        return TerminalScreen.readLine().trim();
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
