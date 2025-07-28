package ap.project.view;

import ap.project.control.GameController;
import ap.project.control.GeneralController;
import ap.project.control.game.activities.AnimalController;
import ap.project.control.game.activities.MarketingController;
import ap.project.control.game.activities.CommunicateController;
import ap.project.control.game.activities.TradeController;
import ap.project.model.App.App;
import ap.project.model.App.Result;
import ap.project.model.App.TerminalEntry;
import ap.project.model.enums.Menu;
import ap.project.model.enums.regex_enums.CommunicateCommands;
import ap.project.model.enums.regex_enums.GameCommands;
import ap.project.model.enums.regex_enums.GeneralCommands;
import ap.project.screen.TerminalScreen;
import ap.project.screen.WorldScreen;

import java.util.Scanner;
import java.util.regex.Matcher;

public class GameMenu implements AppMenu
{
    static GameController gameController = new GameController();
    static CommunicateController comController = new CommunicateController();
    static GeneralController generalController = new GeneralController();
    static AnimalController animalController = new AnimalController();
    static TradeController tradeController = new TradeController();
    static MarketingController marketingController = new MarketingController();

    @Override
    public void check(Scanner scanner)
    {
        String input = scanner.nextLine().trim();
        Matcher matcher;
        Matcher comMatcher;

        //TODO: add checkFainted in game menu

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
        } else if ((matcher = GeneralCommands.CHEAT_TOOL_CHECK.getMatcher(input)) != null) {
            println(gameController.cheatToolCheck(matcher));
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


        else if ((matcher = GameCommands.SHOW_CRAFT_INFO.getMatcher(input)) != null)
        {
            String craftName = matcher.group("craftName").trim();
            println(gameController.showCraftInfo(craftName));
        } else if ((matcher = GameCommands.PLANT_SEED.getMatcher(input)) != null)
        {
            String seed = matcher.group("seed").trim();
            String direction = matcher.group("direction").trim();
            println(gameController.plantSeed(seed,direction));
        } else if ((matcher = GameCommands.FERTILIZE.getMatcher(input)) != null)
        {
            String fertilizer = matcher.group("fertilizer").trim();
            String direction = matcher.group("direction").trim();
            println(gameController.fertilize(fertilizer,direction));
        } else if ((matcher = GameCommands.HOW_MUCH_WATER.getMatcher(input)) != null)
        {
            println(gameController.howMuchWater());
        } else if ((matcher = GameCommands.CD_PLACE.getMatcher(input)) != null)
        {
            String placeName = matcher.group("placeName").trim();
            println(gameController.goToPlace(placeName));
        } else if ((matcher = GameCommands.HELP_READ_MAP.getMatcher(input)) != null)
        {
            println(gameController.helpReadMap());
        } else if ((matcher = GameCommands.BUILD_GREENHOUSE.getMatcher(input)) != null)
        {
            println(gameController.buildGreenhouse());
        } else if ((matcher = GameCommands.CHEAT_ADD_MONEY.getMatcher(input)) != null)
        {
            String amount = matcher.group("amount").trim();
            println(gameController.cheatAddMoney(amount));
        } else if ((matcher = GameCommands.SHOW_PLANT.getMatcher(input)) != null)
        {
            String x = matcher.group("x").trim();
            String y = matcher.group("y").trim();
            println(gameController.showPlant(x,y));
        } else if (GeneralCommands.SHOW_MONEY.getMatcher(input) != null) {
            println(gameController.showMoney());
        } else if (CommunicateCommands.FRIENDSHIP.getMatcher(input) != null) {
            comController.friendships();
        } else if ((matcher = CommunicateCommands.CHEAT_UPGRADE_FRIENDSHIP.getMatcher(input)) != null) {
            println(comController.cheatUpgradeFriendship(matcher));
        } else if ((matcher = CommunicateCommands.CHEAT_UPGRADE_XP.getMatcher(input)) != null) {
            println(comController.cheatUpgradeFriendshipLevel(matcher));
//        } else if ((matcher = CommunicateCommands.TALK.getMatcher(input)) != null) {
//            println(comController.talk(matcher));
        } else if ((matcher = CommunicateCommands.TALK_HISTORY.getMatcher(input)) != null) {
            comController.talkHistory(matcher);
//        } else if ((matcher = CommunicateCommands.GIFT.getMatcher(input)) != null) {
//            comController.gift(matcher);
        } else if (CommunicateCommands.GIFT_LIST.getMatcher(input) != null) {
            comController.giftList();
//        } else if ((matcher = CommunicateCommands.GIFT_RATE.getMatcher(input)) != null) {
//            println(comController.giftRate(matcher));
//        } else if ((matcher = CommunicateCommands.GIFT_HISTORY.getMatcher(input)) != null) {
//            comController.giftHistory(matcher);
//        } else if ((matcher = CommunicateCommands.HUG.getMatcher(input)) != null) {
//            println(comController.giveHug(matcher));
//        } else if ((matcher = CommunicateCommands.FLOWER.getMatcher(input)) != null) {
//            println(comController.giveFlower(matcher));
//        } else if ((matcher = CommunicateCommands.ASK_MARRIAGE.getMatcher(input)) != null) {
//            println(comController.purposeAsk(matcher));
//        } else if ((matcher = CommunicateCommands.RESPOND.getMatcher(input)) != null) {
//            comController.purposeRespond(matcher);
        }

        else if(GameCommands.BUILD_ANIMAL_HOUSE.getMatcher(input) != null)
        {
            println(animalController.buildAnimalBuilding(input));
        } else if(GameCommands.BUY_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.buyAnimal(input));
        } else if(GameCommands.PET_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.pet(input));
        } else if(GameCommands.ANIMAL_INFOS.getMatcher(input) != null)
        {
            animalController.showAnimalDetails();
        } else if(GameCommands.SHEPHERD_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.shepherdAnimal(input));
        } else if(GameCommands.FEED_HAY.getMatcher(input) != null)
        {
            println(animalController.feedHay(input));
        } else if(GameCommands.PRODUCES.getMatcher(input) != null)
        {
            animalController.showProducts();
        } else if(GameCommands.SELL_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.sellAnimal(input, scanner));
        } else if(GameCommands.SET_FRIENDSHIP.getMatcher(input) != null)
        {
            println(animalController.cheatSetFriendship(input));
        } else if (GameCommands.COLLECT_PRODUCES.getMatcher(input) != null)
        {
            println(animalController.collectProducts(input));
        }
        else if((matcher = GameCommands.FISHING.getMatcher(input)) != null)
        {
            println(animalController.fishing(input));
        }

        else if (GeneralCommands.START_TRADE.getMatcher(input) != null)
        {
            App.setCurrentMenu(Menu.TradeMenu);
            println("redirecting to trade menu...");
            tradeController.tradeList();
        }
