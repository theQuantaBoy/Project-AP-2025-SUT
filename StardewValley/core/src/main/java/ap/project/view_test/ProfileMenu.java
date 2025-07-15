package ap.project.view_test;

import ap.project.control.ProfileController;
import ap.project.model.App.Result;
import ap.project.model.enums.regex_enums.ProfileCommands;
import ap.project.screen.TerminalScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class ProfileMenu implements AppMenu
{
    ProfileController controller = new ProfileController();

    @Override
    public void check(Scanner scanner)
    {
        String input = scanner.nextLine().trim();
        Matcher matcher;

        if ((matcher = ProfileCommands.CHANGE_USERNAME.getMatcher(input)) != null)
        {
            String newUsername = matcher.group("newUsername").trim();
            println(controller.changeUsername(newUsername, scanner));
        } else if ((matcher = ProfileCommands.CHANGE_PASSWORD.getMatcher(input)) != null)
        {
            String newPassword = matcher.group("newPassword").trim();
            String oldPassword = matcher.group("oldPassword").trim();
            println(controller.changePassword(newPassword, oldPassword));
        } else if ((matcher = ProfileCommands.SET_PASSWORD_TO_RANDOM.getMatcher(input)) != null)
        {
            String oldPassword = matcher.group("oldPassword").trim();
            controller.setRandomPassword(oldPassword, scanner);
        } else if ((matcher = ProfileCommands.CHANGE_NICKNAME.getMatcher(input)) != null)
        {
            String newNickname = matcher.group("newNickname").trim();
            println(controller.changeNickName(newNickname));
        } else if ((matcher = ProfileCommands.CHANGE_EMAIL.getMatcher(input)) != null)
        {
            String newEmail = matcher.group("newEmail").trim();
            println(controller.changeEmail(newEmail));
        } else if ((matcher = ProfileCommands.CHANGE_GENDER.getMatcher(input)) != null)
        {
            String newGender = matcher.group("newGender").trim();
            println(controller.changeGender(newGender));
        } else if ((matcher = ProfileCommands.USER_INFO.getMatcher(input)) != null)
        {
            println(controller.userInfo());
        } else if ((matcher = ProfileCommands.MENU_EXIT.getMatcher(input)) != null)
        {
            println(controller.exitMenu());
        } else if ((matcher = ProfileCommands.SHOW_CURRENT_MENU.getMatcher(input)) != null)
        {
            println(controller.showCurrentMenu());
        } else if ((matcher = ProfileCommands.MENU_ENTER.getMatcher(input)) != null)
        {
            String menuName = matcher.group("menuName").trim();
            println(controller.enterMenu(menuName));
        } else if ((matcher = ProfileCommands.MENU_BACK.getMatcher(input)) != null)
        {
            println(controller.back());
        } else if ((matcher = ProfileCommands.HELP.getMatcher(input)) != null)
        {
            println(controller.help());
        } else
        {
            println("invalid command");
        }
    }

    public static String scan()
    {
        return null;
//        return TerminalScreen.readLine().trim();
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
