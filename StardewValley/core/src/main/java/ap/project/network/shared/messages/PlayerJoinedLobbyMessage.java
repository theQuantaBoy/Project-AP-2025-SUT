package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PlayerJoinedLobbyMessage extends Message
{
    public int userId;
    public String lobbyId;

    public PlayerJoinedLobbyMessage() {}

    public PlayerJoinedLobbyMessage(int userId, String lobbyId)
    {
        this.userId = userId;
        this.lobbyId = lobbyId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_JOINED_LOBBY;
    }
}
