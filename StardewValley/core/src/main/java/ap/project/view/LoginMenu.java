package ap.project.view;

import ap.project.control.LoginController;
import ap.project.model.App.Result;
import ap.project.model.enums.regex_enums.LoginCommands;
import ap.project.screen.TerminalScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class LoginMenu implements AppMenu {
    LoginController controller = new LoginController();
    @Override
    public void check(Scanner scanner) {
        String input = scanner.nextLine().trim();
        Matcher matcher;

        if ((matcher = LoginCommands.LOGIN.getMatcher(input)) != null) {
            println(controller.login(matcher));
        } else if ((matcher = LoginCommands.FORGET_PASSWORD.getMatcher(input)) != null) {
            println(controller.forgetPassword(matcher, scanner));
        } else if ((matcher = LoginCommands.MENU_ENTER.getMatcher(input)) != null) {
            String menuName = matcher.group("menuName").trim();
            println(controller.enterMenu(menuName));
        } else if (LoginCommands.MENU_EXIT.getMatcher(input) != null) {
            println(controller.exit());
        } else if (LoginCommands.MENU_BACK.getMatcher(input) != null) {
            println(controller.back());
        } else if (LoginCommands.HELP.getMatcher(input) != null) {
            println(controller.help());
        } else if (LoginCommands.SHOW_CURRENT_MENU.getMatcher(input) != null) {
            println(controller.showCurrentMenu());
        } else {
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
