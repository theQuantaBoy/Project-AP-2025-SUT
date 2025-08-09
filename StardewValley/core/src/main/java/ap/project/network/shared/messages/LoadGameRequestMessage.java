package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LoadGameRequestMessage extends Message
{
    public String gameId;
    public int playerId;

    public LoadGameRequestMessage() {}

    public LoadGameRequestMessage(String gameId, int playerId)
    {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOAD_GAME_REQUEST;
    }
}
