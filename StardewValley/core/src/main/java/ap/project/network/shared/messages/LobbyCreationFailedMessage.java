package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyCreationFailedMessage extends Message
{
    public LobbyCreationFailedMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_CREATION_FAILED;
    }
}
