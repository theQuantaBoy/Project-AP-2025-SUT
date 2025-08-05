package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class CloseLobbyErrorMessage extends Message
{
    public String message;

    public CloseLobbyErrorMessage() {}

    public CloseLobbyErrorMessage(String message)
    {
        this.message = message;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.CLOSE_LOBBY_ERROR;
    }
}
