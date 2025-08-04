package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class CloseLobbyMessage extends Message
{
    public CloseLobbyMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.CLOSE_LOBBY;
    }
}
