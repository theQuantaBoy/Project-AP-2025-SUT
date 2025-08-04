package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyPresenceMessage extends Message
{
    public String id;

    public LobbyPresenceMessage() {}

    public LobbyPresenceMessage(String id)
    {
        this.id = id;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_PRESENCE;
    }
}
