package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LoadGameFailedMessage extends Message
{
    public String reason;

    public LoadGameFailedMessage() {}

    public LoadGameFailedMessage(String reason)
    {
        this.reason = reason;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOAD_GAME_FAILED;
    }
}
