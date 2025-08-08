package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GameShutdownMessage extends Message
{
    public String reason;
    public List<Integer> disconnectedPlayers;

    public GameShutdownMessage() {}

    public GameShutdownMessage(String reason, List<Integer> disconnectedPlayers)
    {
        this.reason = reason;
        this.disconnectedPlayers = disconnectedPlayers;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_SHUTDOWN;
    }
}
