package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class MusicFileRequestMessage extends Message
{
    public String filename;

    public MusicFileRequestMessage() {}

    public MusicFileRequestMessage(String filename)
    {
        this.filename = filename;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.MUSIC_FILE_REQUEST;
    }
}
