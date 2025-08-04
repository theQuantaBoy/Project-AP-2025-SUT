package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class LobbyCreationPermissionMessage extends Message
{
    public String name;
    public String password;
    public boolean isPrivate;
    public boolean isVisible;

    public LobbyCreationPermissionMessage() {}

    public LobbyCreationPermissionMessage(String name, String password, boolean isPrivate, boolean isVisible)
    {
        this.name = name;
        this.password = password;
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOBBY_CREATION_MESSAGE;
    }
}
