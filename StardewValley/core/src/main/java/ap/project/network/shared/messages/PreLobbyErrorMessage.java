package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PreLobbyErrorMessage extends Message
{
    public String message;

    public PreLobbyErrorMessage() {}

    public PreLobbyErrorMessage(String message)
    {
        this.message = message;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PRE_LOBBY_ERROR;
    }
}
