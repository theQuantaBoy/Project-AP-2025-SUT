package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class JoinActiveGameMessage extends Message
{
    public String gameID;

    public JoinActiveGameMessage() {}

    public JoinActiveGameMessage(String gameID)
    {
        this.gameID = gameID;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.JOIN_GAME;
    }
}
