package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class TimeSyncRequestMessage extends Message
{
    public String playerId;

    public TimeSyncRequestMessage() {}

    public TimeSyncRequestMessage(String playerId)
    {
        this.playerId = playerId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.TIME_SYNC_REQUEST;
    }
}
