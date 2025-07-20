package ap.project.view;

import ap.project.control.RegisterController;
import ap.project.model.App.Result;
import ap.project.model.enums.SecurityQuestionType;
import ap.project.model.enums.regex_enums.RegisterCommands;
import ap.project.screen.TerminalScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class RegisterMenu implements AppMenu
{
    RegisterController controller = new RegisterController();
    @Override
    public void check(Scanner scanner)
    {
        String input = scanner.nextLine().trim();
        Matcher matcher;

//        if ((matcher = RegisterCommands.REGISTER.getMatcher(input)) != null) {
//            Result result = controller.register(matcher, scanner);
//            println(result);
//            if (result.isSuccessful()) {
//                println("choose your security question number: ");
//                for (SecurityQuestionType type : SecurityQuestionType.values()) {
//                    println(type.getId() + "- " + type.getQuestion());
//                }
//            }
//        }
//        if ((matcher = RegisterCommands.PICK_QUESTION.getMatcher(input)) != null) {
//            println(controller.pickQuestion(matcher));
//        } else if ((matcher = RegisterCommands.MENU_ENTER.getMatcher(input)) != null) {
//            String menuName = matcher.group("menuName").trim();
//            println(controller.enterMenu(menuName));
//        } else if (RegisterCommands.MENU_EXIT.getMatcher(input) != null) {
//            println(controller.exit());
//        } else if (RegisterCommands.MENU_BACK.getMatcher(input) != null) {
//            println(controller.back());
//        } else if (RegisterCommands.HELP.getMatcher(input) != null) {
//            println(controller.help());
//        } else if (RegisterCommands.SHOW_CURRENT_MENU.getMatcher(input) != null) {
//            println(controller.showCurrentMenu());
//        } else {
//            println("invalid command");
//        }
//    }
//
//    public static void println(Result result)
//    {
//        System.out.println(result.toString());
//        TerminalScreen.appendOutputLn(result.toString());
//    }
//
//    public static void print(Result result)
//    {
//        System.out.print(result.toString());
//        TerminalScreen.appendOutput(result.toString());
//    }
//
//    public static void println(String output)
//    {
//        System.out.println(output);
//        TerminalScreen.appendOutputLn(output);
//    }
//
//    public static void print(String output)
//    {
//        System.out.print(output);
//        TerminalScreen.appendOutput(output);
    }

    @Override
    public void check(String input)
    {

    }
}
