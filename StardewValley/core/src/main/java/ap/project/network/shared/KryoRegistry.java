package ap.project.network.shared;

import ap.project.model.game.AbstractCharacter;
import com.esotericsoftware.kryo.Kryo;
import ap.project.model.enums.Gender;
import ap.project.model.enums.MapTypes;
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

        // Register all other message classes ...
    }
}
