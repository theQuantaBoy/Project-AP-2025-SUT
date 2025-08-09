package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LeaveMessage extends Message
{
    public LeaveMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.LEAVE;
    }
}
