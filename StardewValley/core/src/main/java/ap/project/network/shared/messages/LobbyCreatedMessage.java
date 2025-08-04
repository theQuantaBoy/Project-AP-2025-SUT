package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyCreatedMessage extends Message
{
    public String lobbyName;
    public String lobbyId;
    public boolean isPrivate;
    public boolean isVisible;

    public LobbyCreatedMessage() {}

    public LobbyCreatedMessage(String lobbyName, String lobbyId, boolean isPrivate, boolean isVisible)
    {
        this.lobbyName = lobbyName;
        this.lobbyId = lobbyId;
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_CREATED;
    }
}
