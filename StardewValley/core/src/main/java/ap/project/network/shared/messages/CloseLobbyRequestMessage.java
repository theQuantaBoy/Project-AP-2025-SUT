package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class CloseLobbyRequestMessage extends Message
{
    public CloseLobbyRequestMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.CLOSE_LOBBY_REQUEST;
    }
}
