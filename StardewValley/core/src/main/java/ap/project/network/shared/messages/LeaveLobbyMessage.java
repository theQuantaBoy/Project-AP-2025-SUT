package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LeaveLobbyMessage extends Message
{
    public LeaveLobbyMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.LEAVE_LOBBY_REQUEST;
    }
}
