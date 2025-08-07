package ap.project.network.shared.DTO;

import ap.project.model.enums.CharacterType;
import ap.project.model.enums.Gender;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.enums.building_enums.KitchenRecipe;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.network.shared.Mapper.Mapper;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class PlayerDTO
{
    public UserDTO userDTO;
    public FarmDTO farmDTO;

    public CharacterType characterType;
    public float positionX;
    public float positionY;

    public Gender gender;

    public float energy;
    public float maxEnergy;
    public boolean isMaxEnergySet;
    public boolean fainted;

    public BackPackDTO backPackDTO;

    public SkillDTO farmingSkill;
    public SkillDTO miningSkill;
    public SkillDTO foragingSkill;
    public SkillDTO fishingSKill;

    public boolean newMessage;

    public ToolDTO currentToolDTO;
    public GameObjectDTO currentObject;
    public double money;

    public ArrayList<CraftingRecipeEnums> craftingRecipes = new ArrayList<>();
    public ArrayList<KitchenRecipe> kitchenRecipes = new ArrayList<>();
    public ArrayList<GameObjectDTO> refrigerator = new ArrayList<>();

    public boolean isInFarm;
    public boolean isInCity;
    public boolean isInGreenHouse;
    public boolean isInHome;
    public boolean isInZeidiesFarm;
    public boolean isInZeidiesHome;
    public boolean isInShop;

    public String apperance;

    public MapTypes mapType;

    public PlayerDTO() {}

    public PlayerDTO(Player player)
    {
        this.userDTO = new UserDTO(player.getUser());
        this.farmDTO = new FarmDTO(player.getFarm());

        this.characterType = player.getCharacter().getType();
        this.positionX = player.getPosition().x;
        this.positionY = player.getPosition().y;

        this.gender = player.getGender();

        this.energy = player.getEnergy();
        this.maxEnergy = player.getMaxEnergy();
        this.isMaxEnergySet = player.isMaxEnergySet();
        this.fainted = player.isFainted();

        this.backPackDTO = new BackPackDTO(player.getCurrentBackPack());

        this.craftingRecipes = player.getCraftingRecipes();
        this.kitchenRecipes = player.getCookingRecipes();

        for (GameObject object : player.getRefrigerator())
        {
            this.refrigerator.add(Mapper.toDTO(object));
        }

        this.fishingSKill = new SkillDTO(player.getFishingSkill());
        this.miningSkill = new SkillDTO(player.getMiningSkill());
        this.foragingSkill = new SkillDTO(player.getForagingSkill());
        this.farmingSkill = new SkillDTO(player.getFarmingSkill());

        this.newMessage = player.isNewMessage();

        this.currentToolDTO = new ToolDTO(player.getCurrentTool());
        this.currentObject = Mapper.toDTO(player.getCurrentObject());
        this.money = player.getMoney();

        this.isInFarm = player.isInFarm();
        this.isInCity = player.isInCity();
        this.isInGreenHouse = player.isInGreenHouse();
        this.isInHome = player.isInHome();
        this.isInZeidiesFarm = player.isInZeidiesFarm();
        this.isInZeidiesHome = player.isInZeidiesHome();
        this.isInShop = player.isInShop();

        this.apperance = player.getApperance();

        this.mapType = player.getMapType();
    }
}
