package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class OnlinePlayersListMessage extends Message
{
    public String list;

    public OnlinePlayersListMessage() {}

    public OnlinePlayersListMessage(String list)
    {
        this.list = list;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.ONLINE_PLAYERS_LIST;
    }
}
