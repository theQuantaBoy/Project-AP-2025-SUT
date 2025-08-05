package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class JoinLobbyErrorMessage extends Message
{
    public String reason;

    public JoinLobbyErrorMessage() {}

    public JoinLobbyErrorMessage(String reason)
    {
        this.reason = reason;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.JOIN_LOBBY_ERROR;
    }
}
