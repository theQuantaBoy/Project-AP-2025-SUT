package ap.project.model.enums.building_enums;

import ap.project.model.enums.GameObjectType;
import ap.project.model.building.CraftingItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum CraftingRecipeEnums
{
    CHERRY_BOMB_RECIPE(GameObjectType.CHERRY_BOMB_RECIPE, GameObjectType.CHERRY_BOMB,
            "Destroys everything in a 3-tile radius.", new HashMap<>() {{
                put(GameObjectType.COPPER_ORE, 4);
                put(GameObjectType.COAL, 1);
    }}, 50, false, CraftingItem.ItemType.ONE_TIME),

    BOMB_RECIPE(GameObjectType.BOMB_RECIPE, GameObjectType.BOMB,
            "Destroys everything in a 5-tile radius.", new HashMap<>() {{
                put(GameObjectType.IRON_ORE, 4);
                put(GameObjectType.COAL, 1);
    }}, 50, false, CraftingItem.ItemType.ONE_TIME),

    MEGA_BOMB_RECIPE(GameObjectType.MEGA_BOMB_RECIPE, GameObjectType.MEGA_BOMB,
            "Destroys everything in a 7-tile radius.", new HashMap<>() {{
                put(GameObjectType.GOLD_ORE, 4);
                put(GameObjectType.COAL, 1);
    }}, 50, false, CraftingItem.ItemType.ONE_TIME),

    SPRINKLER_RECIPE(GameObjectType.SPRINKLER_RECIPE, GameObjectType.SPRINKLER,
            "Waters the 4 adjacent tiles.", new HashMap<>() {{
                put(GameObjectType.COPPER_BAR, 1);
                put(GameObjectType.IRON_BAR, 1);
    }}, 0, false, CraftingItem.ItemType.PERMANENT),

    QUALITY_SPRINKLER_RECIPE(GameObjectType.QUALITY_SPRINKLER_RECIPE, GameObjectType.QUALITY_SPRINKLER,
            "Waters the 8 surrounding tiles.", new HashMap<>() {{
                put(GameObjectType.IRON_BAR, 1);
                put(GameObjectType.GOLD_BAR, 1);
    }}, 0, false, CraftingItem.ItemType.PERMANENT),

    IRIDIUM_SPRINKLER_RECIPE(GameObjectType.IRIDIUM_SPRINKLER_RECIPE, GameObjectType.IRIDIUM_SPRINKLER,
            "Waters 24 nearby tiles.", new HashMap<>() {{
                put(GameObjectType.GOLD_BAR, 1);
                put(GameObjectType.IRIDIUM_BAR, 1);
    }}, 0, false, CraftingItem.ItemType.PERMANENT),

    CHARCOAL_KILN_RECIPE(GameObjectType.CHARCOAL_KILN_RECIPE, GameObjectType.CHARCOAL_KLIN,
            "Turns 10 wood into 1 charcoal.", new HashMap<>() {{
                put(GameObjectType.WOOD, 20);
                put(GameObjectType.COPPER_BAR, 2);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    FURNACE_RECIPE(GameObjectType.FURNACE_RECIPE, GameObjectType.FURNACE,
            "Turns ores and coal into bars.", new HashMap<>() {{
                put(GameObjectType.COPPER_BAR, 20);
                put(GameObjectType.STONE, 25);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    SCARECROW_RECIPE(GameObjectType.SCARECROW_RECIPE, GameObjectType.SCARECROW,
            "Protects crops from crows within 8 tiles.", new HashMap<>() {{
                put(GameObjectType.WOOD, 50);
                put(GameObjectType.COAL, 1);
                put(GameObjectType.FIBER, 20);
    }}, 0, true, CraftingItem.ItemType.PERMANENT),

    DELUXE_SCARECROW_RECIPE(GameObjectType.DELUXE_SCARECROW_RECIPE, GameObjectType.DELUXE_SCARECROW,
            "Protects crops from crows within 12 tiles.", new HashMap<>() {{
                put(GameObjectType.WOOD, 50);
                put(GameObjectType.COAL, 1);
                put(GameObjectType.FIBER, 20);
                put(GameObjectType.IRIDIUM_ORE, 1);
    }}, 0, true, CraftingItem.ItemType.PERMANENT),

    BEE_HOUSE_RECIPE(GameObjectType.BEE_HOUSE_RECIPE, GameObjectType.BEE_HOUSE,
            "Produces honey when placed on the farm.", new HashMap<>() {{
                put(GameObjectType.WOOD, 40);
                put(GameObjectType.COAL, 8);
                put(GameObjectType.IRON_BAR, 1);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    CHEESE_PRESS_RECIPE(GameObjectType.CHEESE_PRESS_RECIPE, GameObjectType.CHEESE_PRESS,
            "Turns milk into cheese.", new HashMap<>() {{
                put(GameObjectType.WOOD, 45);
                put(GameObjectType.STONE, 45);
                put(GameObjectType.COPPER_BAR, 1);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    KEG_RECIPE(GameObjectType.KEG_RECIPE, GameObjectType.KEG,
            "Turns fruit and vegetables into drinks.", new HashMap<>() {{
                put(GameObjectType.WOOD, 30);
                put(GameObjectType.COPPER_BAR, 1);
                put(GameObjectType.IRON_BAR, 1);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    LOOM_RECIPE(GameObjectType.LOOM_RECIPE, GameObjectType.LOOM,
            "Turns wool into cloth.", new HashMap<>() {{
                put(GameObjectType.WOOD, 60);
                put(GameObjectType.FIBER, 30);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    MAYONNAISE_MACHINE_RECIPE(GameObjectType.MAYONNAISE_MACHINE_RECIPE, GameObjectType.MAYONNAISE_MACHINE,
            "Turns eggs into mayonnaise.", new HashMap<>() {{
                put(GameObjectType.WOOD, 15);
                put(GameObjectType.STONE, 15);
                put(GameObjectType.COPPER_BAR, 1);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    OIL_MAKER_RECIPE(GameObjectType.OIL_MAKER_RECIPE, GameObjectType.OIL_MAKER,
            "Turns truffles into oil.", new HashMap<>() {{
                put(GameObjectType.WOOD, 100);
                put(GameObjectType.GOLD_BAR, 1);
                put(GameObjectType.IRON_BAR, 1);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    PRESERVES_JAR_RECIPE(GameObjectType.PRESERVES_JAR_RECIPE, GameObjectType.PRESERVES_JAR,
            "Turns vegetables into pickles and fruit into jam.", new HashMap<>() {{
                put(GameObjectType.WOOD, 50);
                put(GameObjectType.STONE, 40);
                put(GameObjectType.COAL, 8);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    DEHYDRATOR_RECIPE(GameObjectType.DEHYDRATOR_RECIPE, GameObjectType.DEHYDRATOR,
            "Dries fruit or mushrooms.", new HashMap<>() {{
                put(GameObjectType.WOOD, 30);
                put(GameObjectType.STONE, 20);
                put(GameObjectType.FIBER, 30);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    GRASS_STARTER_RECIPE(GameObjectType.GRASS_STARTER_RECIPE, GameObjectType.GRAPE_STARTER,
            "Makes grass grow where it's placed.", new HashMap<>() {{
                put(GameObjectType.WOOD, 1);
                put(GameObjectType.FIBER, 1);
    }}, 0, false, CraftingItem.ItemType.PERIODIC),

    FISH_SMOKER_RECIPE(GameObjectType.FISH_SMOKER_RECIPE, GameObjectType.FISH_SMOKER,
            "Smokes any fish using one coal while keeping its quality.", new HashMap<>() {{
                put(GameObjectType.WOOD, 50);
                put(GameObjectType.IRON_BAR, 3);
                put(GameObjectType.COAL, 10);
    }}, 0, true, CraftingItem.ItemType.PERIODIC),

    MYSTIC_TREE_SEED_RECIPE(GameObjectType.MYSTIC_TREE_SEED_RECIPE, GameObjectType.MYSTIC_TREE_SEED,
            "Can be planted to grow a mystic tree.", new HashMap<>() {{
                put(GameObjectType.ACORN, 5);
                put(GameObjectType.MAPLE_SEED, 5);
                put(GameObjectType.PINE_CONE, 5);
                put(GameObjectType.MAHOGANY_SEED, 5);
    }}, 100, false, CraftingItem.ItemType.PERIODIC);

    ;

    private final GameObjectType type;
    private final GameObjectType product;
    private final String description;
    private final HashMap<GameObjectType, Integer> ingredients;
    private final int price;
    private final boolean isTall;
    private final CraftingItem.ItemType itemType;

    CraftingRecipeEnums(GameObjectType type, GameObjectType product, String description,
                        HashMap<GameObjectType, Integer> ingredients, int price, boolean isTall,  CraftingItem.ItemType itemType)
    {
        this.type = type;
        this.product = product;
        this.description = description;
        this.ingredients = ingredients;
        this.price = price;
        this.isTall = isTall;
        this.itemType = itemType;
    }

    public boolean isTall()
    {
        return isTall;
    }

    public GameObjectType getType()
    {
        return type;
    }

    public GameObjectType getProduct()
    {
        return product;
    }

    public String getDescription()
    {
        return description;
    }

    public HashMap<GameObjectType, Integer> getIngredients()
    {
        return ingredients;
    }

    public int getPrice()
    {
        return price;
    }

    public String getInfo()
    {
        StringBuilder output = new StringBuilder();

        output.append("Name: ").append(type).append('\n');
        output.append("Product: ").append(product).append('\n');
        output.append("Description: ").append(description).append('\n');

        output.append("------------------\n");
        output.append("Ingredients: \n");
        for (Map.Entry<GameObjectType, Integer> entry : ingredients.entrySet())
        {
            output.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        return output.toString();
    }

    public static CraftingRecipeEnums getRecipeFromItemName(String name)
    {
        for (CraftingRecipeEnums recipe : CraftingRecipeEnums.values())
        {
            if (name.equalsIgnoreCase(recipe.getProduct().toString()))
            {
                return recipe;
            }
        }
        return null;
    }

    public CraftingItem.ItemType getItemType()
    {
        return itemType;
    }

    public ArrayList<ArtisanGoodsType> getArtisanTypes()
    {
        ArrayList<ArtisanGoodsType> artisanTypes = new ArrayList<>();
        for (ArtisanGoodsType type : ArtisanGoodsType.values())
        {
            if (type.getDevice() == this)
            {
                artisanTypes.add(type);
            }
        }
        return artisanTypes;
    }
}
