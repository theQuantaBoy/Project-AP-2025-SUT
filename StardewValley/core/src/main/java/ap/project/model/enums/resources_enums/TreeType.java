package ap.project.model.enums.resources_enums;

import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.Season;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public enum TreeType
{
    APRICOT_TREE(GameObjectType.APRICOT_TREE, "Apricot Tree", TreeSeedType.Apricot_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Apricot, 1, 59, true, 38, List.of(Season.Spring),
        List.of("objects/farming/trees/apricot/Apricot_Stage_1.png",
            "objects/farming/trees/apricot/Apricot_Stage_2.png",
            "objects/farming/trees/apricot/Apricot_Stage_3.png",
            "objects/farming/trees/apricot/Apricot_Stage_4.png"),
        List.of("objects/farming/trees/apricot/Apricot_Stage_5_spring.png",
            "objects/farming/trees/apricot/Apricot_Stage_5_summer.png",
            "objects/farming/trees/apricot/Apricot_Stage_5_fall.png",
            "objects/farming/trees/apricot/Apricot_Stage_5_winter.png"),
        "objects/farming/trees/apricot/Apricot_Stage_5_Fruit.png"),

    CHERRY_TREE(GameObjectType.CHERRY_TREE, "Cherry Tree", TreeSeedType.Cherry_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Cherry, 1, 80, true, 38, List.of(Season.Spring),
        List.of("objects/farming/trees/cherry/Cherry_Stage_1.png",
            "objects/farming/trees/cherry/Cherry_Stage_2.png",
            "objects/farming/trees/cherry/Cherry_Stage_3.png",
            "objects/farming/trees/cherry/Cherry_Stage_4.png"),
        List.of("objects/farming/trees/cherry/Cherry_Stage_5_spring.png",
            "objects/farming/trees/cherry/Cherry_Stage_5_summer.png",
            "objects/farming/trees/cherry/Cherry_Stage_5_fall.png",
            "objects/farming/trees/cherry/Cherry_Stage_5_winter.png"),
        "objects/farming/trees/cherry/Cherry_Stage_5_Fruit.png"),

    BANANA_TREE(GameObjectType.BANANA_TREE, "Banana Tree", TreeSeedType.Banana_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Banana, 1, 150, true, 75, List.of(Season.Summer),
        List.of("objects/farming/trees/banana/Banana_Stage_1.png",
            "objects/farming/trees/banana/Banana_Stage_2.png",
            "objects/farming/trees/banana/Banana_Stage_3.png",
            "objects/farming/trees/banana/Banana_Stage_4.png"),
        List.of("objects/farming/trees/banana/Banana_Stage_5_spring.png",
            "objects/farming/trees/banana/Banana_Stage_5_summer.png",
            "objects/farming/trees/banana/Banana_Stage_5_fall.png",
            "objects/farming/trees/banana/Banana_Stage_5_winter.png"),
        "objects/farming/trees/banana/Banana_Stage_5_Fruit.png"),

    MANGO_TREE(GameObjectType.MANGO_TREE, "Mango Tree", TreeSeedType.Mango_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Mango, 1, 130, true, 100, List.of(Season.Summer),
        List.of("objects/farming/trees/mango/Mango_Stage_1.png",
            "objects/farming/trees/mango/Mango_Stage_2.png",
            "objects/farming/trees/mango/Mango_Stage_3.png",
            "objects/farming/trees/mango/Mango_Stage_4.png"),
        List.of("objects/farming/trees/mango/Mango_Stage_5_spring.png",
            "objects/farming/trees/mango/Mango_Stage_5_summer.png",
            "objects/farming/trees/mango/Mango_Stage_5_fall.png",
            "objects/farming/trees/mango/Mango_Stage_5_winter.png"),
        "objects/farming/trees/mango/Mango_Stage_5_Fruit.png"),

    ORANGE_TREE(GameObjectType.ORANGE_TREE, "Orange Tree", TreeSeedType.Orange_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Orange, 1, 100, true, 38, List.of(Season.Summer),
        List.of("objects/farming/trees/orange/Orange_Stage_1.png",
            "objects/farming/trees/orange/Orange_Stage_2.png",
            "objects/farming/trees/orange/Orange_Stage_3.png",
            "objects/farming/trees/orange/Orange_Stage_4.png"),
        List.of("objects/farming/trees/orange/Orange_Stage_5_spring.png",
            "objects/farming/trees/orange/Orange_Stage_5_summer.png",
            "objects/farming/trees/orange/Orange_Stage_5_fall.png",
            "objects/farming/trees/orange/Orange_Stage_5_winter.png"),
        "objects/farming/trees/orange/Orange_Stage_5_Fruit.png"),

    PEACH_TREE(GameObjectType.PEACH_TREE, "Peach Tree", TreeSeedType.Peach_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Peach, 1, 140, true, 38, List.of(Season.Summer),
        List.of("objects/farming/trees/peach/Peach_Stage_1.png",
            "objects/farming/trees/peach/Peach_Stage_2.png",
            "objects/farming/trees/peach/Peach_Stage_3.png",
            "objects/farming/trees/peach/Peach_Stage_4.png"),
        List.of("objects/farming/trees/peach/Peach_Stage_5_spring.png",
            "objects/farming/trees/peach/Peach_Stage_5_summer.png",
            "objects/farming/trees/peach/Peach_Stage_5_fall.png",
            "objects/farming/trees/peach/Peach_Stage_5_winter.png"),
        "objects/farming/trees/peach/Peach_Stage_5_Fruit.png"),

    APPLE_TREE(GameObjectType.APPLE_TREE, "Apple Tree", TreeSeedType.Apple_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Apple, 1, 100, true, 38, List.of(Season.Fall),
        List.of("objects/farming/trees/apple/Apple_Stage_1.png",
            "objects/farming/trees/apple/Apple_Stage_2.png",
            "objects/farming/trees/apple/Apple_Stage_3.png",
            "objects/farming/trees/apple/Apple_Stage_4.png"),
        List.of("objects/farming/trees/apple/Apple_Stage_5_spring.png",
            "objects/farming/trees/apple/Apple_Stage_5_summer.png",
            "objects/farming/trees/apple/Apple_Stage_5_fall.png",
            "objects/farming/trees/apple/Apple_Stage_5_winter.png"),
        "objects/farming/trees/apple/Apple_Stage_5_Fruit.png"),

    POMEGRANATE_TREE(GameObjectType.POMEGRANATE_TREE,  "Pomegranate Tree",  TreeSeedType.Pomegranate_Sapling, List.of(7, 7, 7, 7), 28, FruitType.Pomegranate, 1, 140, true, 38, List.of(Season.Fall),
        List.of("objects/farming/trees/pomegranate/Pomegranate_Stage_1.png",
            "objects/farming/trees/pomegranate/Pomegranate_Stage_2.png",
            "objects/farming/trees/pomegranate/Pomegranate_Stage_3.png",
            "objects/farming/trees/pomegranate/Pomegranate_Stage_4.png"),
        List.of("objects/farming/trees/pomegranate/Pomegranate_Stage_5_spring.png",
            "objects/farming/trees/pomegranate/Pomegranate_Stage_5_summer.png",
            "objects/farming/trees/pomegranate/Pomegranate_Stage_5_fall.png",
            "objects/farming/trees/pomegranate/Pomegranate_Stage_5_winter.png"),
        "objects/farming/trees/pomegranate/Pomegranate_Stage_5_Fruit.png"),

    OAK_TREE(GameObjectType.OAK_TREE, "Oak Tree", TreeSeedType.Acorns, List.of(7, 7, 7, 7), 28, FruitType.Oak_Resin, 7, 150, false, -1, List.of(Season.Spring, Season.Summer, Season.Fall, Season.Winter),
        List.of("objects/farming/trees/oak/Oak_Stage_1.png",
            "objects/farming/trees/oak/Oak_Stage_2.png",
            "objects/farming/trees/oak/Oak_Stage_3.png",
            "objects/farming/trees/oak/Oak_Stage_4.png"),
        List.of("objects/farming/trees/oak/Oak_Stage_5_spring.png",
            "objects/farming/trees/oak/Oak_Stage_5_summer.png",
            "objects/farming/trees/oak/Oak_Stage_5_fall.png",
            "objects/farming/trees/oak/Oak_Stage_5_winter.png"),
        "objects/farming/trees/oak/Oak_stump_Spring.png"),

    MAPLE_TREE(GameObjectType.MAPLE_TREE, "Maple Tree", TreeSeedType.Maple_Seeds, List.of(7, 7, 7, 7), 28, FruitType.Maple_Syrup, 9, 200, false, -1, List.of(Season.Spring, Season.Summer, Season.Fall, Season.Winter),
        List.of("objects/farming/trees/maple/Maple_Stage_1.png",
            "objects/farming/trees/maple/Maple_Stage_2.png",
            "objects/farming/trees/maple/Maple_Stage_3.png",
            "objects/farming/trees/maple/Maple_Stage_4.png"),
        List.of("objects/farming/trees/maple/Maple_Stage_5_spring.png",
            "objects/farming/trees/maple/Maple_Stage_5_summer.png",
            "objects/farming/trees/maple/Maple_Stage_5_fall.png",
            "objects/farming/trees/maple/Maple_Stage_5_winter.png"),
        "objects/farming/trees/maple/Maple_stump_Spring.png"),

    PINE_TREE(GameObjectType.PINE_TREE, "Pine Tree", TreeSeedType.Pine_Cones, List.of(7, 7, 7, 7), 28, FruitType.Pine_Tar, 5, 100, false, -1, List.of(Season.Spring, Season.Summer, Season.Fall, Season.Winter),
        List.of("objects/farming/trees/pine/Pine_Stage_1.png",
            "objects/farming/trees/pine/Pine_Stage_2.png",
            "objects/farming/trees/pine/Pine_Stage_3.png",
            "objects/farming/trees/pine/Pine_Stage_4.png"),
        List.of("objects/farming/trees/pine/Pine_Stage_5_spring.png",
            "objects/farming/trees/pine/Pine_Stage_5_summer.png",
            "objects/farming/trees/pine/Pine_Stage_5_fall.png",
            "objects/farming/trees/pine/Pine_Stage_5_winter.png"),
        "objects/farming/trees/pine/Pine_stump_Spring.png"),

    MAHOGANY_TREE(GameObjectType.MAHOGANY_TREE, "Mahogany Tree", TreeSeedType.Mahogany_Seeds, List.of(7, 7, 7, 7), 28, FruitType.Sap, 1, 2, true, -1, List.of(Season.Spring, Season.Summer, Season.Fall, Season.Winter),
        List.of("objects/farming/trees/mahogany/Mahogany_Stage_1.png",
            "objects/farming/trees/mahogany/Mahogany_Stage_2.png",
            "objects/farming/trees/mahogany/Mahogany_Stage_3.png",
            "objects/farming/trees/mahogany/Mahogany_Stage_4.png"),
        List.of("objects/farming/trees/mahogany/Mahogany_Stage_5_spring.png",
            "objects/farming/trees/mahogany/Mahogany_Stage_5_summer.png",
            "objects/farming/trees/mahogany/Mahogany_Stage_5_fall.png",
            "objects/farming/trees/mahogany/Mahogany_Stage_5_winter.png"),
        "objects/farming/trees/mahogany/Mahogany_stump_Spring.png"),

    MUSHROOM_TREE(GameObjectType.MUSHROOM_TREE, "Mushroom Tree", TreeSeedType.Mushroom_Tree_Seeds, List.of(7, 7, 7, 7), 28, FruitType.Common_Mushroom, 1, 40, true, 38, List.of(Season.Spring, Season.Summer, Season.Fall, Season.Winter),
        List.of("objects/farming/trees/mushroom/MushroomTree_Stage_1.png",
            "objects/farming/trees/mushroom/MushroomTree_Stage_2.png",
            "objects/farming/trees/mushroom/MushroomTree_Stage_3.png",
            "objects/farming/trees/mushroom/MushroomTree_Stage_4.png"),
        List.of("objects/farming/trees/mushroom/MushroomTree_Stage_5_spring.png",
            "objects/farming/trees/mushroom/MushroomTree_Stage_5_summer.png",
            "objects/farming/trees/mushroom/MushroomTree_Stage_5_fall.png",
            "objects/farming/trees/mushroom/MushroomTree_Stage_5_winter.png"),
        "objects/farming/trees/mushroom/Mushroom_stump.png"),

    MYSTIC_TREE(GameObjectType.MYSTIC_TREE, "Mystic Tree", TreeSeedType.Mystic_Tree_Seed, List.of(7, 7, 7, 7), 28, FruitType.Mystic_Syrup, 7, 1000, true, 500, List.of(Season.Spring, Season.Summer, Season.Fall, Season.Winter),
        List.of("objects/farming/trees/mystic/Mystic_Tree_Stage_1.png",
            "objects/farming/trees/mystic/Mystic_Tree_Stage_2.png",
            "objects/farming/trees/mystic/Mystic_Tree_Stage_3.png",
            "objects/farming/trees/mystic/Mystic_Tree_Stage_4.png"),
        List.of("objects/farming/trees/mystic/Mystic_Tree_Stage_5_spring.png",
            "objects/farming/trees/mystic/Mystic_Tree_Stage_5_summer.png",
            "objects/farming/trees/mystic/Mystic_Tree_Stage_5_fall.png",
            "objects/farming/trees/mystic/Mystic_Tree_Stage_5_winter.png"),
        "objects/farming/trees/mystic/Mystic_Tree_Stump.png");
    ;

    private final GameObjectType type;
    private final String name;
    private final TreeSeedType source;
    private final List<Integer> stages;
    private final int totalHarvestTime;
    private final FruitType fruit;
    private final int fruitHarvestCycle;
    private final int fruitBaseSellPrice;
    private final boolean isEdible;
    private final int energy;
    private final List<Season> seasons;
    private final List<String> stagePaths;
    private final List<String> seasonPaths;
    private final String withProductPath;

    private ArrayList<Texture> stageTextures;
    private ArrayList<Texture> seasonTextures;
    private Texture withProductTexture;

    TreeType(GameObjectType type, String name, TreeSeedType source, List<Integer> stages, int totalHarvestTime, FruitType fruit,
             int fruitHarvestCycle, int fruitBaseSellPrice, boolean isEdible, int energy, List<Season> seasons, List<String> stagePaths,
             List<String> seasonPaths, String withProductPath)
    {
        this.type = type;
        this.name = name;
        this.source = source;
        this.stages = stages;
        this.totalHarvestTime = totalHarvestTime;
        this.fruit = fruit;
        this.fruitHarvestCycle = fruitHarvestCycle;
        this.fruitBaseSellPrice = fruitBaseSellPrice;
        this.isEdible = isEdible;
        this.energy = energy;
        this.seasons = seasons;
        this.stagePaths = new ArrayList<>(stagePaths);
        this.seasonPaths = new ArrayList<>(seasonPaths);
        this.withProductPath = withProductPath;

        stageTextures = new ArrayList<>();
        seasonTextures = new ArrayList<>();
    }

    public List<String> getSeasonPaths()
    {
        return seasonPaths;
    }

    public String getWithProductPath()
    {
        return withProductPath;
    }

    public void addStage(Texture stage)
    {
        stageTextures.add(stage);
    }

    public List<String> getStagePaths()
    {
        return stagePaths;
    }

    public void addSeasonTexture(Texture season)
    {
        seasonTextures.add(season);
    }

    public void setWithProductTexture(Texture withProductTexture)
    {
        this.withProductTexture = withProductTexture;
    }

    public ArrayList<Texture> getSeasonTextures()
    {
        return seasonTextures;
    }

    public Texture getWithProductTexture()
    {
        return withProductTexture;
    }

    public ArrayList<Texture> getStageTextures()
    {
        return stageTextures;
    }

    public TreeSeedType getSource()
    {
        return source;
    }

    public List<Integer> getStages()
    {
        return stages;
    }

    public int getTotalHarvestTime()
    {
        return totalHarvestTime;
    }

    public FruitType getFruit()
    {
        return fruit;
    }

    public int getFruitHarvestCycle()
    {
        return fruitHarvestCycle;
    }

    public int getFruitBaseSellPrice()
    {
        return fruitBaseSellPrice;
    }

    public boolean isEdible()
    {
        return isEdible;
    }

    public int getEnergy()
    {
        return energy;
    }

    public List<Season> getSeasons()
    {
        return seasons;
    }

    public String getName()
    {
        return name;
    }

    public String getCraftInfo()
    {
        StringBuilder output = new StringBuilder();
        output.append("Name: ").append(name).append("\n");
        output.append("Source: ").append(source.getType()).append("\n");
        output.append("Fruit: ").append(fruit.getType()).append("\n");
        output.append("Fruit Harvest Cycle: ").append(fruitHarvestCycle).append("\n");

        output.append("Stages: ");
        for (int i = 0; i < getStages().size(); i++)
        {
            output.append(getStages().get(i)).append(i == getStages().size() - 1 ? "\n" : " - ");
        }

        output.append("Total Harvest Time: ").append(totalHarvestTime).append("\n");
        output.append("Fruit Base Sell Price: ").append(fruitBaseSellPrice == -1 ? "-" : fruitBaseSellPrice).append("\n");
        output.append("Is Fruit Edible: ").append(isEdible ? "TRUE" : "FALSE").append("\n");
        output.append("Fruit Base Energy: ").append(energy == -1 ? "-" : energy).append("\n");

        output.append("Season(s): ");
        for (int i = 0; i < getSeasons().size(); i++)
        {
            output.append(getSeasons().get(i).getName()).append(i == getSeasons().size() - 1 ? "\n" : ", ");
        }

        return output.toString().trim();
    }

    public static TreeType getTreeType(String name)
    {
        for (TreeType type : TreeType.values())
        {
            if (type.getName().equalsIgnoreCase(name))
            {
                return type;
            }
        }
        return null;
    }

    public static TreeType getTreeBySeed(GameObjectType seedType)
    {
        for (TreeType type : TreeType.values())
        {
            if (type.source.getType().equals(seedType))
            {
                return type;
            }
        }
        return null;
    }

    public static TreeType getTreeByFruit(FruitType fruitType)
    {
        if (fruitType == null)
        {
            return null;
        }

        for (TreeType type : TreeType.values())
        {
            if (type.getFruit().getType().equals(fruitType.getType()))
            {
                return type;
            }
        }

        return null;
    }
}
