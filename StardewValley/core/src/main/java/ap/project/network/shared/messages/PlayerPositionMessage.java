package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PlayerPositionMessage extends Message
{
    public String playerId;
    public float x, y;
    public byte direction; // Using byte for efficiency
    public long timestamp;

    public PlayerPositionMessage() {} // Kryo requires this

    public PlayerPositionMessage(String playerId, float x, float y, byte direction)
    {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_POSITION;
    }
}
