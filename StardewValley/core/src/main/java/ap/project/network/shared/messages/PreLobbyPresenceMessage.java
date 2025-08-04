package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PreLobbyPresenceMessage extends Message
{
    public PreLobbyPresenceMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.PRE_LOBBY_PRESENCE;
    }
}
