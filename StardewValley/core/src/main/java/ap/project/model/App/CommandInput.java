package ap.project.model.App;

import java.util.Scanner;

public class CommandInput
{
    private static String currentCommand;

    public static void setCommand(String line)
    {
        currentCommand = line;
    }

    public static Scanner getScanner()
    {
        return new Scanner(currentCommand);
    }
}

