package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PlayerLeftLobbyMessage extends Message
{
    public String lobbyID;
    public int userID;

    public PlayerLeftLobbyMessage() {}

    public PlayerLeftLobbyMessage(String lobbyID, int userID)
    {
        this.lobbyID = lobbyID;
        this.userID = userID;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_LEFT_LOBBY;
    }
}
