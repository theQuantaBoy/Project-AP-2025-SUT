package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.App.Result;
import ap.project.model.App.User;
import ap.project.model.animal.Animal;
import ap.project.model.animal.AnimalBuilding;
import ap.project.model.animal.Fish;
import ap.project.model.enums.*;
import ap.project.model.shops.Shop;
import ap.project.model.tools.*;
import ap.project.model.enums.building_enums.ArtisanGoodsType;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.enums.building_enums.KitchenRecipe;
import ap.project.model.enums.tool_enums.ToolType;
import ap.project.model.player_data.FriendshipData;
import ap.project.model.player_data.FriendshipWithNpcData;
import ap.project.model.player_data.Skill;
import ap.project.model.player_data.Trade;
import ap.project.model.resources.Plant;
import ap.project.screen.WorldScreen;
import ap.project.view.CityMenu;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

import static ap.project.model.game.Map.TILE_SIZE;

public class Player {
    private PlayerCharacter character;

    private final User user;
    private Farm farm;
    private final Cabin cabin;
    private GreenHouse greenHouse;

    private Gender gender;
    private Point location = null;
    private Map currentMap = null;

    private float energy;
    private float maxEnergy = 200f;
    private boolean fainted = false;

    private Skill farmingSkill = new Skill(SkillType.Farming);
    private Skill miningSkill = new Skill(SkillType.Mining);
    private Skill foragingSkill = new Skill(SkillType.Foraging);
    private Skill fishingSkill = new Skill(SkillType.Fishing);

    BackPack currentBackPack = new BackPack();
    private HashMap<Player, FriendshipData> friendships = new HashMap<>(); //TODO: might change to nested hashmap
    private ArrayList<Trade> sentTrades = new ArrayList<>();
    private ArrayList<Trade> receivedTrades = new ArrayList<>();
    private ArrayList<Trade> archiveTrades = new ArrayList<>();
    private List<Skill> skills = new LinkedList<>();

    private boolean newMessage;

    private ArrayList<Gift> newGifts = new ArrayList<>();
    private ArrayList<Gift> archiveGifts = new ArrayList<>();
    private ArrayList<Gift> givenGifts = new ArrayList<>();

    private FriendshipWithNpcData SebastianFriendship;
    private FriendshipWithNpcData AbigailFriendship;
    private FriendshipWithNpcData HarveyFriendship;
    private FriendshipWithNpcData LiaFriendship;
    private FriendshipWithNpcData RobinFriendship;

    private HashMap<Player, GameObjectType> purposeList = new HashMap<>();
    private Player zeidy;

    private Tool currentTool;
    private double money;

    private ArrayList<AnimalBuilding> animalBuildings = new ArrayList<>();

    private ArrayList<CraftingRecipeEnums> craftingRecipes = new ArrayList<>();

    private ArrayList<KitchenRecipe> cookingRecipes = new ArrayList<>(
            Arrays.asList(KitchenRecipe.FRIED_EGG,
                    KitchenRecipe.BAKED_FISH,
                    KitchenRecipe.SALAD));
    private ArrayList<GameObject> refrigerator = new ArrayList<>();

    public static ArrayList<String> appearences = new ArrayList<>(List.of("\uD83D\uDC31", "\uD83E\uDD8A", "\uD83D\uDC3C", "\uD83E\uDD81"));

    private boolean isInFarm = true;
    private boolean isInCity = false;
    private boolean isInGreenHouse = false;
    private boolean isInHome = false;
    private boolean isInZeidiesFarm = false;
    private boolean isInZeidiesHome = false;
    private boolean isInShop = false;

    private final String apperance;
    private boolean shouldBeSkipped = false;

    private NPC currentNPC = null;
    private ArrayList<GameObject> npcGiftsObject = new ArrayList<>();
    private ArrayList<NPC> npcGiftsNPC = new ArrayList<>();

    private ArrayList<ArtisanGood> artisanGoods = new ArrayList<>();

    private ShopType currentShop;

    private ArrayList<Fish> fishes = new ArrayList<>();

    private MapTypes mapType;

