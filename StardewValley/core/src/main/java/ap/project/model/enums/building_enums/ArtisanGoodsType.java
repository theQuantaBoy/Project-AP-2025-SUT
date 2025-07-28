package ap.project.model.enums.building_enums;

import ap.project.model.game.GameObject;
import ap.project.model.animal.Fish;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.animal_enums.FishType;
import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.ForagingCropType;
import ap.project.model.enums.resources_enums.FruitType;
import ap.project.model.enums.resources_enums.TreeType;
import ap.project.model.resources.Crop;
import ap.project.model.resources.ForagingCrop;
import ap.project.model.resources.Tree;

import java.util.ArrayList;
import java.util.Random;

public enum ArtisanGoodsType
{
    // Bee House
    HONEY(GameObjectType.HONEY, CraftingRecipeEnums.BEE_HOUSE_RECIPE, "It's a sweet syrup produced by bees.",
            75, 56, null, 350),


    // Cheese Press
    //  cheese - start
    CHEESE_1(GameObjectType.CHEESE, CraftingRecipeEnums.CHEESE_PRESS_RECIPE, "It's your basic cheese.", 100,
            3, new GameObject(GameObjectType.MILK, 1), 230),
    CHEESE_2(GameObjectType.CHEESE, CraftingRecipeEnums.CHEESE_PRESS_RECIPE, "It's your basic cheese.", 100,
            3, new GameObject(GameObjectType.LARGE_MILK, 1), 345),
    //  cheese - end

    //  goat cheese - start
    GOAT_CHEESE_1(GameObjectType.GOAT_CHEESE, CraftingRecipeEnums.CHEESE_PRESS_RECIPE, "Soft cheese made from goat's milk.",
            100, 3, new GameObject(GameObjectType.GOAT_MILK, 1), 400),
    GOAT_CHEESE_2(GameObjectType.GOAT_CHEESE, CraftingRecipeEnums.CHEESE_PRESS_RECIPE, "Soft cheese made from goat's milk.",
            100, 3, new GameObject(GameObjectType.LARGE_GOAT_MILK, 1), 600),
    //  goat cheese - end


    // Keg
    BEER(GameObjectType.BEER, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
            50, 14, new GameObject(GameObjectType.WHEAT, 1), 200),

    VINEGAR(GameObjectType.VINEGAR, CraftingRecipeEnums.KEG_RECIPE, "An aged fermented liquid used in many cooking recipes.",
            13, 10, new GameObject(GameObjectType.RICE, 1), 100),

    COFFEE(GameObjectType.COFFEE, CraftingRecipeEnums.KEG_RECIPE, "It smells delicious. This is sure to give you a boost.",
            75, 2, new GameObject(GameObjectType.COFFEE_BEAN, 5), 150),

    //  juice - start
    JUICE_01(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
            50, 56, new GameObject(GameObjectType.AMARANTH, 1), 50),

