package ap.project.network.shared.messages;

import ap.project.model.enums.MapTypes;
import ap.project.network.shared.enums.MessageType;
import java.util.ArrayList;
import java.util.List;

public class GameConfigMessage extends Message
{
    public static class PlayerConfig implements java.io.Serializable
    {
        public String username;
        public String nickname;
        public MapTypes mapType;
        public int playerIndex;
        public int avatarId;
    }

    public List<PlayerConfig> players = new ArrayList<>();
    public int yourPlayerIndex;

    public GameConfigMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_CONFIG;
    }
}