    public void setMapType(MapTypes mapType)
    {
        this.mapType = mapType;
    }

    public MapTypes getMapType()
    {
        return mapType;
    }

    public void setFarm(Farm farm)
    {
        this.farm = farm;
    }

    public Player(User user, MapTypes currentMapType, int number) {
        this.user = user;
        this.mapType = currentMapType;
        this.cabin = new Cabin();
        this.greenHouse = new GreenHouse();
//        this.currentMap = this.farm;
        this.energy = 200f;
        this.fainted = false;
        this.money = 0;
        addTool(new Axe());
        addTool(new Hoe());
        addTool(new Pickaxe());
        addTool(new WateringCan());
        addTool(new Seythe());
        addToInventory(GameObjectType.MILK, 2);

        this.zeidy = null;
//        this.location = farm.getStartingPoint();
        this.newMessage = false;
        this.apperance = appearences.get(number);
        this.skills.add(farmingSkill); this.skills.add(miningSkill);
        this.skills.add(foragingSkill); this.skills.add(fishingSkill);

//        Point spawn = currentMap.getStartingPoint();
//        Vector2 spawnPoint = currentMap.tileToWorld(currentMap.getTile(spawn.getX(), spawn.getY()));
//
//        this.character = new PlayerCharacter(CharacterType.ABIGAIL, spawnPoint, user.getNickname());
    }

    public void spawn()
    {
        this.currentMap = this.farm;
        Point spawn = currentMap.getStartingPoint();
        Vector2 spawnPoint = currentMap.tileToWorld(currentMap.getTile(spawn.getX(), spawn.getY()));

        this.character = new PlayerCharacter(CharacterType.ABIGAIL, spawnPoint, user.getNickname());
    }

    public Player(User user, Farm farm, int number) {
        this.user = user;
        this.farm = farm;
        this.cabin = new Cabin();
        this.greenHouse = new GreenHouse();
        this.currentMap = this.farm;
        this.energy = 200f;
        this.fainted = false;
        this.money = 0;
        this.addToInventory(new Axe());
        this.addToInventory(new Hoe());
        this.addToInventory(new Pickaxe());
        this.addToInventory(new WateringCan());
        this.addToInventory(new Seythe());
        this.addToInventory(new TrashCan());

        this.zeidy = null;
//        this.location = farm.getStartingPoint();
        setLocation(farm.getStartingPoint());
        this.newMessage = false;
        this.apperance = appearences.get(number);

        this.character = new PlayerCharacter(CharacterType.ABIGAIL, new Vector2(60 * 24, 60 * 24),
            user.getNickname());
        this.skills.add(farmingSkill); this.skills.add(miningSkill);
        this.skills.add(foragingSkill); this.skills.add(fishingSkill);
    }

