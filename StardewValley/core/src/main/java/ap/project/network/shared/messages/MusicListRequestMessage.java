package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class MusicListRequestMessage extends Message
{
    public MusicListRequestMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.MUSIC_LIST_REQUEST;
    }
}
