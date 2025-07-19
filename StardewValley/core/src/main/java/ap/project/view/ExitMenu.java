package ap.project.view;

import java.util.Scanner;

public class ExitMenu implements AppMenu {
    @Override
    public void check(Scanner scanner) {
        System.exit(0);
    }

    @Override
    public void check(String input)
    {

    }
}
