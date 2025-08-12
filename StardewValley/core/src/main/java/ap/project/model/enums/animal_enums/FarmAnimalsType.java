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

public enum FarmAnimalsType
{
    CHICKEN(GameObjectType.CHICKEN, "Chicken", 800,
        List.of(
            new GameObject(GameObjectType.EGG, 50),
            new GameObject(GameObjectType.LARGE_EGG, 95)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.COOP, FarmBuildingType.BIG_COOP, FarmBuildingType.DELUXE_COOP),
        "animal_sheets/chicken/Chicken_Sheet.atlas"),

    DUCK(GameObjectType.DUCK, "Duck", 1200,
        List.of(
            new GameObject(GameObjectType.DUCK_EGG, 95),
            new GameObject(GameObjectType.DUCK_FEATHER, 250)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.BIG_COOP, FarmBuildingType.DELUXE_COOP),
        "animal_sheets/duck/Duck_Sheet.atlas"),

    RABBIT(GameObjectType.RABBIT, "Rabbit", 8000,
        List.of(
            new GameObject(GameObjectType.WOOL, 340),
            new GameObject(GameObjectType.RABBITS_FOOT, 565)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.DELUXE_COOP),
        "animal_sheets/rabbit/Rabbit_Sheet.atlas"),

    DINOSAUR(GameObjectType.DINOSAUR, "Dinosaur", 14000,
        List.of(
            new GameObject(GameObjectType.DINOSAUR_EGG, 350)
        ),
        AnimalType.COOP, List.of(FarmBuildingType.BIG_COOP),
        "animal_sheets/dinosaur/Dinosaur_Sheet.atlas"),

    COW(GameObjectType.COW, "Cow", 1500,
        List.of(
            new GameObject(GameObjectType.MILK, 125),
            new GameObject(GameObjectType.LARGE_MILK, 190)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.BARN, FarmBuildingType.BIG_BARN, FarmBuildingType.DELUXE_BARN),
        "animal_sheets/cow/Cow_Sheet.atlas"),

    GOAT(GameObjectType.GOAT, "Goat", 4000,
        List.of(
            new GameObject(GameObjectType.GOAT_MILK, 225),
            new GameObject(GameObjectType.LARGE_GOAT_MILK, 345)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.BIG_BARN, FarmBuildingType.DELUXE_BARN),
        "animal_sheets/goat/Goat_Sheet.atlas"),

    SHEEP(GameObjectType.SHEEP, "Sheep", 8000,
        List.of(
            new GameObject(GameObjectType.WOOL, 340)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.DELUXE_BARN),
        "animal_sheets/sheep/Sheep_Sheet.atlas"),

    PIG(GameObjectType.PIG, "Pig", 16000,
        List.of(
            new GameObject(GameObjectType.TRUFFLE, 625)
        ),
        AnimalType.BARN, List.of(FarmBuildingType.DELUXE_BARN),
        "animal_sheets/pig/Pig_Sheet.atlas");

    // --- Fields ---
    private final GameObjectType type;
    private final String name;
    private final int purchaseCost;
    private final List<GameObject> products;
    private final AnimalType animalType;
    private final List<FarmBuildingType> buildings;
    private final String atlasPath;

    FarmAnimalsType(GameObjectType type, String name, int purchaseCost, List<GameObject> products,
                    AnimalType animalType, List<FarmBuildingType> buildings, String atlasPath) {
        this.type = type;
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.products = new ArrayList<>(products);
        this.animalType = animalType;
        this.buildings = buildings;

        this.atlasPath = atlasPath;
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

    public AnimalType getAnimalType()
    {
        return animalType;
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