    JUICE_02(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.ARTICHOKE_CROP, 1), 50),

    JUICE_03(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.BEET, 1), 50),

    JUICE_04(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.BOK_CHOY_CROP, 1), 50),

    JUICE_05(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.BROCCOLI_CROP, 1), 50),

    JUICE_06(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.CARROT, 1), 50),

    JUICE_07(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.CAULIFLOWER_CROP, 1), 50),

    JUICE_08(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.CORN, 1), 50),

    JUICE_09(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.EGGPLANT, 1), 50),

    JUICE_10(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.FIDDLEHEAD_FERN_FORAGING_CROP, 1), 50),

    JUICE_11(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.GARLIC_CROP, 1), 50),

    JUICE_12(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.GREEN_BEAN_CROP, 1), 50),

    JUICE_13(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.HOPS_CROP, 1), 50),

    JUICE_14(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.KALE, 1), 50),

    JUICE_15(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.PARSNIP, 1), 50),

    JUICE_16(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.POTATO, 1), 50),

    JUICE_17(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.PUMPKIN, 1), 50),

    JUICE_18(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.RADISH, 1), 50),

    JUICE_19(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.RED_CABBAGE, 1), 50),

    JUICE_20(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.SUMMER_SQUASH_CROP, 1), 50),

    JUICE_21(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.TOMATO, 1), 50),

    JUICE_22(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.UNMILLED_RICE_CROP, 1), 50),

    JUICE_23(GameObjectType.JUICE, CraftingRecipeEnums.KEG_RECIPE, "A sweet, nutritious beverage.",
        50, 56, new GameObject(GameObjectType.WHEAT, 1), 50),
    //  juice - end

    MEAD(GameObjectType.MEAD, CraftingRecipeEnums.KEG_RECIPE, "A fermented beverage made from honey. Drink in moderation.",
            100, 10, new GameObject(GameObjectType.HONEY, 1), 300),

    PALE_ALE(GameObjectType.PALE_ALE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
            50, 42, new GameObject(GameObjectType.HOPS_CROP, 1), 300),

    //  wine - start
    WINE_01(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
            50, 98, new GameObject(GameObjectType.ANCIENT_FRUIT_CROP, 1), 300),

    WINE_02(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.APPLE, 1), 300),

    WINE_03(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.APRICOT, 1), 300),

    WINE_04(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.BANANA, 1), 300),

    WINE_05(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.BLACKBERRY_FORAGING_CROP, 1), 300),

    WINE_06(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.BLUEBERRY, 1), 300),

    WINE_07(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.CHERRY, 1), 300),

    WINE_08(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.CRANBERRIES_CROP, 1), 300),

    WINE_09(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.CRYSTAL_FRUIT_FORAGING_CROP, 1), 300),

    WINE_10(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        200, 98, new GameObject(GameObjectType.GRAPE, 1), 1000),

    WINE_11(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.HOT_PEPPER_CROP, 1), 300),

    WINE_12(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.MANGO, 1), 300),

    WINE_13(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.MELON, 1), 300),

    WINE_14(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.ORANGE, 1), 300),

    WINE_15(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.PEACH, 1), 300),

    WINE_16(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.POMEGRANATE, 1), 300),

    WINE_17(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.POWDERMELON_CROP, 1), 300),

    WINE_18(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.RHUBARB_CROP, 1), 300),

    WINE_19(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
    50, 98, new GameObject(GameObjectType.SALMONBERRY_FORAGING_CROP, 1), 300),

    WINE_20(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.SPICE_BERRY_FORAGING_CROP, 1), 300),

    WINE_21(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.STARFRUIT_CROP, 1), 300),

    WINE_22(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.STRAWBERRY_CROP, 1), 300),

    WINE_23(GameObjectType.WINE, CraftingRecipeEnums.KEG_RECIPE, "Drink in moderation.",
        50, 98, new GameObject(GameObjectType.WILD_PLUM_FORAGING_CROP, 1), 300),
    //  wine - end


    // Dehydrator
    //  dried mushroom - start
    DRIED_MUSHROOMS_1(GameObjectType.DRIED_MUSHROOM, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "A package of gourmet mushrooms.",
            50, 14, new GameObject(GameObjectType.COMMON_MUSHROOM, 5), 150),

    DRIED_MUSHROOMS_2(GameObjectType.DRIED_MUSHROOM, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "A package of gourmet mushrooms.",
        50, 14, new GameObject(GameObjectType.RED_MUSHROOM_FORAGING_CROP, 5), 150),

    DRIED_MUSHROOMS_3(GameObjectType.DRIED_MUSHROOM, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "A package of gourmet mushrooms.",
        50, 14, new GameObject(GameObjectType.PURPLE_MUSHROOM_FORAGING_CROP, 5), 150),
    //  dried mushroom - end

    //  dried fruit - start
    DRIED_FRUIT_01(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.ANCIENT_FRUIT_CROP, 5), 150),

    DRIED_FRUIT_02(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.APPLE, 5), 150),

    DRIED_FRUIT_03(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.APRICOT, 5), 150),

    DRIED_FRUIT_04(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.BANANA, 5), 150),

    DRIED_FRUIT_05(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.BLACKBERRY_FORAGING_CROP, 5), 150),

    DRIED_FRUIT_06(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.BLUEBERRY, 5), 150),

    DRIED_FRUIT_07(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.CHERRY, 5), 150),

    DRIED_FRUIT_08(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.CRANBERRIES_CROP, 5), 150),

    DRIED_FRUIT_09(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.CRYSTAL_FRUIT_FORAGING_CROP, 5), 150),

    DRIED_FRUIT_10(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.MANGO, 5), 150),

    DRIED_FRUIT_11(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.MELON, 5), 150),

    DRIED_FRUIT_12(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.ORANGE, 5), 150),

    DRIED_FRUIT_13(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.PEACH, 5), 150),

    DRIED_FRUIT_14(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.POMEGRANATE, 5), 150),

    DRIED_FRUIT_15(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.POWDERMELON_CROP, 5), 150),

    DRIED_FRUIT_16(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.RHUBARB_CROP, 5), 150),

    DRIED_FRUIT_17(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.SALMONBERRY_FORAGING_CROP, 5), 150),

    DRIED_FRUIT_18(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.SPICE_BERRY_FORAGING_CROP, 5), 150),

    DRIED_FRUIT_19(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.STARFRUIT_CROP, 5), 150),

    DRIED_FRUIT_20(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.STRAWBERRY_CROP, 5), 150),

    DRIED_FRUIT_21(GameObjectType.DRIED_FRUIT, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "Chewy pieces of dried fruit.",
        75, 14, new GameObject(GameObjectType.WILD_PLUM_FORAGING_CROP, 5), 150),
    //  dried fruit - end

    RAISINS(GameObjectType.RAISIN, CraftingRecipeEnums.DEHYDRATOR_RECIPE, "It's said to be the Junimos' favorite food.",
            125, 14, new GameObject(GameObjectType.GRAPE, 5), 600),



    // Charcoal Klin
    COAL(GameObjectType.COAL, CraftingRecipeEnums.CHARCOAL_KILN_RECIPE, "Turns 10 pieces of wood into one piece of coal.",
            -1, 1, new GameObject(GameObjectType.WOOD, 10), 50),


    // Loom
    CLOTH(GameObjectType.CLOTH, CraftingRecipeEnums.LOOM_RECIPE, "A bolt of fine wool cloth.",
            -1, 4, new GameObject(GameObjectType.WOOL, 1), 470),


    // Mayonnaise Machine
    //  mayonnaise - start
    MAYONNAISE_1(GameObjectType.MAYONNAISE, CraftingRecipeEnums.MAYONNAISE_MACHINE_RECIPE, "It looks spreadable.",
            50, 3, new GameObject(GameObjectType.EGG, 1), 190),

    MAYONNAISE_2(GameObjectType.MAYONNAISE, CraftingRecipeEnums.MAYONNAISE_MACHINE_RECIPE, "It looks spreadable.",
            50, 3, new GameObject(GameObjectType.LARGE_EGG, 1), 237),
    // mayonnaise - end

    DUCK_MAYONNAISE(GameObjectType.DUCK_MAYONNAISE, CraftingRecipeEnums.MAYONNAISE_MACHINE_RECIPE, "It's a rich, yellow mayonnaise.",
            75, 3, new GameObject(GameObjectType.DUCK_EGG, 1), 37),

    DINOSAUR_MAYONNAISE(GameObjectType.DINOSAUR_MAYONNAISE, CraftingRecipeEnums.MAYONNAISE_MACHINE_RECIPE, "It's thick and creamy, with a vivid green hue. It smells like grass and leather.",
            125, 3, new GameObject(GameObjectType.DINOSAUR_EGG, 1), 800),


    // Oil Maker
    TRUFFLE_OIL(GameObjectType.TRUFFLE_OIL, CraftingRecipeEnums.OIL_MAKER_RECIPE, "A gourmet cooking ingredient.",
            38, 6, new GameObject(GameObjectType.TRUFFLE, 1), 1065),

    //  oil - start
    OIL_1(GameObjectType.OIL, CraftingRecipeEnums.OIL_MAKER_RECIPE, "All purpose cooking oil.",
            13, 6, new GameObject(GameObjectType.CORN, 1), 100),

    OIL_2(GameObjectType.OIL, CraftingRecipeEnums.OIL_MAKER_RECIPE, "All purpose cooking oil.",
            13, 28, new GameObject(GameObjectType.SUNFLOWER_SEEDS, 1), 100),

    OIL_3(GameObjectType.OIL, CraftingRecipeEnums.OIL_MAKER_RECIPE, "All purpose cooking oil.",
            13, 1, new GameObject(GameObjectType.SUNFLOWER, 1), 100),
    //  oil - end


    // Preserves Jar
    //  pickle - start
    PICKLES_01(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.AMARANTH, 1), 200),

    PICKLES_02(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.BEET, 1), 200),

    PICKLES_03(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.BOK_CHOY_CROP, 1), 200),

    PICKLES_04(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.BROCCOLI_CROP, 1), 200),

    PICKLES_05(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.CARROT, 1), 200),

    PICKLES_06(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.CAULIFLOWER_CROP, 1), 200),

    PICKLES_07(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.CORN, 1), 200),

    PICKLES_08(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.EGGPLANT, 1), 200),

    PICKLES_09(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.FIDDLEHEAD_FERN_FORAGING_CROP, 1), 200),

    PICKLES_10(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.GARLIC_CROP, 1), 200),

    PICKLES_11(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.GREEN_BEAN_CROP, 1), 200),

    PICKLES_12(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.HOPS_CROP, 1), 200),

    PICKLES_13(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.KALE, 1), 200),

    PICKLES_14(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.PARSNIP, 1), 200),

    PICKLES_15(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.POTATO, 1), 200),

    PICKLES_16(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.PUMPKIN, 1), 200),

    PICKLES_17(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.RADISH, 1), 200),

    PICKLES_18(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.RED_CABBAGE_CROP, 1), 200),

    PICKLES_19(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.SUMMER_SQUASH_CROP, 1), 200),

    PICKLES_20(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.TOMATO, 1), 200),

    PICKLES_21(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.UNMILLED_RICE_CROP, 1), 200),

    PICKLES_22(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.WHEAT, 1), 200),

    PICKLES_23(GameObjectType.PICKLE, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "A jar of your home-made pickles.",
        15, 6, new GameObject(GameObjectType.YAM_CROP, 1), 200),
    //  pickle - end

    //  jelly - start
    JELLY_01(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
            10, 42, new GameObject(GameObjectType.ANCIENT_FRUIT_CROP, 1), 300),

    JELLY_02(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.APPLE, 1), 300),

    JELLY_03(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.APRICOT, 1), 300),

    JELLY_04(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.BANANA, 1), 300),

    JELLY_05(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.BLACKBERRY_FORAGING_CROP, 1), 300),

    JELLY_06(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.BLUEBERRY, 1), 300),

    JELLY_07(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.CHERRY, 1), 300),

    JELLY_08(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.CRANBERRIES_CROP, 1), 300),

    JELLY_09(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.GRAPE, 1), 300),

    JELLY_10(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.MANGO, 1), 300),

    JELLY_11(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.MELON, 1), 300),

    JELLY_12(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.ORANGE, 1), 300),

    JELLY_13(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.PEACH, 1), 300),

    JELLY_14(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.POMEGRANATE, 1), 300),

    JELLY_15(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.POWDERMELON_CROP, 1), 300),

    JELLY_16(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.RHUBARB_CROP, 1), 300),

    JELLY_17(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.SPICE_BERRY_FORAGING_CROP, 1), 300),

    JELLY_18(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.STARFRUIT_CROP, 1), 300),

    JELLY_19(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.STRAWBERRY_CROP, 1), 300),

    JELLY_20(GameObjectType.JELLY, CraftingRecipeEnums.PRESERVES_JAR_RECIPE, "Gooey.",
        10, 42, new GameObject(GameObjectType.WILD_PLUM_FORAGING_CROP, 1), 300),
    //  jelly - end


    // Fish Smoker
    //  smoked fish - start
    SMOKED_FISH_01(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
            50, 1, new GameObject(GameObjectType.FISH, 1), 60),

    SMOKED_FISH_02(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.TUNA, 1), 60),

    SMOKED_FISH_03(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.SARDINE, 1), 60),

    SMOKED_FISH_04(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.SALMON, 1), 60),

    SMOKED_FISH_05(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.PERCH, 1), 60),

    SMOKED_FISH_06(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.MIDNIGHT_CARP, 1), 60),

    SMOKED_FISH_07(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.HERRING, 1), 60),

    SMOKED_FISH_08(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.SQUID, 1), 60),

    SMOKED_FISH_09(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.GHOSTFISH, 1), 60),

    SMOKED_FISH_10(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.TILAPIA, 1), 60),

    SMOKED_FISH_11(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.DORADO, 1), 60),

    SMOKED_FISH_12(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.SHAD, 1), 60),

    SMOKED_FISH_13(GameObjectType.SMOKED_FISH, CraftingRecipeEnums.FISH_SMOKER_RECIPE, "A whole fish, smoked to perfection.",
        50, 1, new GameObject(GameObjectType.LIONFISH, 1), 60),
    // smoked fish - end


    // Furnace
    //  metal bar - start
    METAL_BAR_01(GameObjectType.COPPER_BAR, CraftingRecipeEnums.FURNACE_RECIPE, "Turns ore and coal into metal bars.",
            -1, 4, new GameObject(GameObjectType.COPPER_ORE, 5), 500),

    METAL_BAR_02(GameObjectType.IRON_BAR, CraftingRecipeEnums.FURNACE_RECIPE, "Turns ore and coal into metal bars.",
        -1, 4, new GameObject(GameObjectType.IRON_ORE, 5), 500),

    METAL_BAR_03(GameObjectType.GOLD_BAR, CraftingRecipeEnums.FURNACE_RECIPE, "Turns ore and coal into metal bars.",
        -1, 4, new GameObject(GameObjectType.GOLD_ORE, 5), 500),

    METAL_BAR_04(GameObjectType.IRIDIUM_BAR, CraftingRecipeEnums.FURNACE_RECIPE, "Turns ore and coal into metal bars.",
        -1, 4, new GameObject(GameObjectType.IRIDIUM_ORE, 5), 500)
    //  metal bar - end
    ;

    private final GameObjectType type;
    private final CraftingRecipeEnums device;
    private final String description;
    private final int energy;
    private final int processTime; // hours (each day is 14 hours)
    GameObject ingredient;
    private final int sellPrice;

    ArtisanGoodsType(GameObjectType type, CraftingRecipeEnums device, String description, int energy, int processTime,
                     GameObject ingredient, int sellPrice)
    {
        this.type = type;
        this.device = device;
        this.description = description;
        this.energy = energy;
        this.processTime = processTime;
        this.ingredient = ingredient;
        this.sellPrice = sellPrice;
    }

    public GameObjectType getType()
    {
        if (type == null)
        {
            Random rand = new Random();
            int random = rand.nextInt(4);

            if (random == 0)
            {
                return GameObjectType.COPPER_BAR;
            }

            if (random == 1)
            {
                return GameObjectType.IRIDIUM_BAR;
            }

            if (random == 2)
            {
                return GameObjectType.IRON_BAR;
            }

            return GameObjectType.GOLD_BAR;
        }

        return type;
    }

    public CraftingRecipeEnums getDevice()
    {
        return device;
    }

    public String getDescription()
    {
        return description;
    }

    public int getEnergy(GameObjectType ingredient)
    {
        return energy;
    }

    public int getProcessTime()
    {
        return processTime;
    }

    public GameObject getIngredient()
    {
        return ingredient;
    }

    public int getSellPrice()
    {
        return sellPrice;
    }

    public boolean isEdible()
    {
        return energy != -1;
    }

    public static ArrayList<ArtisanGoodsType> getPossibleProducts(CraftingRecipeEnums recipe)
    {
        ArrayList<ArtisanGoodsType> products = new ArrayList<>();

        for (ArtisanGoodsType type : ArtisanGoodsType.values())
        {
            if (type.getDevice() == recipe)
            {
                products.add(type);
            }
        }

        return products;
    }
}
