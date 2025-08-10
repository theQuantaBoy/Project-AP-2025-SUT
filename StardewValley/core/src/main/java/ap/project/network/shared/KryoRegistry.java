package ap.project.network.shared;

import ap.project.model.App.User;
import ap.project.model.enums.*;
import ap.project.model.game.AbstractCharacter;
import ap.project.model.game.GameObject;
import ap.project.model.game.Time;
import ap.project.network.shared.DTO.*;
import com.esotericsoftware.kryo.Kryo;
import ap.project.network.shared.enums.MessageType;
import ap.project.network.shared.messages.*;
import java.util.ArrayList;

public class KryoRegistry
{
    public static final int BUFFER_LIMIT = 5120000;

    public static void registerClasses(Kryo kryo)
    {
        kryo.register(MessageType.class);
        kryo.register(TestMessage.class);

        kryo.register(UserProfileMessage.class);
        kryo.register(ArrayList.class);

        kryo.register(ap.project.model.enums.Gender.class);
        kryo.register(ap.project.model.enums.MapTypes.class);

        kryo.register(AbstractCharacter.Direction.class);

        // Enums for time system
        kryo.register(Time.class);
        kryo.register(Season.class);
        kryo.register(Weather.class);
        kryo.register(TimeOfDay.class);

        // Primitive types used in messages
        kryo.register(int.class);
        kryo.register(long.class);
        kryo.register(boolean.class);
        kryo.register(float.class);
        kryo.register(String.class);

        kryo.register(ConnectionConfirmedMessage.class);
        kryo.register(ConnectionFailedMessage.class);
        kryo.register(User.class);

        kryo.register(com.badlogic.gdx.graphics.Texture.class);
        kryo.register(com.badlogic.gdx.graphics.glutils.FileTextureData.class);

        kryo.register(LobbyPresenceMessage.class);
        kryo.register(PreLobbyConfirmationMessage.class);
        kryo.register(PreLobbyErrorMessage.class);
        kryo.register(PreLobbyPresenceMessage.class);
        kryo.register(UserChoicesMessage.class);
        kryo.register(LobbyCreatedMessage.class);
        kryo.register(LobbyCreationFailedMessage.class);
        kryo.register(LobbyCreationPermissionMessage.class);

        kryo.register(ActiveLobbiesListMessage.class);
        kryo.register(JoinLobbyErrorMessage.class);
        kryo.register(JoinLobbySuccessMessage.class);
        kryo.register(LobbyJoinRequestMessage.class);
        kryo.register(OnlinePlayersListMessage.class);

        kryo.register(CloseLobbyRequestMessage.class);
        kryo.register(CreateGameRequestMessage.class);
        kryo.register(LeaveLobbyMessage.class);
        kryo.register(CloseLobbyMessage.class);
        kryo.register(LobbyTimeUpdateMessage.class);
        kryo.register(PlayerJoinedLobbyMessage.class);
        kryo.register(PlayerLeftLobbyMessage.class);
        kryo.register(PlayerPositionUpdateMessage.class);

        kryo.register(GameCreationSuccessMessage.class);
        kryo.register(GameCreationFailedMessage.class);

        kryo.register(GameStartedMessage.class);
        kryo.register(GameTimeSyncMessage.class);
        kryo.register(GamePresenceMessage.class);
        kryo.register(UpdateGameMinuteMessage.class);
        kryo.register(CloseLobbyErrorMessage.class);

        kryo.register(PlayerDTOMessage.class);

        kryo.register(BackPackDTO.class);
        kryo.register(FarmDTO.class);
        kryo.register(GameObjectDTO.class);
        kryo.register(PlayerDTO.class);
        kryo.register(SkillDTO.class);
        kryo.register(TileDTO.class);
        kryo.register(ToolDTO.class);
        kryo.register(UserDTO.class);

        kryo.register(ap.project.model.enums.tool_enums.BackPackLevel.class);
        kryo.register(ap.project.model.enums.GameObjectType.class);
        kryo.register(ap.project.model.enums.tool_enums.ToolType.class);
        kryo.register(ap.project.model.enums.CharacterType.class);
        kryo.register(ap.project.model.enums.building_enums.CraftingRecipeEnums.class);
        kryo.register(ap.project.model.game.Point.class);
        kryo.register(ap.project.model.enums.TileTexture.class);
        kryo.register(ap.project.model.enums.SkillType.class);
        kryo.register(ap.project.model.enums.building_enums.KitchenRecipe.class);
        kryo.register(com.badlogic.gdx.math.Vector2.class);
        kryo.register(ap.project.model.enums.tool_enums.AxeLevel.class);
        kryo.register(ap.project.model.enums.tool_enums.FishingPoleLevel.class);
        kryo.register(ap.project.model.enums.tool_enums.PickaxeLevel.class);
        kryo.register(ap.project.model.enums.tool_enums.HoeLevel.class);
        kryo.register(ap.project.model.enums.tool_enums.BackPackLevel.class);
        kryo.register(ap.project.model.enums.tool_enums.ToolType.class);
        kryo.register(ap.project.model.enums.tool_enums.TrashCanLevel.class);
        kryo.register(ap.project.model.enums.tool_enums.WateringCanLevel.class);

        kryo.register(ap.project.network.shared.DTO.TileDTO[][].class);
        kryo.register(ap.project.network.shared.DTO.TileDTO[].class);
        kryo.register(ap.project.network.shared.DTO.ForagingTreeDTO.class);
        kryo.register(ap.project.network.shared.DTO.ForagingSeedDTO.class);
        kryo.register(ap.project.network.shared.DTO.ForagingMineralDTO.class);
        kryo.register(ap.project.network.shared.DTO.ForagingCropDTO.class);

        kryo.register(ap.project.model.enums.resources_enums.ForagingTreeType.class);
        kryo.register(ap.project.model.enums.resources_enums.ForagingCropType.class);
        kryo.register(ap.project.model.enums.resources_enums.ForagingSeedType.class);
        kryo.register(ap.project.model.enums.resources_enums.ForagingMineralType.class);

        kryo.register(ap.project.network.shared.DTO.ResourceDTO.class);
        kryo.register(ap.project.model.enums.resources_enums.ResourceItem.class);

        kryo.register(ap.project.network.shared.DTO.CropDTO.class);
        kryo.register(ap.project.network.shared.DTO.GiantCropDTO.class);
        kryo.register(ap.project.network.shared.DTO.PlantDTO.class);
        kryo.register(ap.project.network.shared.DTO.TreeDTO.class);
        kryo.register(ap.project.network.shared.DTO.ForagingCropDTO.class);
        kryo.register(ap.project.network.shared.DTO.ForagingMineralDTO.class);
        kryo.register(ap.project.network.shared.DTO.ForagingSeedDTO.class);
        kryo.register(ap.project.network.shared.DTO.ForagingTreeDTO.class);
        kryo.register(ap.project.network.shared.DTO.CraftingItemDTO.class);

        kryo.register(ap.project.model.enums.Season[].class);
        kryo.register(ap.project.model.enums.Season.class);
        kryo.register(ap.project.model.enums.resources_enums.CropType.class);
        kryo.register(ap.project.model.enums.resources_enums.TreeType.class);
        kryo.register(ap.project.model.enums.building_enums.CraftingRecipeEnums.class);

        kryo.register(PlayerDataMessage.class);
        kryo.register(UserSyncRequestMessage.class);
        kryo.register(UserSyncResponseMessage.class);
        kryo.register(UserUpdateMessage.class);

        kryo.register(GameShutdownMessage.class);
        kryo.register(SaveAndLeaveMessage.class);

        kryo.register(LoadedGameStartedMessage.class);
        kryo.register(LoadGameFailedMessage.class);
        kryo.register(LoadGameRequestMessage.class);
        kryo.register(LoadGameSuccessMessage.class);
        kryo.register(UserSavedGameRequestMessage.class);
        kryo.register(UserSavedGameResponseMessage.class);

        kryo.register(LeaveMessage.class);
        kryo.register(UpdatePlayerDTOsMessage.class);
        kryo.register(java.util.concurrent.ConcurrentHashMap.class);

        kryo.register(JoinActiveGameMessage.class);

        kryo.register(TradeRequestMessage.class);
        kryo.register(TradeResponseMessage.class);
        kryo.register(IncomingTradeRequestMessage.class);
        kryo.register(IncomingTradeResponseMessage.class);
        kryo.register(TradeConfirmMessage.class);
        kryo.register(TradeCancelMessage.class);
        kryo.register(GameObject.class);
        kryo.register(GameObjectType.class);
        kryo.register(MovingItemToTadeMessage.class);
        kryo.register(MovingItemToInventoryMessage.class);
        kryo.register(BackPackDTOMessage.class);

        kryo.register(NewChatMessage.class);
        kryo.register(NewGiftMessage.class);


        // Register all other message classes ...
    }
}
