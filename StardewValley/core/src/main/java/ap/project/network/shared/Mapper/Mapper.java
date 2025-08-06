package ap.project.network.shared.Mapper;

import ap.project.model.App.User;
import ap.project.model.enums.tool_enums.FishingPoleLevel;
import ap.project.model.game.*;
import ap.project.model.player_data.Skill;
import ap.project.model.tools.*;
import ap.project.network.shared.DTO.*;

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
        if (!gameObjectDTO.initialized) return null;

        GameObject gameObject = new GameObject(gameObjectDTO.objectType, gameObjectDTO.number);
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

    public static Tool fromDTO(ToolDTO toolDTO)
    {
        if (!toolDTO.initialized) return null;

        return switch (toolDTO.type)
        {
            case Axe -> new Axe(toolDTO.axeLevel);
            case Hoe ->  new Hoe(toolDTO.hoeLevel);
            case Shear -> new Shear();
            case Scythe -> new Seythe();
            case Pickaxe -> new Pickaxe(toolDTO.pickaxeLevel);
            case MilkPail -> new MilkPail();
            case WateringCan -> new WateringCan(toolDTO.wateringCanLevel);
            case FishingPole -> new FishingPole(toolDTO.fishingPoleLevel);
            case BackPack ->  new BackPack();
            case TrashCan ->  new TrashCan(toolDTO.trashCanLevel);
        };
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
        return null;
    }
}

