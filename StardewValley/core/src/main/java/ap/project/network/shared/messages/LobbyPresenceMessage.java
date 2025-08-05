package ap.project.network.shared.messages;

import ap.project.model.game.Point;
import ap.project.network.shared.enums.MessageType;

public class LobbyPresenceMessage extends Message
{
    public float x;
    public float y;
    public byte direction;
    public boolean isMoving;

    public LobbyPresenceMessage() {}

    public LobbyPresenceMessage(float x, float y,  byte direction,  boolean isMoving)
    {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.isMoving = isMoving;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_PRESENCE;
    }
}