//        else if((matcher = GameCommands.CHECK_SHOP.getMatcher(input)) != null) {
//            App.setCurrentMenu(Menu.ShopMenu);
//            Shop shop = new Blacksmith();
//            App.getCurrentGame().setCurrentShop(shop);
//        }

        else if ((matcher = GeneralCommands.CHEAT_TOOL_CHECK.getMatcher(input)) != null) {
            println(gameController.cheatToolCheck(matcher));
        }

        else if (input.equalsIgnoreCase("cheat"))
        {
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();

            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();

            println("fdfffffffffffffffffffffffffffffffffffffffffffff");
        } else if((matcher = GameCommands.SELL.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        } else if((matcher = GameCommands.SELL_N.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        }

        else
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
        } else if ((matcher = GeneralCommands.CHEAT_TOOL_CHECK.getMatcher(input)) != null) {
            println(gameController.cheatToolCheck(matcher));
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


        else if ((matcher = GameCommands.SHOW_CRAFT_INFO.getMatcher(input)) != null)
        {
            String craftName = matcher.group("craftName").trim();
            println(gameController.showCraftInfo(craftName));
        } else if ((matcher = GameCommands.PLANT_SEED.getMatcher(input)) != null)
        {
            String seed = matcher.group("seed").trim();
            String direction = matcher.group("direction").trim();
            println(gameController.plantSeed(seed,direction));
        } else if ((matcher = GameCommands.FERTILIZE.getMatcher(input)) != null)
        {
            String fertilizer = matcher.group("fertilizer").trim();
            String direction = matcher.group("direction").trim();
            println(gameController.fertilize(fertilizer,direction));
        } else if ((matcher = GameCommands.HOW_MUCH_WATER.getMatcher(input)) != null)
        {
            println(gameController.howMuchWater());
        } else if ((matcher = GameCommands.CD_PLACE.getMatcher(input)) != null)
        {
            String placeName = matcher.group("placeName").trim();
            println(gameController.goToPlace(placeName));
        } else if ((matcher = GameCommands.HELP_READ_MAP.getMatcher(input)) != null)
        {
            println(gameController.helpReadMap());
        } else if ((matcher = GameCommands.BUILD_GREENHOUSE.getMatcher(input)) != null)
        {
            println(gameController.buildGreenhouse());
        } else if ((matcher = GameCommands.CHEAT_ADD_MONEY.getMatcher(input)) != null)
        {
            String amount = matcher.group("amount").trim();
            println(gameController.cheatAddMoney(amount));
        } else if ((matcher = GameCommands.SHOW_PLANT.getMatcher(input)) != null)
        {
            String x = matcher.group("x").trim();
            String y = matcher.group("y").trim();
            println(gameController.showPlant(x,y));
        } else if (GeneralCommands.SHOW_MONEY.getMatcher(input) != null) {
            println(gameController.showMoney());
        } else if (CommunicateCommands.FRIENDSHIP.getMatcher(input) != null) {
            comController.friendships();
        } else if ((matcher = CommunicateCommands.CHEAT_UPGRADE_FRIENDSHIP.getMatcher(input)) != null) {
            println(comController.cheatUpgradeFriendship(matcher));
        } else if ((matcher = CommunicateCommands.CHEAT_UPGRADE_XP.getMatcher(input)) != null) {
            println(comController.cheatUpgradeFriendshipLevel(matcher));
//        } else if ((matcher = CommunicateCommands.TALK.getMatcher(input)) != null) {
//            println(comController.talk(matcher));
        } else if ((matcher = CommunicateCommands.TALK_HISTORY.getMatcher(input)) != null) {
            comController.talkHistory(matcher);
//        } else if ((matcher = CommunicateCommands.GIFT.getMatcher(input)) != null) {
//            comController.gift(matcher);
        } else if (CommunicateCommands.GIFT_LIST.getMatcher(input) != null) {
            comController.giftList();
//        } else if ((matcher = CommunicateCommands.GIFT_RATE.getMatcher(input)) != null) {
//            println(comController.giftRate(matcher));
//        } else if ((matcher = CommunicateCommands.GIFT_HISTORY.getMatcher(input)) != null) {
//            comController.giftHistory(matcher);
//        } else if ((matcher = CommunicateCommands.HUG.getMatcher(input)) != null) {
//            println(comController.giveHug(matcher));
//        } else if ((matcher = CommunicateCommands.FLOWER.getMatcher(input)) != null) {
//            println(comController.giveFlower(matcher));
//        } else if ((matcher = CommunicateCommands.ASK_MARRIAGE.getMatcher(input)) != null) {
//            println(comController.purposeAsk(matcher));
//        } else if ((matcher = CommunicateCommands.RESPOND.getMatcher(input)) != null) {
//            comController.purposeRespond(matcher);
        }

        else if(GameCommands.BUILD_ANIMAL_HOUSE.getMatcher(input) != null)
        {
            println(animalController.buildAnimalBuilding(input));
        } else if(GameCommands.BUY_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.buyAnimal(input));
        } else if(GameCommands.PET_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.pet(input));
        } else if(GameCommands.ANIMAL_INFOS.getMatcher(input) != null)
        {
            animalController.showAnimalDetails();
        } else if(GameCommands.SHEPHERD_ANIMAL.getMatcher(input) != null)
        {
            println(animalController.shepherdAnimal(input));
        } else if(GameCommands.FEED_HAY.getMatcher(input) != null)
        {
            println(animalController.feedHay(input));
        } else if(GameCommands.PRODUCES.getMatcher(input) != null)
        {
            animalController.showProducts();
        } else if(GameCommands.SELL_ANIMAL.getMatcher(input) != null)
        {
//            println(animalController.sellAnimal(input, scanner));
        } else if(GameCommands.SET_FRIENDSHIP.getMatcher(input) != null)
        {
            println(animalController.cheatSetFriendship(input));
        } else if (GameCommands.COLLECT_PRODUCES.getMatcher(input) != null)
        {
            println(animalController.collectProducts(input));
        }
        else if((matcher = GameCommands.FISHING.getMatcher(input)) != null)
        {
            println(animalController.fishing(input));
        }

        else if (GeneralCommands.START_TRADE.getMatcher(input) != null)
        {
            App.setCurrentMenu(Menu.TradeMenu);
            println("redirecting to trade menu...");
            tradeController.tradeList();
        }
//        else if((matcher = GameCommands.CHECK_SHOP.getMatcher(input)) != null) {
//            App.setCurrentMenu(Menu.ShopMenu);
//            Shop shop = new Blacksmith();
//            App.getCurrentGame().setCurrentShop(shop);
//        }

        else if ((matcher = GeneralCommands.CHEAT_TOOL_CHECK.getMatcher(input)) != null) {
            println(gameController.cheatToolCheck(matcher));
        }

        else if (input.equalsIgnoreCase("cheat"))
        {
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();

            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();
            App.getCurrentGame().getCurrentPlayer().getFishingSkill().addLevel();

            println("fdfffffffffffffffffffffffffffffffffffffffffffff");
        } else if((matcher = GameCommands.SELL.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        } else if((matcher = GameCommands.SELL_N.getMatcher(input)) != null) {
            println(marketingController.sell(input));
        }

        else
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
