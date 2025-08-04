package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class ActiveLobbiesListMessage extends Message
{
    public String list;

    public ActiveLobbiesListMessage() {}

    public ActiveLobbiesListMessage(String list)
    {
        this.list = list;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.ACTIVE_LOBBIES_LIST;
    }
}
