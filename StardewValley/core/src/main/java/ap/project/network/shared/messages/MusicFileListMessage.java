package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

import java.util.ArrayList;

public class MusicFileListMessage extends Message
{
    public ArrayList<String> filenames;

    public MusicFileListMessage() {}

    public MusicFileListMessage(ArrayList<String> filenames)
    {
        this.filenames = filenames;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.MUSIC_FILE_LIST;
    }
}
