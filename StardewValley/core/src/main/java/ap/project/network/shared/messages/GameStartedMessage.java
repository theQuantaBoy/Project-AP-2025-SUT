package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class GameStartedMessage extends Message
{
    public String gameID;

    public GameStartedMessage() {}

    public GameStartedMessage(String gameID)
    {
        this.gameID = gameID;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_STARTED;
    }
}
