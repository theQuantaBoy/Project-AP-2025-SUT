package ap.project.network.shared.Mapper;

import ap.project.model.App.User;
import ap.project.model.building.CraftingItem;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.enums.resources_enums.CropType;
import ap.project.model.enums.resources_enums.ForagingCropType;
import ap.project.model.enums.resources_enums.TreeType;
import ap.project.model.enums.tool_enums.FishingPoleLevel;
import ap.project.model.game.*;
import ap.project.model.player_data.Skill;
import ap.project.model.resources.*;
import ap.project.model.tools.*;
import ap.project.network.shared.DTO.*;
import com.badlogic.gdx.math.Vector2;

public class Mapper
{
    public static User fromDTO(UserDTO userDTO)
    {
        if (!userDTO.initialized) return null;

        User user = new User (userDTO.username, userDTO.password, userDTO.nickname, userDTO.email,
            userDTO.gender, userDTO.question, userDTO.answer,
            userDTO.numberOfGames, userDTO.hashID, userDTO.characterChoice, userDTO.mapChoice);

        return user;
    }

    public static GameObject fromDTO(GameObjectDTO gameObjectDTO)
    {
        if (!gameObjectDTO.initialized)
        {
            return null;
        }

        if (gameObjectDTO instanceof PlantDTO)
        {
            if (gameObjectDTO instanceof TreeDTO)
            {
                TreeDTO treeDTO = (TreeDTO)gameObjectDTO;
                Tree tree = new Tree((TreeType) treeDTO.type, treeDTO.point, treeDTO.playerIndex, treeDTO.isGrowFaster);

                tree.setHasStarted(treeDTO.hasStarted);
                tree.setLastWatered(treeDTO.lastWatered);
                tree.setCurrentStageDay(treeDTO.currentStageDay);
                tree.setLastHarvested(treeDTO.lastHarvested);

                tree.setHasHarvested(treeDTO.hasHarvested);
                tree.setHarvestWaitTime(treeDTO.harvestWaitTime);

                tree.setInGreenhouse(treeDTO.isInGreenhouse);

                tree.setObjectType(treeDTO.objectType);
                tree.setNumber(treeDTO.number);
                tree.setPrice(treeDTO.price);

                return tree;
            }

            if (gameObjectDTO instanceof CropDTO)
            {
                if (gameObjectDTO instanceof GiantCropDTO)
                {
                    GiantCropDTO giantCropDTO = (GiantCropDTO) gameObjectDTO;
                    GiantCrop giantCrop = new GiantCrop((CropType) giantCropDTO.type, giantCropDTO.rootPoint, giantCropDTO.playerIndex, giantCropDTO.isGrowFaster);

                    giantCrop.setHasStarted(giantCropDTO.hasStarted);
                    giantCrop.setLastWatered(giantCropDTO.lastWatered);
                    giantCrop.setCurrentStageDay(giantCropDTO.currentStageDay);
                    giantCrop.setLastHarvested(giantCropDTO.lastHarvested);

                    giantCrop.setHasHarvested(giantCropDTO.hasHarvested);
                    giantCrop.setHarvestWaitTime(giantCropDTO.harvestWaitTime);

                    giantCrop.setInGreenhouse(giantCropDTO.isInGreenhouse);

                    giantCrop.setObjectType(giantCropDTO.objectType);
                    giantCrop.setNumber(giantCropDTO.number);
                    giantCrop.setPrice(giantCropDTO.price);

                    return giantCrop;
                }

                CropDTO cropDTO = (CropDTO) gameObjectDTO;
                Crop crop = new Crop((CropType) cropDTO.type, cropDTO.point, cropDTO.playerIndex, cropDTO.isGrowFaster);

                crop.setHasStarted(cropDTO.hasStarted);
                crop.setLastWatered(cropDTO.lastWatered);
                crop.setCurrentStageDay(cropDTO.currentStageDay);
                crop.setLastHarvested(cropDTO.lastHarvested);

                crop.setHasHarvested(cropDTO.hasHarvested);
                crop.setHarvestWaitTime(cropDTO.harvestWaitTime);

                crop.setInGreenhouse(cropDTO.isInGreenhouse);

                crop.setObjectType(cropDTO.objectType);
                crop.setNumber(cropDTO.number);
                crop.setPrice(cropDTO.price);

                return crop;
            }
        }

        if (gameObjectDTO instanceof ForagingCropDTO)
        {
            ForagingCropDTO foragingCropDTO = (ForagingCropDTO) gameObjectDTO;
            ForagingCrop foragingCrop = new ForagingCrop(foragingCropDTO.foragingCropType);

            foragingCrop.setObjectType(foragingCropDTO.objectType);
            foragingCrop.setNumber(foragingCropDTO.number);
            foragingCrop.setPrice(foragingCropDTO.price);

            return foragingCrop;
        }

        if (gameObjectDTO instanceof ForagingMineralDTO)
        {
            ForagingMineralDTO foragingMineralDTO = (ForagingMineralDTO) gameObjectDTO;
            ForagingMineral foragingMineral =  new ForagingMineral(foragingMineralDTO.foragingMineralType);

            foragingMineral.setObjectType(foragingMineralDTO.objectType);
            foragingMineral.setNumber(foragingMineralDTO.number);
            foragingMineral.setPrice(foragingMineralDTO.price);

            return foragingMineral;
        }

        if (gameObjectDTO instanceof ForagingSeedDTO)
        {
            ForagingSeedDTO foragingSeedDTO = (ForagingSeedDTO) gameObjectDTO;
            ForagingSeed foragingSeed = new ForagingSeed(foragingSeedDTO.foragingSeedType);

            foragingSeed.setObjectType(foragingSeedDTO.objectType);
            foragingSeed.setNumber(foragingSeedDTO.number);
            foragingSeed.setPrice(foragingSeedDTO.price);

            return foragingSeed;
        }

        if (gameObjectDTO instanceof ForagingTreeDTO)
        {
            ForagingTreeDTO foragingTreeDTO = (ForagingTreeDTO) gameObjectDTO;
            ForagingTree foragingTree = new ForagingTree(foragingTreeDTO.foragingTreeType);

            foragingTree.setObjectType(foragingTreeDTO.objectType);
            foragingTree.setNumber(foragingTreeDTO.number);
            foragingTree.setPrice(foragingTreeDTO.price);

            return foragingTree;
        }

        if (gameObjectDTO instanceof ResourceDTO)
        {
            ResourceDTO resourceDTO = (ResourceDTO) gameObjectDTO;
            Resource resource = new Resource(resourceDTO.resourceItem);

            resource.setObjectType(resourceDTO.objectType);
            resource.setNumber(resourceDTO.number);
            resource.setPrice(resourceDTO.price);

            return resource;
        }

        if (gameObjectDTO instanceof ToolDTO)
        {
            return fromDTO((ToolDTO) gameObjectDTO);
        }

        if (gameObjectDTO instanceof CraftingItemDTO)
        {
            CraftingItemDTO craftingItemDTO = (CraftingItemDTO) gameObjectDTO;
            CraftingItem craftingItem = new CraftingItem(craftingItemDTO.craftingType, craftingItemDTO.point);

            craftingItem.setArtisanType(craftingItemDTO.artisanType);
            craftingItem.setWorking(craftingItemDTO.isWorking);

            craftingItem.setStartDay(craftingItemDTO.startDay);
            craftingItem.setStartHour(craftingItemDTO.startHour);

            craftingItem.setNeededDays(craftingItemDTO.neededDays);
            craftingItem.setNeededHours(craftingItemDTO.neededHours);

            for (GameObjectDTO o : craftingItemDTO.craftingIngredients)
            {
                craftingItem.getCraftingIngredients().add(fromDTO(o));
            }

            craftingItem.setObjectType(craftingItemDTO.objectType);
            craftingItem.setNumber(craftingItemDTO.number);
            craftingItem.setPrice(craftingItemDTO.price);

            return craftingItem;
        }

        GameObject gameObject = new GameObject();

        gameObject.setObjectType(gameObjectDTO.objectType);
        gameObject.setNumber(gameObjectDTO.number);
        gameObject.setPrice(gameObjectDTO.price);

        return gameObject;
    }

