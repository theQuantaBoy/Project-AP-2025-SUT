package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class AckMessage extends Message
{
    public String message;

    public AckMessage() {}

    public AckMessage(String message)
    {
        this.message = message;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.ACK;
    }
}
