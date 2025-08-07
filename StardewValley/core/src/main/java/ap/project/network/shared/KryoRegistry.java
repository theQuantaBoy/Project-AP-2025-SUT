package ap.project.network.shared;

import ap.project.model.App.User;
import ap.project.model.enums.*;
import ap.project.model.game.AbstractCharacter;
import ap.project.model.game.GameObject;
import ap.project.model.game.Time;
import com.esotericsoftware.kryo.Kryo;
import ap.project.network.shared.enums.MessageType;
import ap.project.network.shared.messages.*;
import java.util.ArrayList;

public class KryoRegistry
{
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
        kryo.register(PlayerGamePresenceMessage.class);
        kryo.register(UpdateGameMinuteMessage.class);
        kryo.register(CloseLobbyErrorMessage.class);

        kryo.register(TradeRequestMessage.class);
        kryo.register(TradeResponseMessage.class);
        kryo.register(IncomingTradeRequestMessage.class);
        kryo.register(IncomingTradeResponseMessage.class);

        kryo.register(MovingItemToTadeMessage.class);
        kryo.register(MovingItemToInventoryMessage.class);
        kryo.register(GameObject.class);
        kryo.register(GameObjectType.class);

        kryo.register(TradeConfirmMessage.class);

        // Register all other message classes ...
    }
}
