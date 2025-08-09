package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class SaveAndLeaveMessage extends Message
{
    public String gameId;
    public int playerId;

    public SaveAndLeaveMessage() {}

    public SaveAndLeaveMessage(String gameId, int playerId)
    {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.SAVE_AND_LEAVE;
    }
}