    public static Tile fromDTO(TileDTO tileDTO)
    {
        if (!tileDTO.initialized) return null;

        Tile tile = new Tile(tileDTO.texture, tileDTO.point);
        tile.setHitByThunder(tileDTO.hitByThunder);
        tile.setObject(fromDTO(tileDTO.gameObjectDTO));
        tile.setPloughed(tileDTO.isPloughed);
        tile.setFertilized(tileDTO.isFertilized);
        tile.setWateringChance(tileDTO.wateringChance);
        tile.setGrowFaster(tileDTO.growFaster);
        tile.setImmuneFromCrows(tileDTO.isImmuneFromCrows);
        tile.setRandomForaging(tileDTO.isRandomForaging);
        tile.setInCity(tileDTO.isInCity);
        tile.setTypeName(tileDTO.typeName);
        tile.setShouldBeWateredAutomatically(tileDTO.shouldBeWateredAutomatically);

        return tile;
    }

    public static GameObjectDTO toDTO(GameObject gameObject)
    {
        if (gameObject == null) return new GameObjectDTO(gameObject);

        if (gameObject instanceof Plant)
        {
            if (gameObject instanceof Tree)
            {
                return new TreeDTO((Tree) gameObject);
            }

            if (gameObject instanceof Crop)
            {
                if (gameObject instanceof GiantCrop)
                {
                    return new GiantCropDTO((GiantCrop) gameObject);
                }

                return new CropDTO((Crop) gameObject);
            }
        }

        if (gameObject instanceof ForagingCrop)
        {
            return new ForagingCropDTO((ForagingCrop) gameObject);
        }

        if (gameObject instanceof ForagingMineral)
        {
            return new ForagingMineralDTO((ForagingMineral) gameObject);
        }

        if (gameObject instanceof ForagingSeed)
        {
            return new ForagingSeedDTO((ForagingSeed) gameObject);
        }

        if (gameObject instanceof ForagingTree)
        {
            return new ForagingTreeDTO((ForagingTree) gameObject);
        }

        if (gameObject instanceof Resource)
        {
            return new ResourceDTO((Resource) gameObject);
        }

        if (gameObject instanceof Tool)
        {
            return new ToolDTO((Tool) gameObject);
        }

        if (gameObject instanceof CraftingItem)
        {
            return new CraftingItemDTO((CraftingItem) gameObject);
        }

        return new GameObjectDTO(gameObject);
    }

