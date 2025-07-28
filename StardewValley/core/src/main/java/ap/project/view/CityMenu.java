package ap.project.view;

import ap.project.control.GeneralController;
import ap.project.control.game.activities.*;
import ap.project.model.App.Result;
import ap.project.model.enums.regex_enums.*;
import ap.project.screen.WorldScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class CityMenu implements AppMenu
{
    static GeneralController generalController = new GeneralController();
    static CityController cityController = new CityController();
    static CommunicateController comController = new CommunicateController();
    static MarketingController marketingController = new MarketingController();
    static HomeController homeController = new HomeController();
    static AnimalController animalController = new AnimalController();

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
            println(generalController.toolsEquip(matcher));
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_CURRENT.getMatcher(input)) != null) {
            println(generalController.toolsShowCurrent());
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_AVAILABLE.getMatcher(input)) != null) {
            generalController.toolsShowAvailable();
        } else if ((matcher = GeneralCommands.TOOLS_UPGRADE.getMatcher(input)) != null) {
            println(marketingController.upgradeTool(input));
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
//            generalController.walk(x,y, scanner);
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

        else if ((matcher = CityCommands.CD_PLACE.getMatcher(input)) != null)
        {
            String place = matcher.group("place");
            println(cityController.goToPlace(place));
        }else if (CommunicateCommands.FRIENDSHIP.getMatcher(input) != null) {
            comController.friendships();
        } else if ((matcher = CommunicateCommands.TALK.getMatcher(input)) != null) {
            println(comController.talk(matcher));
        } else if ((matcher = CommunicateCommands.TALK_HISTORY.getMatcher(input)) != null) {
            comController.talkHistory(matcher);
//        } else if ((matcher = CommunicateCommands.GIFT.getMatcher(input)) != null) {
//            comController.gift(matcher);
        } else if (CommunicateCommands.GIFT_LIST.getMatcher(input) != null) {
            comController.giftList();
        } else if ((matcher = CommunicateCommands.GIFT_RATE.getMatcher(input)) != null) {
            println(comController.giftRate(matcher));
        } else if ((matcher = CommunicateCommands.GIFT_HISTORY.getMatcher(input)) != null) {
            comController.giftHistory(matcher);
        } else if ((matcher = CommunicateCommands.HUG.getMatcher(input)) != null) {
            println(comController.giveHug(matcher));
        } else if ((matcher = CommunicateCommands.FLOWER.getMatcher(input)) != null) {
            println(comController.giveFlower(matcher));
        } else if ((matcher = CommunicateCommands.ASK_MARRIAGE.getMatcher(input)) != null) {
            println(comController.purposeAsk(matcher));
        } else if ((matcher = CommunicateCommands.RESPOND.getMatcher(input)) != null) {
            comController.purposeRespond(matcher);
        }

        else if(GameCommands.BUILD_ANIMAL_HOUSE.getMatcher(input) != null)
        {
            println(animalController.buildAnimalBuilding(input));
        } else if(GameCommands.BUY_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.buyAnimal(input));
        }

        else if((matcher = CityCommands.MEET_NPC.getMatcher(input)) != null)
        {
            println(marketingController.meetNPC(input));
        } else if((matcher = CityCommands.GIFT_NPC.getMatcher(input)) != null)
        {
            marketingController.giftNPC(input);
        } else if ((matcher = CityCommands.FRIENDSHIP_NPC_LIST.getMatcher(input)) != null)
        {
            println(marketingController.showFriendshipNPCList());
        }
        else if((matcher = CityCommands.QUESTS_LIST.getMatcher(input)) != null)
        {
            marketingController.questsNPCList();
        } else if((matcher = CityCommands.QUESTS_FINISH.getMatcher(input)) != null)
        {
            println(marketingController.questsFinish(input));
        } else if ((matcher = CityCommands.SHOW_GIFTS.getMatcher(input)) != null)
        {
            marketingController.showNpcGifts();
        } else if ((matcher = CityCommands.OPEN_GIFTS.getMatcher(input)) != null)
        {
            println(marketingController.openGifts());
        } else if ((matcher = HomeCommands.CHEAT_ADD_ITEM.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            String count = matcher.group("count");
            println(homeController.cheatAddItem(itemName, count));
        } else if ((matcher = CityCommands.WHERE_NPC.getMatcher(input)) != null)
        {
            String npcName = matcher.group("NPCname");
            println(marketingController.showNpcLocation(npcName));
        }

        else if((matcher = GameCommands.SHOW_ALL_PRODUCTS.getMatcher(input)) != null) {
            println(marketingController.showAllProducts());
        } else if((matcher = GameCommands.SHOW_AVAILABLE_PRODUCTS.getMatcher(input)) != null) {
            println(marketingController.showAvailableProducts());
        } else if((matcher = GameCommands.PURCHASE_N.getMatcher(input)) != null) {
            println(marketingController.purchase(input, true));
        } else if((matcher = GameCommands.PURCHASE.getMatcher(input)) != null) {
            println(marketingController.purchase(input, false));
        } else if((matcher = GameCommands.SELL.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        } else if((matcher = GameCommands.SELL_N.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        } else if((matcher = GeneralCommands.TOOLS_UPGRADE.getMatcher(input)) != null) {
            println(marketingController.upgradeTool(input));
        } else if((matcher = GameCommands.CHEAT_ADD_MONEY.getMatcher(input)) != null) {
            marketingController.cheatAddMoney(input);
        }

        else if ((matcher = GeneralCommands.GO_ESGH_O_HAL.getMatcher(input)) != null)
        {
            println(generalController.goEsghOHal());
        }

        else
        {
//            println("invalid command");
            println(animalController.showShop(input));
        }
    }

    public void check (String input)
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
            println(generalController.toolsEquip(matcher));
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_CURRENT.getMatcher(input)) != null) {
            println(generalController.toolsShowCurrent());
        } else if ((matcher = GeneralCommands.TOOLS_SHOW_AVAILABLE.getMatcher(input)) != null) {
            generalController.toolsShowAvailable();
        } else if ((matcher = GeneralCommands.TOOLS_UPGRADE.getMatcher(input)) != null) {
            println(marketingController.upgradeTool(input));
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

        else if ((matcher = CityCommands.CD_PLACE.getMatcher(input)) != null)
        {
            String place = matcher.group("place");
            println(cityController.goToPlace(place));
        }else if (CommunicateCommands.FRIENDSHIP.getMatcher(input) != null) {
            comController.friendships();
        } else if ((matcher = CommunicateCommands.TALK.getMatcher(input)) != null) {
            println(comController.talk(matcher));
        } else if ((matcher = CommunicateCommands.TALK_HISTORY.getMatcher(input)) != null) {
            comController.talkHistory(matcher);
//        } else if ((matcher = CommunicateCommands.GIFT.getMatcher(input)) != null) {
//            comController.gift(matcher);
        } else if (CommunicateCommands.GIFT_LIST.getMatcher(input) != null) {
            comController.giftList();
        } else if ((matcher = CommunicateCommands.GIFT_RATE.getMatcher(input)) != null) {
            println(comController.giftRate(matcher));
        } else if ((matcher = CommunicateCommands.GIFT_HISTORY.getMatcher(input)) != null) {
            comController.giftHistory(matcher);
        } else if ((matcher = CommunicateCommands.HUG.getMatcher(input)) != null) {
            println(comController.giveHug(matcher));
        } else if ((matcher = CommunicateCommands.FLOWER.getMatcher(input)) != null) {
            println(comController.giveFlower(matcher));
        } else if ((matcher = CommunicateCommands.ASK_MARRIAGE.getMatcher(input)) != null) {
            println(comController.purposeAsk(matcher));
        } else if ((matcher = CommunicateCommands.RESPOND.getMatcher(input)) != null) {
            comController.purposeRespond(matcher);
        }

        else if(GameCommands.BUILD_ANIMAL_HOUSE.getMatcher(input) != null)
        {
            println(animalController.buildAnimalBuilding(input));
        } else if(GameCommands.BUY_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.buyAnimal(input));
        }

        else if((matcher = CityCommands.MEET_NPC.getMatcher(input)) != null)
        {
            println(marketingController.meetNPC(input));
        } else if((matcher = CityCommands.GIFT_NPC.getMatcher(input)) != null)
        {
            marketingController.giftNPC(input);
        } else if ((matcher = CityCommands.FRIENDSHIP_NPC_LIST.getMatcher(input)) != null)
        {
            println(marketingController.showFriendshipNPCList());
        }
        else if((matcher = CityCommands.QUESTS_LIST.getMatcher(input)) != null)
        {
            marketingController.questsNPCList();
        } else if((matcher = CityCommands.QUESTS_FINISH.getMatcher(input)) != null)
        {
            println(marketingController.questsFinish(input));
        } else if ((matcher = CityCommands.SHOW_GIFTS.getMatcher(input)) != null)
        {
            marketingController.showNpcGifts();
        } else if ((matcher = CityCommands.OPEN_GIFTS.getMatcher(input)) != null)
        {
            println(marketingController.openGifts());
        } else if ((matcher = HomeCommands.CHEAT_ADD_ITEM.getMatcher(input)) != null)
        {
            String itemName = matcher.group("itemName");
            String count = matcher.group("count");
            println(homeController.cheatAddItem(itemName, count));
        } else if ((matcher = CityCommands.WHERE_NPC.getMatcher(input)) != null)
        {
            String npcName = matcher.group("NPCname");
            println(marketingController.showNpcLocation(npcName));
        }

        else if((matcher = GameCommands.SHOW_ALL_PRODUCTS.getMatcher(input)) != null) {
            println(marketingController.showAllProducts());
        } else if((matcher = GameCommands.SHOW_AVAILABLE_PRODUCTS.getMatcher(input)) != null) {
            println(marketingController.showAvailableProducts());
        } else if((matcher = GameCommands.PURCHASE_N.getMatcher(input)) != null) {
            println(marketingController.purchase(input, true));
        } else if((matcher = GameCommands.PURCHASE.getMatcher(input)) != null) {
            println(marketingController.purchase(input, false));
        } else if((matcher = GameCommands.SELL.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        } else if((matcher = GameCommands.SELL_N.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        } else if((matcher = GeneralCommands.TOOLS_UPGRADE.getMatcher(input)) != null) {
            println(marketingController.upgradeTool(input));
        } else if((matcher = GameCommands.CHEAT_ADD_MONEY.getMatcher(input)) != null) {
            marketingController.cheatAddMoney(input);
        }

        else if ((matcher = GeneralCommands.GO_ESGH_O_HAL.getMatcher(input)) != null)
        {
            println(generalController.goEsghOHal());
        }

        else
        {
//            println("invalid command");
            println(animalController.showShop(input));
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
