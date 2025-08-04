package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class GameCreationFailedMessage extends Message
{
    public String reason;

    public GameCreationFailedMessage() {}

    public GameCreationFailedMessage(String reason)
    {
        this.reason = reason;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_CREATION_FAILED;
    }
}
