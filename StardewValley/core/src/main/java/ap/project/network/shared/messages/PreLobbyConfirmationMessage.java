package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PreLobbyConfirmationMessage extends Message
{
    public PreLobbyConfirmationMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.PRE_LOBBY_CONFIRMATION;
    }
}
