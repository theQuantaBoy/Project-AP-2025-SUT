package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class JoinLobbySuccessMessage extends Message
{
    public String name;
    public String id;

    public JoinLobbySuccessMessage() {}

    public JoinLobbySuccessMessage(String name, String id)
    {
        this.name = name;
        this.id = id;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.JOIN_LOBBY_SUCCESS;
    }
}