    public static Tool fromDTO(ToolDTO toolDTO)
    {
        if (!toolDTO.initialized) return null;

        switch (toolDTO.type)
        {
            case Axe ->
            {
                Axe axe = new Axe(toolDTO.axeLevel);

                axe.setObjectType(toolDTO.objectType);
                axe.setNumber(toolDTO.number);
                axe.setPrice(toolDTO.price);

                return axe;
            }

            case Hoe ->
            {
                Hoe hoe = new Hoe(toolDTO.hoeLevel);

                hoe.setObjectType(toolDTO.objectType);
                hoe.setNumber(toolDTO.number);
                hoe.setPrice(toolDTO.price);

                return hoe;
            }

            case Shear ->
            {
                Shear shear = new Shear();

                shear.setObjectType(toolDTO.objectType);
                shear.setNumber(toolDTO.number);
                shear.setPrice(toolDTO.price);

                return shear;
            }

            case Scythe ->
            {
                Seythe seythe = new Seythe();

                seythe.setObjectType(toolDTO.objectType);
                seythe.setNumber(toolDTO.number);
                seythe.setPrice(toolDTO.price);

                return seythe;
            }

            case Pickaxe ->
            {
                Pickaxe pickaxe = new Pickaxe(toolDTO.pickaxeLevel);

                pickaxe.setObjectType(toolDTO.objectType);
                pickaxe.setNumber(toolDTO.number);
                pickaxe.setPrice(toolDTO.price);

                return pickaxe;
            }

            case MilkPail ->
            {
                MilkPail milkPail = new MilkPail();

                milkPail.setObjectType(toolDTO.objectType);
                milkPail.setNumber(toolDTO.number);
                milkPail.setPrice(toolDTO.price);

                return milkPail;
            }

            case WateringCan ->
            {
                WateringCan wateringCan = new WateringCan(toolDTO.wateringCanLevel);

                wateringCan.setObjectType(toolDTO.objectType);
                wateringCan.setNumber(toolDTO.number);
                wateringCan.setPrice(toolDTO.price);

                return wateringCan;
            }

            case FishingPole ->
            {
                FishingPole fishingPole = new FishingPole(toolDTO.fishingPoleLevel);

                fishingPole.setObjectType(toolDTO.objectType);
                fishingPole.setNumber(toolDTO.number);
                fishingPole.setPrice(toolDTO.price);

                return fishingPole;
            }

            case BackPack ->
            {
                BackPack backPack = new BackPack();

                backPack.setObjectType(toolDTO.objectType);
                backPack.setNumber(toolDTO.number);
                backPack.setPrice(toolDTO.price);

                return backPack;
            }

            case TrashCan ->
            {
                TrashCan trashCan = new TrashCan(toolDTO.trashCanLevel);

                trashCan.setObjectType(toolDTO.objectType);
                trashCan.setNumber(toolDTO.number);
                trashCan.setPrice(toolDTO.price);

                return trashCan;
            }
        };

        return null;
    }

