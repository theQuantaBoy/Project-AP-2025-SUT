package ap.project.control;

import ap.project.model.App.App;
import ap.project.model.App.Result;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.enums.Menu;
import ap.project.model.enums.SecurityQuestionType;
import ap.project.model.enums.regex_enums.RegisterCommands;

import java.security.SecureRandom;
import java.util.Scanner;
import java.util.regex.Matcher;

public class RegisterController
{
    private static final SecureRandom random = new SecureRandom();
    private static User newUser = null;

    public Result register(String username, String password, String passwordConfirm, String email, String nickName, Gender gender, String secQ, String secA)
    {
        boolean random = false;

        if (RegisterCommands.CHECK_USERNAME.getMatcher(username) == null) {
            return new Result(false, "username is not valid!");
        } else if (App.getPlayerByUsername(username) != null) {
            return new Result(false, "username is already taken!");
        }
        if (!random) {
            if (RegisterCommands.CHECK_PASSWORD.getMatcher(password) == null) {
                return new Result(false, "password is invalid!");
            } else if (password.length() < 8) {
                return new Result(false, "password is short!");
            } else if (!password.matches(".*[a-z].*")) {
                return new Result(false, "your password should include small letters!");
            } else if (!password.matches(".*[A-Z].*")) {
                return new Result(false, "your password should include capital letters!");
            } else if (!password.matches(".*[0-9].*")) {
                return new Result(false, "your password should include numbers!");
            } else if (!password.matches(".*[!@#$%^&*)(=+}{\\[\\]|\\\\/:;'\",><?].*")) {
                return new Result(false, "your password should include special character!");
            } else if (!password.equals(passwordConfirm)) {
                return new Result(false, "your password doesn't match confirm password!");
            } else if (RegisterCommands.CHECK_EMAIL.getMatcher(email) == null) {
                return new Result(false, "email is invalid!");
            } else if (nickName.equals("")) {
                return new Result(false, "your nickname must not be empty!");
            } else if (secA.equals("")) {
                return new Result(false, "your answer must not be empty!");
            }
        }

        newUser = new User(username, password, nickName, email, gender, secQ, secA);
        App.createUser(newUser);
        return new Result(true, "user created successfully!");
    }

    public Result enterMenu(String menuName)
    {
        String output;
        if (menuName.equalsIgnoreCase("login menu") || menuName.equalsIgnoreCase("login"))
        {
            App.setCurrentMenu(Menu.LoginMenu);
            output = "Switching to login menu...";
        } else
        {
            output = "Menu name is invalid or you can not switch to this menu from here.";
        }

        return new Result(true, output);
    }

    public Result showCurrentMenu()
    {
        return new Result(true, "You are currently in Register Menu. Use 'help' for more information.");
    }

    public Result back()
    {
        App.setCurrentMenu(Menu.LoginMenu);
        return new Result(true, "Redirecting to login menu...");
    }

    public Result exit()
    {
        App.setCurrentMenu(Menu.ExitMenu);
        return new Result(true, "Exiting the game...");
    }

    public Result help()
    {
        return new Result(true, "Available commands:\n" +
                "\n" +
                "- register -u <username> -p <password> <confirmPassword> -n <nickname> -e <email> -g <gender>\n" +
                "    creates a new account. you can get a random password by typing 'random password' in -p flag\n" +
                "\n" +
                "- forget password -u <username>\n" +
                "    you can change your password here if you pass the security question\n" +
                "\n" +
                "- menu enter <menuName>\n" +
                "    Enters the specified menu. Example: menu enter login menu\n" +
                "\n" +
                "- menu back\n" +
                "    Exits the register menu and returns to the login menu.\n" +
                "\n" +
                "- menu exit\n" +
                "    Exits the game.\n" +
                "\n" +
                "- show current menu\n" +
                "    Displays the name of the current active menu.\n" +
                "\n" +
                "- help\n" +
                "    Shows this help message.\n");
    }

    public static String generatePassword()
    {
//        String specialChars = "!@#$%^&*-_=+{};:,.<>?";
//        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" + specialChars;
//
//        int length = 8 + random.nextInt(9);
//        StringBuilder password = new StringBuilder(length);
//        for (int i = 0; i < length; i++) {
//            int index = random.nextInt(allowedChars.length());
//            password.append(allowedChars.charAt(index));
//        }
//        return password.toString();

        return ProfileController.generateStrongRandomPassword();
    }
}
