package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyTimeUpdateMessage extends Message
{
    public int remainingSeconds;

    public LobbyTimeUpdateMessage() {}

    public LobbyTimeUpdateMessage(int remainingSeconds)
    {
        this.remainingSeconds = remainingSeconds;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_TIME_UPDATE;
    }
}
