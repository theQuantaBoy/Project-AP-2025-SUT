package ap.project.model.enums.animal_enums;

import ap.project.model.enums.animal_enums.Direction;
import ap.project.model.enums.GameObjectType;
import ap.project.model.game.GameObject;
import ap.project.util.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public enum FarmAnimalsType {

    // --- WORKING EXAMPLE ---
    // The CHICKEN entry is fully configured with all its data and a working animation path.
    CHICKEN(GameObjectType.CHICKEN, "Chicken", 800,
        List.of(
            new GameObject(GameObjectType.EGG, 50),
            new GameObject(GameObjectType.LARGE_EGG, 95)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.COOP, FarmBuildingType.BIG_COOP, FarmBuildingType.DELUXE_COOP),
        "animals/chicken_spritesheet.png", 4, 7, "animal_sheets/chicken/Chicken_Sheet.atlas"),

    // --- PLACEHOLDER ENTRIES ---
    // The following animals use placeholder paths. You must replace them with your own spritesheet files.
    DUCK(GameObjectType.DUCK, "Duck", 1200,
        List.of(
            new GameObject(GameObjectType.DUCK_EGG, 95),
            new GameObject(GameObjectType.DUCK_FEATHER, 250)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.BIG_COOP, FarmBuildingType.DELUXE_COOP),
        "animals/duck_spritesheet.png", 4, 7, "animal_sheets/duck/Duck_Sheet.atlas"),

    RABBIT(GameObjectType.RABBIT, "Rabbit", 8000,
        List.of(
            new GameObject(GameObjectType.WOOL, 340),
            new GameObject(GameObjectType.RABBITS_FOOT, 565)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.DELUXE_COOP),
        "animals/rabbit_spritesheet.png", 4, 7, "animal_sheets/rabbit/Rabbit_Sheet.atlas"),

    DINOSAUR(GameObjectType.DINOSAUR, "Dinosaur", 14000,
        List.of(
            new GameObject(GameObjectType.DINOSAUR_EGG, 350)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.BIG_COOP),
        "animals/dinosaur_spritesheet.png", 4, 7, "animal_sheets/dinosaur/Dinosaur_Sheet.atlas"),

    COW(GameObjectType.COW, "Cow", 1500,
        List.of(
            new GameObject(GameObjectType.MILK, 125),
            new GameObject(GameObjectType.LARGE_MILK, 190)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.BARN, FarmBuildingType.BIG_BARN, FarmBuildingType.DELUXE_BARN),
        "animals/cow_spritesheet.png", 4, 7, "animal_sheets/cow/Cow_Sheet.atlas"),

    GOAT(GameObjectType.GOAT, "Goat", 4000,
        List.of(
            new GameObject(GameObjectType.GOAT_MILK, 225),
            new GameObject(GameObjectType.LARGE_GOAT_MILK, 345)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.BIG_BARN, FarmBuildingType.DELUXE_BARN),
        "animals/goat_spritesheet.png", 4, 7, "animal_sheets/goat/Goat_Sheet.atlas"),

    SHEEP(GameObjectType.SHEEP, "Sheep", 8000,
        List.of(
            new GameObject(GameObjectType.WOOL, 340)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.DELUXE_BARN),
        "animals/sheep_spritesheet.png", 4, 7, "animal_sheets/sheep/Sheep_Sheet.atlas"),

    PIG(GameObjectType.PIG, "Pig", 16000,
        List.of(
            new GameObject(GameObjectType.TRUFFLE, 625)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.DELUXE_BARN),
        "animals/pig_spritesheet.png", 4, 7, "animal_sheets/pig/Pig_Sheet.atlas");

    // --- Fields ---
    private final GameObjectType type;
    private final String name;
    private final int purchaseCost;
    private final List<GameObject> products;
    private final AnimalType animalType;
    private final List<FarmBuildingType> buildings;
    private final EnumMap<Direction, Animation<TextureRegion>> walkAnimations;
    private final Animation<TextureRegion> idleAnimation;
    private final String atlasPath;

    FarmAnimalsType(GameObjectType type, String name, int purchaseCost, List<GameObject> products,
                    AnimalType animalType, List<FarmBuildingType> buildings,
                    String sheetPath, int cols, int rows, String atlasPath) {
        this.type = type;
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.products = new ArrayList<>(products);
        this.animalType = animalType;
        this.buildings = buildings;

        // --- Animation Loading Logic ---
        Texture sheet;
        if (Gdx.files.internal(sheetPath).exists()) {
            sheet = new Texture(Gdx.files.internal(sheetPath));
        } else {
            Gdx.app.error("FarmAnimalsType", "Spritesheet not found: " + sheetPath + ". Using chicken as fallback.");
            sheet = new Texture(Gdx.files.internal("animals/chicken/Brown Chicken.png"));
        }

        float frameDuration = 0.2f;
        this.walkAnimations = new EnumMap<>(Direction.class);
        this.walkAnimations.put(Direction.DOWN, AnimationUtil.createAnimationFromRow(sheet, 0, cols, rows, frameDuration));
        this.walkAnimations.put(Direction.RIGHT, AnimationUtil.createAnimationFromRow(sheet, 1, cols, rows, frameDuration));
        this.walkAnimations.put(Direction.LEFT, AnimationUtil.createAnimationFromRow(sheet, 2, cols, rows, frameDuration));
        this.walkAnimations.put(Direction.UP, AnimationUtil.createAnimationFromRow(sheet, 3, cols, rows, frameDuration));
        this.idleAnimation = AnimationUtil.createAnimationFromRow(sheet, 4, cols, rows, 0.4f);
        this.atlasPath = atlasPath;
    }

    // --- Getters ---
    public Animation<TextureRegion> getWalkAnimation(Direction direction) {
        return walkAnimations.get(direction);
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return idleAnimation;
    }

    public String getName() {
        return name;
    }

    public int getPurchaseCost() {
        return purchaseCost;
    }

    public List<GameObject> getProducts() {
        return products;
    }

    public static FarmAnimalsType getFarmAnimalsType(String name) {

        for (FarmAnimalsType type : FarmAnimalsType.values()) {

            if (type.getName().equalsIgnoreCase(name)) {

                return type;

            }

        }

        return null;

    }

    public List<FarmBuildingType> getBuilding()

    {

        return buildings;

    }

    public List<FarmBuildingType> getBuildings() {
        return buildings;
    }

    public GameObjectType getType() {
        return type;
    }

    public String getAtlasPath()
    {
        return atlasPath;
    }

    public static FarmAnimalsType getAnimalType(GameObjectType type)
    {
        for (FarmAnimalsType animalType : FarmAnimalsType.values())
        {
            if (animalType.getType() == type)
            {
                return animalType;
            }
        }

        return null;
    }
}
