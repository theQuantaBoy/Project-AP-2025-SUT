package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class UserSavedGameRequestMessage extends Message
{
    public UserSavedGameRequestMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.USER_SAVED_GAME_REQUEST;
    }
}
