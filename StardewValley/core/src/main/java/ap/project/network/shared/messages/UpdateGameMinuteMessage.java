package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class UpdateGameMinuteMessage extends Message
{
    public UpdateGameMinuteMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.UPDATE_GAME_MINUTE;
    }
}