    public PlayerCharacter getCharacter()
    {
        return character;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void addFriendship(Player player, FriendshipData data)
    {
        this.friendships.put(player, data);
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public void increaseEnergy(float energy)
    {
        if (energy != -1)
        {
           this.energy += energy;
        }

        // TODO: add faint check mechanism
    }


    public float getMaxEnergy() {
        return this.maxEnergy;
    }

    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public boolean isFainted() {
        return fainted;
    }

    public void setFainted(boolean fainted) {
        this.fainted = fainted;
    }

    public Skill getFarmingSkill() {
        return farmingSkill;
    }

    public Skill getMiningSkill() {
        return miningSkill;
    }

    public Skill getForagingSkill() {
        return foragingSkill;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public Skill getFishingSkill() {
        return fishingSkill;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public void setCurrentTool(Tool currentTool) {
        this.currentTool = currentTool;
    }

    public double getMoney() {
        if (this.zeidy == null) {
            return money;
        }
        return money + zeidy.money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void increaseMoney(double money) {
        this.money += money;
    }

    public ArrayList<GameObject> getInventory() {
        return new ArrayList<>(currentBackPack.getNonEmptyItems());
    }

    public HashMap<Player, FriendshipData> getFriendships() {
        return friendships;
    }

    public ArrayList<Trade> getSentTrades() {
        return sentTrades;
    }

    public ArrayList<Trade> getReceivedTrades() {
        return receivedTrades;
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    public ArrayList<Trade> getArchiveTrades() {
        return archiveTrades;
    }

    public ArrayList<Gift> getNewGifts() {
        return newGifts;
    }

    public ArrayList<Gift> getArchiveGifts() {
        return archiveGifts;
    }

    public ArrayList<Gift> getGivenGifts() {
        return givenGifts;
    }

    public HashMap<Player, GameObjectType> getPurposeList() {
        return purposeList;
    }

    public Player getZeidy() {
        return zeidy;
    }

    public void setZeidy(Player zeidy) {
        this.zeidy = zeidy;
    }

    public User getUser() {
        return user;
    }

    public String getNickName() {
        return user.getNickname();
    }

    @Override
    public String toString() {
        return user.getNickname();
    }

    public Point getLocation()
    {
//        return location;
        Vector2 pos = character.getPosition();
        return currentMap.worldToTile(pos.x, pos.y);
    }

    public Map getCurrentMap()
    {
        return currentMap;
    }

    public void setLocation(Point location)
    {
//        this.location = location;

        character.setPosition(currentMap.tileToWorld(currentMap.getTile(location.getX(), location.getY())));
//
//        if (isInCity)
//        {
//            int index = App.getCurrentGame().getPlayerIndex();
//            City city = App.getCurrentGame().getCity();
//            city.getPlayerPoints()[index] = location;
//        }
//        if (isInCity)
//        {
//            int index = App.getCurrentGame().getPlayerIndex();
//            City city = App.getCurrentGame().getCity();
//            city.getPlayerPoints()[index] = location;
//        }
    }

    public void setCurrentMap(Map currentMap)
    {
        this.currentMap = currentMap;
    }

    public GameObject findObjectType(Enum<?> type)
    {
        for (GameObject obj : currentBackPack.getSlots())
        {
            Enum<?> inventoryItemType = obj.getObjectType();

            if (inventoryItemType.equals(type))
            {
                return obj;
            }
        }
        return null;
    }

    public Trade getTradeById (int id) {
        for (Trade trade : this.getReceivedTrades()) {
            if (trade.getId() == id) {
                return trade;
            }
        }
        return null;
    }

    public GameObject getItemInInventory(GameObjectType objectType) {
        for (GameObject object : getInventory()) {
            if (object.getObjectType().equals(objectType)) {
                return object;
            }
        }
        return null;
    }

    public void removeFromInventory(GameObject object) {
        currentBackPack.removeItem(object);
    }

    public Gift getGiftById(int id) {
        for (Gift gift : this.archiveGifts) {
            if (gift.getId() == id) {
                return gift;
            }
        }
        return null;
    }

    public void checkEnergy() {
        if (this.energy < 1) {
            this.setFainted(true);
        }
    }

    public float getEnergyPercentage() {
        return this.energy / this.maxEnergy;
    }

    public Tool getTool(ToolType type)
    {
        for (GameObject object : currentBackPack.getSlots())
        {
            if (object instanceof Tool tool)
            {
                if (tool.getToolType().equals(type))
                {
                    return tool;
                }
            }
        }
        return null;
    }

    public void addToInventory(GameObject object) {
        // First try to stack with existing items
        for (GameObject item : getInventory()) {
            if (item.getObjectType().equals(object.getObjectType())) {
                item.addNumber(object.getNumber());
                return;
            }
        }

        // If no stack available, add to empty slot
        if (currentBackPack.hasEmptySlot()) {
            currentBackPack.addItem(object);
        }
    }



    public ArrayList<CraftingRecipeEnums> getCraftingRecipes()
    {
        return craftingRecipes;
    }

    public boolean hasEnoughInInventory(GameObjectType objectType, int amount) {
        GameObject item = getItemInInventory(objectType);
        return item != null && item.getNumber() >= amount;
    }

    public int howManyInInventory(GameObjectType objectType) {
        GameObject item = getItemInInventory(objectType);
        return item != null ? item.getNumber() : 0;
    }

    public void removeAmountFromInventory(GameObjectType objectType, int amount) {
        for (int i = 0; i < currentBackPack.getSlots().size(); i++) {
            GameObject item = currentBackPack.getSlots().get(i);
            if (item != null && item.getObjectType().equals(objectType)) {
                item.addNumber(-amount);
                if (item.getNumber() <= 0) {
                    currentBackPack.removeItem(i);
                }
                return;
            }
        }
    }

    public boolean inventoryHasCapacity() {
        return currentBackPack.hasEmptySlot();
    }

    public BackPack getCurrentBackPack()
    {
        return currentBackPack;
    }

    public void addToInventory(GameObjectType objectType, int amount) {
        // First try to stack with existing items
        for (GameObject item : getInventory()) {
            if (item.getObjectType().equals(objectType)) {
                item.addNumber(amount);
                return;
            }
        }

        // If no stack available, add to empty slot
        if (currentBackPack.hasEmptySlot()) {
            currentBackPack.addItem(new GameObject(objectType, amount));
        }
    }

    public void addTool(Tool tool) {
        currentBackPack.getTools().add(tool);
    }

    public int getInventoryCapacity() {
        return currentBackPack.getEmptySlotCount();
    }

    public int getTotalInventoryCapacity() {
        return currentBackPack.getCapacity();
    }

    public FriendshipWithNpcData getSebastianFriendship() {
        return SebastianFriendship;
    }

    public void setSebastianFriendship(FriendshipWithNpcData sebastianFriendship) {
        SebastianFriendship = sebastianFriendship;
    }

    public FriendshipWithNpcData getAbigailFriendship() {
        return AbigailFriendship;
    }

    public void setAbigailFriendship(FriendshipWithNpcData abigailFriendship) {
        AbigailFriendship = abigailFriendship;
    }

    public FriendshipWithNpcData getHarveyFriendship() {
        return HarveyFriendship;
    }

    public void setHarveyFriendship(FriendshipWithNpcData harveyFriendship) {
        HarveyFriendship = harveyFriendship;
    }

    public FriendshipWithNpcData getLiaFriendship() {
        return LiaFriendship;
    }

    public void setLiaFriendship(FriendshipWithNpcData liaFriendship) {
        LiaFriendship = liaFriendship;
    }

    public FriendshipWithNpcData getRobinFriendship() {
        return RobinFriendship;
    }

    public void setRobinFriendship(FriendshipWithNpcData robinFriendship) {
        RobinFriendship = robinFriendship;
    }

    public ArrayList<GameObject> getRefrigerator()
    {
        return refrigerator;
    }

    public ArrayList<KitchenRecipe> getCookingRecipes()
    {
        return cookingRecipes;
    }

    public boolean isNear(Point otherLocation)
    {

        Point location = getLocation();
        int dx = Math.abs(location.getX() - otherLocation.getX());
        int dy = Math.abs(location.getY() - otherLocation.getY());

        return (dx <= 2 && dy <= 2) && !(dx == 0 && dy == 0);
    }

    public boolean isNearOrOn(Point otherLocation)
    {

        Point location = getLocation();
        int dx = Math.abs(location.getX() - otherLocation.getX());
        int dy = Math.abs(location.getY() - otherLocation.getY());

        return (dx <= 2 && dy <= 2);
    }

    public GameObject getFromRefrigerator (GameObjectType type)
    {
        for (GameObject thing : refrigerator)
        {
            if (thing.getObjectType().equals(type))
            {
                return thing;
            }
        }
        return null;
    }

    public int howManyInRefrigerator(GameObjectType type)
    {
        for (GameObject thing : refrigerator)
        {
            if (thing.getObjectType().equals(type))
            {
                return thing.getNumber();
            }
        }
        return 0;
    }

    public void removeAmountFromRefrigerator(GameObjectType type, int amount)
    {
        for (GameObject thing : refrigerator)
        {
            if (thing.getObjectType().equals(type))
            {
                thing.addNumber(-1 * amount);
                if (thing.getNumber() <= 0)
                {
                    refrigerator.remove(thing);
                }
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Player player)) return false;
        return user.getUsername().equals(((Player) o).getUser().getUsername());
    }

    public Farm getFarm()
    {
        return farm;
    }

    public Cabin getCabin()
    {
        return cabin;
    }

    public GreenHouse getGreenHouse()
    {
        return greenHouse;
    }

    public void goToFarm()
    {
        if (isInCity)
        {
            City city = App.getCurrentGame().getCity();
            city.getPlayerPoints()[App.getCurrentGame().getPlayerIndex()] = null;
        }

        this.currentMap = this.farm;

        if (isInCity)
        {
            setLocation(farm.getExitPoint());
        } else if (isInHome)
        {
            setLocation(farm.getHomePoint());
        } else if (isInGreenHouse)
        {
            setLocation(farm.getGreenhousePoint());
        } else
        {
            setLocation(farm.getStartingPoint());
        }

        this.isInCity = false;
        this.isInGreenHouse = false;
        this.isInFarm = true;
        this.isInHome = false;
        this.isInZeidiesFarm = false;
        this.isInZeidiesHome = false;

        WorldScreen.getInstance().updateGameInfo();
    }

    public void goToShop(Shop shop)
    {
        this.isInCity = false;
        this.isInShop = true;
        this.currentMap = shop;
        setLocation(shop.getStartingPoint());
        WorldScreen.getInstance().updateGameInfo();
    }

    public void goToZeidyFarm()
    {
        if (isInCity)
        {
            City city = App.getCurrentGame().getCity();
            city.getPlayerPoints()[App.getCurrentGame().getPlayerIndex()] = null;
        }

        this.isInCity = false;
        this.isInGreenHouse = false;
        this.isInFarm = false;
        this.isInHome = false;
        this.isInZeidiesFarm = true;
        this.isInZeidiesHome = false;
        this.currentMap = zeidy.getFarm();
//        this.location = zeidy.getFarm().getStartingPoint();
        setLocation(zeidy.getFarm().getStartingPoint());
    }

    public void goToGreenHouse()
    {
        this.isInHome = false;
        this.isInZeidiesFarm = false;
        this.isInZeidiesHome = false;
        this.isInFarm = false;
        this.isInGreenHouse = true;
        this.currentMap = this.greenHouse;
//        this.location = greenHouse.getStartingPoint();
        setLocation(greenHouse.getStartingPoint());
        WorldScreen.getInstance().updateGameInfo();
    }

    public void goToCity()
    {
        this.isInHome = false;
        this.isInZeidiesFarm = false;
        this.isInZeidiesHome = false;
        this.isInShop =  false;
        City city = App.getCurrentGame().getCity();
        this.isInFarm = false;
        this.isInCity = true;
        this.currentMap = this.user.getCurrentGame().getCity();
//        this.location = this.user.getCurrentGame().getCity().findFreeStartingPoint();
//        setLocation(this.user.getCurrentGame().getCity().findFreeStartingPoint());
        setLocation(this.user.getCurrentGame().getCity().getStartingPoint());
//        city.getPlayerPoints()[App.getCurrentGame().getPlayerIndex()] = this.location;
        WorldScreen.getInstance().updateGameInfo();
    }

    public void goToCity(Point door)
    {
        this.isInHome = false;
        this.isInZeidiesFarm = false;
        this.isInZeidiesHome = false;
        this.isInShop =  false;
        this.isInFarm = false;
        this.isInCity = true;
        this.currentMap = this.user.getCurrentGame().getCity();
        setLocation(door);
        WorldScreen.getInstance().updateGameInfo();
    }

    public boolean isInShop()
    {
        return isInShop;
    }

    public void setEnergyToMax()
    {
        this.energy = maxEnergy;
    }

    public boolean canAffordGreenhouse()
    {
        return (money >= GreenHouse.getMoneyCost() && howManyInInventory(GameObjectType.WOOD) >= GreenHouse.getWoodCost());
    }

    public ArrayList<AnimalBuilding> getAnimalBuildings()
    {
        return farm.getAnimalBuildings();
    }

    public void addAnimalBuilding(AnimalBuilding animalBuilding) {
        animalBuildings.add(animalBuilding);
    }

    public void decreaseMoney(int amount)
    {
        money -= amount;
    }

    public boolean hasEnoughEnergy(int required)
    {
        if (energy == -1)
        {
            return true;
        }

        return energy > required;
    }

    public ArrayList<Tile> getFarmPlants()
    {
        return farm.getAllPlantTiles();
    }

    public ArrayList<Tile> getGreenhousePlants()
    {
        return greenHouse.getAllPlantTiles();
    }

    public boolean isInFarm()
    {
        return isInFarm;
    }

    public boolean isInCity()
    {
        return isInCity;
    }

    public boolean isInGreenHouse()
    {
        return isInGreenHouse;
    }

    public String getApperance()
    {
        return apperance;
    }

    public ArrayList<Plant> getAllPlants()
    {
        ArrayList<Plant> allPlants = new ArrayList<>();

        for (Tile tile : farm.getAllPlantTiles())
        {
            if (tile.getObject() != null && tile.getObject() instanceof Plant)
            {
                allPlants.add((Plant) tile.getObject());
            }
        }

        for (Tile tile : greenHouse.getAllPlantTiles())
        {
            if (tile.getObject() != null && tile.getObject() instanceof Plant)
            {
                allPlants.add((Plant) tile.getObject());
            }
        }

        return allPlants;
    }

    public Result newMessages() {
        Player currentPlayer = App.getCurrentGame().getCurrentPlayer();
        if (currentPlayer.isNewMessage()) {
            currentPlayer.setNewMessage(false);
            return new Result(true, "you have some new messages! check them");
        } else {
            return new Result(false, "you don't have any new message");
        }
    }

    public Result newGifts() {
        Player currentPlayer = App.getCurrentGame().getCurrentPlayer();
        if (!currentPlayer.getNewGifts().isEmpty()) {
            getNewGifts().clear();
            return new Result(true, "you have received new gifts");
        } else {
            return new Result(false, "you didn't receive gifts loser");
        }
    }

    public boolean shouldBeSkipped()
    {
        return shouldBeSkipped;
    }

    public void setShouldBeSkipped(boolean shouldBeSkipped)
    {
        this.shouldBeSkipped = shouldBeSkipped;
    }

    public void resetEnergy()
    {
        if (energy != -1) // not unlimited
        {
            // TODO

            energy = maxEnergy;
            if (fainted)
            {
                energy = (int) (0.75 * maxEnergy);
                fainted = false;
            }
        }
    }

    public void faint()
    {
        fainted = true;
        shouldBeSkipped = true;
    }

    public boolean isInZeidiesFarm()
    {
        return isInZeidiesFarm;
    }

    public boolean isShouldBeSkipped()
    {
        return shouldBeSkipped;
    }

    public void goHome()
    {
        this.isInFarm = false;
        this.isInCity = false;
        this.isInGreenHouse = false;
        this.isInZeidiesFarm = false;
        this.isInZeidiesHome = false;

        this.isInHome = true;
        this.currentMap = cabin;

        setLocation(cabin.getStartingPoint());
        WorldScreen.getInstance().updateGameInfo();

        App.setCurrentMenu(Menu.HomeMenu);
    }

    public void unFaint()
    {
        this.fainted = false;
        shouldBeSkipped = false;
    }

    public boolean isInHome()
    {
        return isInHome;
    }

    public void getAttackedByCrows()
    {
        ArrayList<Tile> farmPlants = getFarmPlants();
        int size = farmPlants.size();

        for (int i = 0; i < (size / 16); i++)
        {
            if (Math.random() < 0.25)
            {
                Random rand = new Random();
                Tile tile = farmPlants.get(rand.nextInt(size));
                if (tile.hasPlants())
                {
                    Plant plant = (Plant) tile.getObject();
                    if (!tile.isImmuneFromCrows())
                    {
                        plant.getAttacked();
                    }
                }
            }
        }
    }

    public FriendshipWithNpcData getNpcFriendship(NPC npc)
    {
        return switch (npc.getNpcDetails())
        {
            case NpcDetails.Lia -> LiaFriendship;
            case NpcDetails.Abigail -> AbigailFriendship;
            case NpcDetails.Harvey -> HarveyFriendship;
            case NpcDetails.Robin -> RobinFriendship;
            case NpcDetails.Sebastian -> SebastianFriendship;
            default -> null;
        };
    }

    public ArrayList<FriendshipWithNpcData> getAllNpcFriendships()
    {
        ArrayList<FriendshipWithNpcData> friendships = new ArrayList<>();

        friendships.add(LiaFriendship);
        friendships.add(AbigailFriendship);
        friendships.add(HarveyFriendship);
        friendships.add(RobinFriendship);
        friendships.add(SebastianFriendship);

        return friendships;
    }

    public NPC getCurrentNPC()
    {
        return currentNPC;
    }

    public void setCurrentNPC(NPC currentNPC)
    {
        this.currentNPC = currentNPC;
    }

    public void addNpcGiftObject(GameObject object)
    {
        npcGiftsObject.add(object);
    }

    public void addNpcGiftNPC(NPC npc)
    {
        npcGiftsNPC.add(npc);
    }

    public ArrayList<GameObject> getNpcGiftsObject()
    {
        return npcGiftsObject;
    }

    public ArrayList<NPC> getNpcGiftsNPC()
    {
        return npcGiftsNPC;
    }

    public boolean recieveNpcGifts()
    {
        boolean recieved = false;
        while (inventoryHasCapacity() && npcGiftsObject.size() > 0)
        {
            GameObject gift = npcGiftsObject.get(0);
            NPC npc = npcGiftsNPC.get(0);
            addToInventory(gift);
            npcGiftsObject.remove(0);
            npcGiftsNPC.remove(0);
            CityMenu.println("You just recieved a " + gift.getObjectType().toString() + " from " + npc.getName() + ".");
            recieved = true;
        }
        return recieved;
    }

    public ArtisanGood getArtisan(ArtisanGoodsType type)
    {
        for (ArtisanGood good : artisanGoods)
        {
            if (good.getArtisanType() == type)
            {
                return good;
            }
        }
        return null;
    }

    public boolean hasFishingPole()
    {
        for (GameObject object : currentBackPack.getSlots())
        {
            if (object instanceof FishingPole)
            {
                return true;
            }
        }
        return false;
    }

    public ShopType getCurrentShop()
    {
        return currentShop;
    }

    public void setCurrentShop(ShopType currentShop)
    {
        this.currentShop = currentShop;
    }

    public ArrayList<Animal> getAnimals()
    {
        ArrayList<Animal> animals = new ArrayList<>();
        for (AnimalBuilding animalBuilding : farm.getAnimalBuildings())
        {
            for (Animal animal : animalBuilding.getAnimals())
            {
                animals.add(animal);
            }
        }
        return animals;
    }

    public Animal findAnimal(String name)
    {
        for (Animal animal : getAnimals())
        {
            if (animal.getName().equalsIgnoreCase(name))
            {
                return animal;
            }
        }
        return null;
    }

    public boolean validAnimalName(String name)
    {
        for (Animal animal : getAnimals())
        {
            if (animal.getName().equalsIgnoreCase(name))
            {
                return false;
            }
        }
        return true;
    }

    public boolean isNearAnimal(Animal animal)
    {
        for (Animal a : getAnimals())
        {
            if (a.getName().equalsIgnoreCase(animal.getName()))
            {
                Tile tile = a.getTile();

                if (tile == null) // TODO: technically should be changed later
                {
                    return false;
                }

                Point p = tile.getPoint();
                ArrayList<Point> neighbors = App.getCurrentGame().getCurrentPlayer().getFarm().getNeighbors(
                        App.getCurrentGame().getCurrentPlayer().getLocation());

                for (Point neighbor : neighbors)
                {
                    if (neighbor.equals(p))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public ArrayList<Fish> getFishes()
    {
        return fishes;
    }
}
