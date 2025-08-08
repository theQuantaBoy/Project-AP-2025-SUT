package ap.project.view;

import ap.project.control.GeneralController;
import ap.project.control.game.activities.TradeController;
import ap.project.model.App.App;
import ap.project.model.App.Result;
import ap.project.model.game.Player;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.Menu;
import ap.project.model.enums.TradeType;
import ap.project.model.enums.regex_enums.GeneralCommands;
import ap.project.model.enums.regex_enums.TradeCommands;
import ap.project.screen.TerminalScreen;
import ap.project.screen.WorldScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class TradeMenu implements AppMenu {

    static TradeController controller = new TradeController();
    static GeneralController generalController = new GeneralController();

    @Override
    public void check(Scanner scanner) {
//        String input = scanner.nextLine().trim();
//        Matcher matcher;
//
//        if ((matcher = TradeCommands.TRADE.getMatcher(input)) != null) {
//            Player player = App.getCurrentGame().getPlayerByNickname(matcher.group("username"));
//            TradeType type = null;
//            String typeName = matcher.group("type");
//            if (typeName.equalsIgnoreCase("offer")) {
//                type = TradeType.OFFER;
//            } else if (typeName.equalsIgnoreCase("request")) {
//                type = TradeType.REQUEST;
//            }
//            GameObjectType item = null;
//            for (GameObjectType type1 : GameObjectType.values()) {
//                if (type1.name().equalsIgnoreCase(matcher.group("item"))) {
//                    item = type1;
//                    break;
//                }
//            }
//            int amount = -1;
//            try {
//                amount = Integer.parseInt(matcher.group("amount"));
//            } catch (Exception ignored) {}
//            int price = -1;
//            try {
//                price = Integer.parseInt(matcher.group("price"));
//            } catch (Exception ignored) {}
//
//            if (price != -1) {
//                println(controller.tradeWith(player, type, item, amount, price));
//            } else {
//                GameObjectType targetItem = null;
//                for (GameObjectType type1 : GameObjectType.values()) {
//                    if (type1.name().equalsIgnoreCase(matcher.group("targetItem"))) {
//                        targetItem = type1;
//                        break;
//                    }
//                }
//                int targetAmount = -1;
//                try {
//                    targetAmount = Integer.parseInt(matcher.group("targetAmount"));
//                } catch (Exception ignored) {}
//
//
//                println(controller.tradeWith(player, type, item, amount, targetItem, targetAmount));
//            }
//        } else if (TradeCommands.TRADE_LIST.getMatcher(input) != null) {
//            controller.tradeList();
//        } else if ((matcher = TradeCommands.TRADE_RESPOND.getMatcher(input)) != null) {
//            int id = Integer.parseInt(matcher.group("id"));
//            boolean respond = true;
//            if (matcher.group("respond").equals("reject")) {
//                respond = false;
//            }
//            println(controller.responseTrade(respond, id));
//        } else if (TradeCommands.TRADE_HISTORY.getMatcher(input) != null) {
//            controller.tradeHistory();
//        } else if (TradeCommands.END_TRADE.getMatcher(input) != null) {
//            App.setCurrentMenu(Menu.GameMenu);
//            println("redirecting to game menu...");
//        } else if (GeneralCommands.NEXT_TURN.getMatcher(input) != null)
//        {
//            generalController.nextTurn();
//        } else {
//            println("invalid command");
//        }
//    }
//
//    public void check(String input)
//    {
//        Matcher matcher;
//
//        if ((matcher = TradeCommands.TRADE.getMatcher(input)) != null) {
//            Player player = App.getCurrentGame().getPlayerByNickname(matcher.group("username"));
//            TradeType type = null;
//            String typeName = matcher.group("type");
//            if (typeName.equalsIgnoreCase("offer")) {
//                type = TradeType.OFFER;
//            } else if (typeName.equalsIgnoreCase("request")) {
//                type = TradeType.REQUEST;
//            }
//            GameObjectType item = null;
//            for (GameObjectType type1 : GameObjectType.values()) {
//                if (type1.name().equalsIgnoreCase(matcher.group("item"))) {
//                    item = type1;
//                    break;
//                }
//            }
//            int amount = -1;
//            try {
//                amount = Integer.parseInt(matcher.group("amount"));
//            } catch (Exception ignored) {}
//            int price = -1;
//            try {
//                price = Integer.parseInt(matcher.group("price"));
//            } catch (Exception ignored) {}
//
//            if (price != -1) {
//                println(controller.tradeWith(player, type, item, amount, price));
//            } else {
//                GameObjectType targetItem = null;
//                for (GameObjectType type1 : GameObjectType.values()) {
//                    if (type1.name().equalsIgnoreCase(matcher.group("targetItem"))) {
//                        targetItem = type1;
//                        break;
//                    }
//                }
//                int targetAmount = -1;
//                try {
//                    targetAmount = Integer.parseInt(matcher.group("targetAmount"));
//                } catch (Exception ignored) {}
//
//
//                println(controller.tradeWith(player, type, item, amount, targetItem, targetAmount));
//            }
//        } else if (TradeCommands.TRADE_LIST.getMatcher(input) != null) {
//            controller.tradeList();
//        } else if ((matcher = TradeCommands.TRADE_RESPOND.getMatcher(input)) != null) {
//            int id = Integer.parseInt(matcher.group("id"));
//            boolean respond = true;
//            if (matcher.group("respond").equals("reject")) {
//                respond = false;
//            }
//            println(controller.responseTrade(respond, id));
//        } else if (TradeCommands.TRADE_HISTORY.getMatcher(input) != null) {
//            controller.tradeHistory();
//        } else if (TradeCommands.END_TRADE.getMatcher(input) != null) {
//            App.setCurrentMenu(Menu.GameMenu);
//            println("redirecting to game menu...");
//        } else if (GeneralCommands.NEXT_TURN.getMatcher(input) != null)
//        {
//            generalController.nextTurn();
//        } else {
//            println("invalid command");
//        }
//    }
//
//    public static void println(Result result)
//    {
//        System.out.println(result.toString());
//        WorldScreen.appendToDialog(result.toString() + "\n");
//    }
//
//    public static void print(Result result)
//    {
//        System.out.print(result.toString());
//        WorldScreen.appendToDialog(result.toString());
//    }
//
//    public static void println(String output)
//    {
//        System.out.println(output);
//        WorldScreen.appendToDialog(output + "\n");
//    }
//
//    public static void print(String output)
//    {
//        System.out.print(output);
//        WorldScreen.appendToDialog(output);
//    }
    }

    @Override
    public void check(String input) {

    }
}
