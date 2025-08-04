package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PlayerJoinedLobbyMessage extends Message
{
    public int userId;
    public String username;
    public String lobbyId;
    public String nickname;
    public int avatarChoice;
    public int mapChoice;

    public PlayerJoinedLobbyMessage() {}

    public PlayerJoinedLobbyMessage(int userId, String username, String lobbyId, String nickname, int avatarChoice, int mapChoice)
    {
        this.userId = userId;
        this.username = username;
        this.lobbyId = lobbyId;
        this.nickname = nickname;
        this.avatarChoice = avatarChoice;
        this.mapChoice = mapChoice;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_JOINED_LOBBY;
    }
}
