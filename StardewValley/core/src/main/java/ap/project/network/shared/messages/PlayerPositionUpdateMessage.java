package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PlayerPositionUpdateMessage extends Message
{
    public int userId;
    public float x;
    public float y;
    public byte direction;
    public boolean isMoving;

    public PlayerPositionUpdateMessage() {}

    public PlayerPositionUpdateMessage(int userId, float x, float y,  byte direction,  boolean isMoving)
    {
        this.userId = userId;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.isMoving = isMoving;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_POSITION_UPDATE;
    }
}