    public static Skill fromDTO(SkillDTO skillDTO)
    {
        if (!skillDTO.initialized) return null;

        Skill skill = new Skill(skillDTO.type);
        skill.setUnit(skillDTO.unit);
        skill.setLevel(skillDTO.level);

        return skill;
    }

    public static Farm fromDTO(FarmDTO farmDTO)
    {
        Farm farm = new Farm(farmDTO.mapType, true);

        int height = farmDTO.height;
        int width = farmDTO.width;

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                farm.setTile(x, y, fromDTO(farmDTO.tiles[y][x]));
            }
        }

        for (Point p : farmDTO.tilesWithResources)
        {
            farm.getTilesWithResources().add(farm.getTile(p.getX(), p.getY()));
        }

        for (Point p : farmDTO.tilesWithForagingTrees)
        {
            farm.getTilesWithForagingTrees().add(farm.getTile(p.getX(), p.getY()));
        }

        for (Point p : farmDTO.tilesWithForagingItems)
        {
            farm.getTilesWithForagingItems().add(farm.getTile(p.getX(), p.getY()));
        }

        for (Point p : farmDTO.plantingTiles)
        {
            farm.getPlantingTiles().add(farm.getTile(p.getX(), p.getY()));
        }

        for (Point p : farmDTO.tilesWIthCraftingItems)
        {
            farm.getTilesWithCraftingItems().add(farm.getTile(p.getX(), p.getY()));
        }

        for (Point p : farmDTO.lightningTiles)
        {
            farm.getLightningTiles().add(farm.getTile(p.getX(), p.getY()));
        }

        for (Point p : farmDTO.hazardTiles)
        {
            farm.getHazardTiles().add(farm.getTile(p.getX(), p.getY()));
        }

        return farm;
    }

    public static BackPack fromDTO(BackPackDTO backPackDTO)
    {
        BackPack backPack = new BackPack(backPackDTO.level);

        for (GameObjectDTO o : backPackDTO.slots)
        {
            backPack.addItem(fromDTO(o));
        }

        for (GameObjectDTO o : backPackDTO.tools)
        {
            backPack.getTools().add((Tool) fromDTO(o));
        }

        return backPack;
    }

    public static Player fromDTO(PlayerDTO playerDTO)
    {
        Player player = new Player(fromDTO(playerDTO.userDTO));
        player.setFarm(fromDTO(playerDTO.farmDTO));

        player.spawn();
        player.getCharacter().setPosition(new Vector2(playerDTO.positionX, playerDTO.positionY));

        player.setGender(playerDTO.gender);

        player.setEnergy(playerDTO.energy);
        player.setMaxEnergy(playerDTO.maxEnergy);
        player.setMaxEnergySet(playerDTO.isMaxEnergySet);
        player.setFainted(playerDTO.fainted);

        player.setCurrentBackPack(fromDTO(playerDTO.backPackDTO));

        player.setFarmingSkill(fromDTO(playerDTO.farmingSkill));
        player.setMiningSkill(fromDTO(playerDTO.miningSkill));
        player.setForagingSkill(fromDTO(playerDTO.foragingSkill));
        player.setFishingSkill(fromDTO(playerDTO.fishingSKill));

        player.setNewMessage(playerDTO.newMessage);

        player.setCurrentTool(fromDTO(playerDTO.currentToolDTO));
        player.setCurrentObject(fromDTO(playerDTO.currentObject));
        player.setMoney(playerDTO.money);

        player.setCraftingRecipes(playerDTO.craftingRecipes);
        player.setCookingRecipes(playerDTO.kitchenRecipes);

        for (GameObjectDTO o : playerDTO.refrigerator)
        {
            player.getRefrigerator().add(fromDTO(o));
        }

        player.setInFarm(playerDTO.isInFarm);
        player.setInCity(playerDTO.isInCity);
        player.setInGreenHouse(playerDTO.isInGreenHouse);
        player.setInHome(playerDTO.isInHome);
        player.setInZeidiesFarm(playerDTO.isInZeidiesFarm);
        player.setInZeidiesHome(playerDTO.isInZeidiesHome);
        player.setInShop(playerDTO.isInShop);

        player.setApperance(playerDTO.apperance);
        player.setMapType(playerDTO.mapType);

        return player;
    }
}

