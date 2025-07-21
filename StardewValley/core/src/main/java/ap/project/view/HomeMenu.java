package ap.project.view;

import ap.project.control.GameController;
import ap.project.control.GeneralController;
import ap.project.control.game.activities.HomeController;
import ap.project.model.App.Result;
import ap.project.model.enums.regex_enums.GameCommands;
import ap.project.model.enums.regex_enums.GeneralCommands;
import ap.project.model.enums.regex_enums.HomeCommands;
import ap.project.screen.TerminalScreen;
import ap.project.screen.WorldScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class HomeMenu implements AppMenu
{
    static HomeController homeController = new HomeController();
    static GeneralController generalController = new GeneralController();
    static GameController gameController = new GameController();

    @Override
    public void check(Scanner scanner)
    {
        String input = scanner.nextLine().trim();
        Matcher matcher;

        /* Player Commands */
        if ((matcher = GeneralCommands.ENERGY_SHOW.getMatcher(input)) != null) {
            println(generalController.energyShow());
        } else if ((matcher = GeneralCommands.INVENTORY_SHOW.getMatcher(input)) != null) {
            generalController.inventoryShow();
        } else if ((matcher = GeneralCommands.INVENTORY_TRASH_NUMBER.getMatcher(input)) != null ||
                (matcher = GeneralCommands.INVENTORY_TRASH.getMatcher(input)) != null) {
            println(generalController.inventoryTrash(matcher));
        } else if ((matcher = GeneralCommands.TOOLS_EQUIP.getMatcher(input)) != null) {
            println(gameController.toolsEquip(matcher));
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_CURRENT.getMatcher(input)) != null) {
            println(gameController.toolsShowCurrent());
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_AVAILABLE.getMatcher(input)) != null) {
            gameController.toolsShowAvailable();
        } else if ((matcher = GeneralCommands.TOOLS_UPGRADE.getMatcher(input)) != null) {
            println(gameController.toolsUpgrade(matcher));
        } else if ((matcher = GameCommands.TOOLS_USE.getMatcher(input)) != null) {
            println(gameController.toolsUse(matcher));
        }

        /* player cheat codes */
        else if ((matcher = GeneralCommands.ENERGY_SET.getMatcher(input)) != null) {
            println(generalController.energySet(matcher));
        } else if ((matcher = GeneralCommands.ENERGY_UNLIMITED.getMatcher(input)) != null) {
            println(generalController.energyUnlimited());
        }

        /* time and date commands */
        else if (GeneralCommands.SHOW_TIME.getMatcher(input) != null)
        {
            println(generalController.showTime());
        } else if (GeneralCommands.SHOW_DATE.getMatcher(input) != null)
        {
            println(generalController.showDate());
        } else if (GeneralCommands.SHOW_DATE_TIME.getMatcher(input) != null)
        {
            println(generalController.showDateAndTime());
        } else if (GeneralCommands.SHOW_DAY_OF_WEEK.getMatcher(input) != null)
        {
            println(generalController.showDayOfWeek());
        } else if (GeneralCommands.SHOW_SEASON.getMatcher(input) != null)
        {
            println(generalController.showSeason());
        }

        /* time and date cheat codes */
        else if ((matcher = GeneralCommands.CHEAT_CODE_ADVANCE_TIME.getMatcher(input)) != null)
        {
            String time = matcher.group("time");
            println(generalController.cheatAdvanceTime(time));
        } else if ((matcher = GeneralCommands.CHEAT_CODE_ADVANCE_DATE.getMatcher(input)) != null)
        {
            String date = matcher.group("date");
            println(generalController.cheatAdvanceDate(date));
        }

        /* weather  commands*/
        else if (GeneralCommands.SHOW_WEATHER.getMatcher(input) != null)
        {
            println(generalController.showWeather());
        } else if (GeneralCommands.SHOW_TOMORROW_WEATHER.getMatcher(input) != null)
        {
            println(generalController.showTomorrowWeather());
        }

        /* weather cheat codes */
        else if ((matcher = GeneralCommands.CHEAT_CODE_SET_TOMORROW_WEATHER.getMatcher(input)) != null)
        {
            String type = matcher.group("type");
            println(generalController.cheatChangeTomorrowWeather(type));
        } else if ((matcher = GeneralCommands.CHEAT_CODE_HIT_THUNDER.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            println(generalController.cheatHitThunder(x,y));
        }

        /* basic map commands */
        else if ((matcher = GeneralCommands.PWD.getMatcher(input)) != null)
        {
            println(generalController.pwd());
        } else if ((matcher = GeneralCommands.SHOW_AROUND.getMatcher(input)) != null)
        {
            println(generalController.showAround());
        } else if ((matcher = GeneralCommands.PRINT_MAP.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            String size = matcher.group("size");
            println(generalController.printMap(x,y,size));
        } else if ((matcher = GeneralCommands.PRINT_ENTIRE_MAP.getMatcher(input)) != null)
        {
            println(generalController.printEntireMap());
        }

        /* walk commands */
        else if ((matcher = GeneralCommands.CAN_WALK.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            println(generalController.canWalk(x,y));
        } else if ((matcher = GeneralCommands.WALK.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            generalController.walk(x,y);
        } else if ((matcher = GeneralCommands.SHOW_PATH.getMatcher(input)) != null)
        {
            String x = matcher.group("x").trim();
            String y = matcher.group("y").trim();
            println(generalController.showPath(x,y));
        }

        /* walk cheat code */
        else if ((matcher = GeneralCommands.SUDO_CD.getMatcher(input)) != null)
        {
            String x = matcher.group("x").trim();
            String y = matcher.group("y").trim();
            println(generalController.sudoCD(x,y));
        }

        /* basic general commands */
        else if ((matcher = GeneralCommands.WHOAMI.getMatcher(input)) != null)
        {
            println(generalController.whoAmI());
        } else if ((matcher = GeneralCommands.NEXT_TURN.getMatcher(input)) != null)
        {
            generalController.nextTurn();
        } else if ((matcher = GeneralCommands.SUDO_NEXT_TURN.getMatcher(input)) != null)
        {
            println(generalController.sudoNextTurn());
        } else if ((matcher = GeneralCommands.EXIT_GAME.getMatcher(input)) != null)
        {
            println(generalController.exitGame(scanner));
        } else if ((matcher = GeneralCommands.DELETE_GAME.getMatcher(input)) != null)
        {
            println(generalController.deleteGame(scanner));
        }


        else if ((matcher = HomeCommands.CRAFTING_SHOW_RECIPES.getMatcher(input)) != null)
        {
            println(homeController.showCraftingRecipes());
        } else if ((matcher = HomeCommands.CRAFTING_CRAFT.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            println(homeController.craftItem(itemName));
        } else if ((matcher = HomeCommands.PLACE_ITEM.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            String direction = matcher.group("direction");
            println(homeController.placeItem(itemName, direction));
        } else if ((matcher = HomeCommands.CHEAT_ADD_ITEM.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            String count = matcher.group("count");
            println(homeController.cheatAddItem(itemName, count));
        } else if ((matcher = HomeCommands.COOKING_PUT.getMatcher(input)) != null)
        {
            String item = matcher.group("item");
            println(homeController.putInRefrigerator(item));
        } else if ((matcher = HomeCommands.COOKING_PICK.getMatcher(input)) != null)
        {
            String item = matcher.group("item");
            println(homeController.pickFromRefrigerator(item));
        } else if ((matcher = HomeCommands.COOKING_SHOW_RECIPES.getMatcher(input)) != null)
        {
            println(homeController.showCookingRecipes());
        } else if ((matcher = HomeCommands.COOKING_PREPARE.getMatcher(input)) != null)
        {
            String recipeName = matcher.group("recipeName");
            println(homeController.cookingPrepare(recipeName));
        } else if ((matcher = HomeCommands.EAT.getMatcher(input)) != null)
        {
            String foodName = matcher.group("foodName");
            println(homeController.eat(foodName));
        } else if ((matcher = HomeCommands.CHEAT_ADD_CRAFTING_RECIPE.getMatcher(input)) != null)
        {
            String recipeName = matcher.group("recipeName");
            println(homeController.cheatAddCraftingRecipe(recipeName));
        } else if ((matcher = HomeCommands.CHEAT_ADD_COOKING_RECIPE.getMatcher(input)) != null)
        {
            String recipeName = matcher.group("recipeName");
            println(homeController.cheatAddCookingRecipe(recipeName));
        } else if ((matcher = HomeCommands.CD_FARM.getMatcher(input)) != null)
        {
            println(homeController.goOut());
        } else if ((matcher = HomeCommands.PRINT_MAP.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            String size = matcher.group("size");
            println(homeController.printMap(x,y,size));
        } else if ((matcher = HomeCommands.SHOW_AROUND.getMatcher(input)) != null)
        {
            println(homeController.showAround());
        } else if ((matcher = HomeCommands.PRINT_ENTIRE_MAP.getMatcher(input)) != null)
        {
            println(homeController.printEntireMap());
        } else if ((matcher = HomeCommands.SHOW_REFRIGERATOR.getMatcher(input)) != null)
        {
            println(homeController.showRefrigerator());
        } else
        {
            println("invalid command");
        }
    }

    public void check(String input)
    {
        Matcher matcher;

        /* Player Commands */
        if ((matcher = GeneralCommands.ENERGY_SHOW.getMatcher(input)) != null) {
            println(generalController.energyShow());
        } else if ((matcher = GeneralCommands.INVENTORY_SHOW.getMatcher(input)) != null) {
            generalController.inventoryShow();
        } else if ((matcher = GeneralCommands.INVENTORY_TRASH_NUMBER.getMatcher(input)) != null ||
            (matcher = GeneralCommands.INVENTORY_TRASH.getMatcher(input)) != null) {
            println(generalController.inventoryTrash(matcher));
        } else if ((matcher = GeneralCommands.TOOLS_EQUIP.getMatcher(input)) != null) {
            println(gameController.toolsEquip(matcher));
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_CURRENT.getMatcher(input)) != null) {
            println(gameController.toolsShowCurrent());
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_AVAILABLE.getMatcher(input)) != null) {
            gameController.toolsShowAvailable();
        } else if ((matcher = GeneralCommands.TOOLS_UPGRADE.getMatcher(input)) != null) {
            println(gameController.toolsUpgrade(matcher));
        } else if ((matcher = GameCommands.TOOLS_USE.getMatcher(input)) != null) {
            println(gameController.toolsUse(matcher));
        }

        /* player cheat codes */
        else if ((matcher = GeneralCommands.ENERGY_SET.getMatcher(input)) != null) {
            println(generalController.energySet(matcher));
        } else if ((matcher = GeneralCommands.ENERGY_UNLIMITED.getMatcher(input)) != null) {
            println(generalController.energyUnlimited());
        }

        /* time and date commands */
        else if (GeneralCommands.SHOW_TIME.getMatcher(input) != null)
        {
            println(generalController.showTime());
        } else if (GeneralCommands.SHOW_DATE.getMatcher(input) != null)
        {
            println(generalController.showDate());
        } else if (GeneralCommands.SHOW_DATE_TIME.getMatcher(input) != null)
        {
            println(generalController.showDateAndTime());
        } else if (GeneralCommands.SHOW_DAY_OF_WEEK.getMatcher(input) != null)
        {
            println(generalController.showDayOfWeek());
        } else if (GeneralCommands.SHOW_SEASON.getMatcher(input) != null)
        {
            println(generalController.showSeason());
        }

        /* time and date cheat codes */
        else if ((matcher = GeneralCommands.CHEAT_CODE_ADVANCE_TIME.getMatcher(input)) != null)
        {
            String time = matcher.group("time");
            println(generalController.cheatAdvanceTime(time));
        } else if ((matcher = GeneralCommands.CHEAT_CODE_ADVANCE_DATE.getMatcher(input)) != null)
        {
            String date = matcher.group("date");
            println(generalController.cheatAdvanceDate(date));
        }

        /* weather  commands*/
        else if (GeneralCommands.SHOW_WEATHER.getMatcher(input) != null)
        {
            println(generalController.showWeather());
        } else if (GeneralCommands.SHOW_TOMORROW_WEATHER.getMatcher(input) != null)
        {
            println(generalController.showTomorrowWeather());
        }

        /* weather cheat codes */
        else if ((matcher = GeneralCommands.CHEAT_CODE_SET_TOMORROW_WEATHER.getMatcher(input)) != null)
        {
            String type = matcher.group("type");
            println(generalController.cheatChangeTomorrowWeather(type));
        } else if ((matcher = GeneralCommands.CHEAT_CODE_HIT_THUNDER.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            println(generalController.cheatHitThunder(x,y));
        }

        /* basic map commands */
        else if ((matcher = GeneralCommands.PWD.getMatcher(input)) != null)
        {
            println(generalController.pwd());
        } else if ((matcher = GeneralCommands.SHOW_AROUND.getMatcher(input)) != null)
        {
            println(generalController.showAround());
        } else if ((matcher = GeneralCommands.PRINT_MAP.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            String size = matcher.group("size");
            println(generalController.printMap(x,y,size));
        } else if ((matcher = GeneralCommands.PRINT_ENTIRE_MAP.getMatcher(input)) != null)
        {
            println(generalController.printEntireMap());
        }

        /* walk commands */
        else if ((matcher = GeneralCommands.CAN_WALK.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            println(generalController.canWalk(x,y));
        } else if ((matcher = GeneralCommands.WALK.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            generalController.walk(x,y);
        } else if ((matcher = GeneralCommands.SHOW_PATH.getMatcher(input)) != null)
        {
            String x = matcher.group("x").trim();
            String y = matcher.group("y").trim();
            println(generalController.showPath(x,y));
        }

        /* walk cheat code */
        else if ((matcher = GeneralCommands.SUDO_CD.getMatcher(input)) != null)
        {
            String x = matcher.group("x").trim();
            String y = matcher.group("y").trim();
            println(generalController.sudoCD(x,y));
        }

        /* basic general commands */
        else if ((matcher = GeneralCommands.WHOAMI.getMatcher(input)) != null)
        {
            println(generalController.whoAmI());
        } else if ((matcher = GeneralCommands.NEXT_TURN.getMatcher(input)) != null)
        {
            generalController.nextTurn();
        } else if ((matcher = GeneralCommands.SUDO_NEXT_TURN.getMatcher(input)) != null)
        {
            println(generalController.sudoNextTurn());
        } else if ((matcher = GeneralCommands.EXIT_GAME.getMatcher(input)) != null)
        {
//            println(generalController.exitGame(scanner));
        } else if ((matcher = GeneralCommands.DELETE_GAME.getMatcher(input)) != null)
        {
//            println(generalController.deleteGame(scanner));
        }


        else if ((matcher = HomeCommands.CRAFTING_SHOW_RECIPES.getMatcher(input)) != null)
        {
            println(homeController.showCraftingRecipes());
        } else if ((matcher = HomeCommands.CRAFTING_CRAFT.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            println(homeController.craftItem(itemName));
        } else if ((matcher = HomeCommands.PLACE_ITEM.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            String direction = matcher.group("direction");
            println(homeController.placeItem(itemName, direction));
        } else if ((matcher = HomeCommands.CHEAT_ADD_ITEM.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            String count = matcher.group("count");
            println(homeController.cheatAddItem(itemName, count));
        } else if ((matcher = HomeCommands.COOKING_PUT.getMatcher(input)) != null)
        {
            String item = matcher.group("item");
            println(homeController.putInRefrigerator(item));
        } else if ((matcher = HomeCommands.COOKING_PICK.getMatcher(input)) != null)
        {
            String item = matcher.group("item");
            println(homeController.pickFromRefrigerator(item));
        } else if ((matcher = HomeCommands.COOKING_SHOW_RECIPES.getMatcher(input)) != null)
        {
            println(homeController.showCookingRecipes());
        } else if ((matcher = HomeCommands.COOKING_PREPARE.getMatcher(input)) != null)
        {
            String recipeName = matcher.group("recipeName");
            println(homeController.cookingPrepare(recipeName));
        } else if ((matcher = HomeCommands.EAT.getMatcher(input)) != null)
        {
            String foodName = matcher.group("foodName");
            println(homeController.eat(foodName));
        } else if ((matcher = HomeCommands.CHEAT_ADD_CRAFTING_RECIPE.getMatcher(input)) != null)
        {
            String recipeName = matcher.group("recipeName");
            println(homeController.cheatAddCraftingRecipe(recipeName));
        } else if ((matcher = HomeCommands.CHEAT_ADD_COOKING_RECIPE.getMatcher(input)) != null)
        {
            String recipeName = matcher.group("recipeName");
            println(homeController.cheatAddCookingRecipe(recipeName));
        } else if ((matcher = HomeCommands.CD_FARM.getMatcher(input)) != null)
        {
            println(homeController.goOut());
        } else if ((matcher = HomeCommands.PRINT_MAP.getMatcher(input)) != null)
        {
            String x = matcher.group("x");
            String y = matcher.group("y");
            String size = matcher.group("size");
            println(homeController.printMap(x,y,size));
        } else if ((matcher = HomeCommands.SHOW_AROUND.getMatcher(input)) != null)
        {
            println(homeController.showAround());
        } else if ((matcher = HomeCommands.PRINT_ENTIRE_MAP.getMatcher(input)) != null)
        {
            println(homeController.printEntireMap());
        } else if ((matcher = HomeCommands.SHOW_REFRIGERATOR.getMatcher(input)) != null)
        {
            println(homeController.showRefrigerator());
        } else
        {
            println("invalid command");
        }
    }

    public static void println(Result result)
    {
        System.out.println(result.toString());
        WorldScreen.appendToDialog(result.toString() + "\n");
    }

    public static void print(Result result)
    {
        System.out.print(result.toString());
        WorldScreen.appendToDialog(result.toString());
    }

    public static void println(String output)
    {
        System.out.println(output);
        WorldScreen.appendToDialog(output + "\n");
    }

    public static void print(String output)
    {
        System.out.print(output);
        WorldScreen.appendToDialog(output);
    }
}
