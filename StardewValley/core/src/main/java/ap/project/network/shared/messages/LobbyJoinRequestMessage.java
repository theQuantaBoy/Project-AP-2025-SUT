package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyJoinRequestMessage extends Message
{
    public String id;
    public String password;

    public LobbyJoinRequestMessage(String id, String password)
    {
        this.id = id;
        this.password = password;
    }

    public LobbyJoinRequestMessage() {}


    @Override
    public MessageType getType()
    {
        return MessageType.JOIN_LOBBY_REQUEST;
    }
}
