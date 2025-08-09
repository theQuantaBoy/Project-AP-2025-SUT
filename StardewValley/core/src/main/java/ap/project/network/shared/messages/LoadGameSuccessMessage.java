package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LoadGameSuccessMessage extends Message
{
    public String lobbyName;
    public String lobbyId;

    public LoadGameSuccessMessage() {}

    public LoadGameSuccessMessage(String lobbyName, String lobbyId)
    {
        this.lobbyName = lobbyName;
        this.lobbyId = lobbyId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOAD_GAME_SUCCESS;
    }
}
