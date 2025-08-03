package ap.project.network.shared;

import ap.project.model.App.User;
import ap.project.model.enums.*;
import ap.project.model.game.AbstractCharacter;
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
        kryo.register(PlayerPositionMessage.class);
        kryo.register(TestMessage.class);

        kryo.register(UserProfileMessage.class);
        kryo.register(GameConfigMessage.class);
        kryo.register(GameConfigMessage.PlayerConfig.class);
        kryo.register(AckMessage.class);
        kryo.register(ArrayList.class);

        kryo.register(ap.project.model.enums.Gender.class);
        kryo.register(ap.project.model.enums.MapTypes.class);

        kryo.register(AbstractCharacter.Direction.class);
        kryo.register(PlayerPositionMessage.class);

        // New registrations for time synchronization
        kryo.register(GameTimeSyncMessage.class);
        kryo.register(TimeSyncRequestMessage.class);

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

        // Register all other message classes ...
    }
}
