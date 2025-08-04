package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyClosedMessage extends Message
{
    public LobbyClosedMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_CLOSED;
    }
}
