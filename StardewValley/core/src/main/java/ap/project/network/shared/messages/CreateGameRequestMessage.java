package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class CreateGameRequestMessage extends Message
{
    public CreateGameRequestMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.CREATE_GAME_REQUEST;
    }
}
