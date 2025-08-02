package ap.project.network.shared.messages;

import ap.project.model.game.AbstractCharacter;
import ap.project.model.game.Player;
import ap.project.network.shared.enums.MessageType;

public class PlayerPositionMessage extends Message
{
    private String playerId;
    private float x, y;
    private AbstractCharacter.Direction direction;

    public PlayerPositionMessage() {}

    public PlayerPositionMessage(String playerId, float x, float y, AbstractCharacter.Direction direction)
    {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_POSITION;
    }
}
