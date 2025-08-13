package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyTimeUpdateMessage extends Message
{
    public int remainingSeconds;
    public String lobbyAdmin;
    public String usersList;

    public LobbyTimeUpdateMessage() {}

    public LobbyTimeUpdateMessage(int remainingSeconds, String lobbyAdmin, String usersList)
    {
        this.remainingSeconds = remainingSeconds;
        this.lobbyAdmin = lobbyAdmin;
        this.usersList = usersList;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_TIME_UPDATE;
    }
}
