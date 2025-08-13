package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class MusicFileChunkMessage extends Message
{
    public String filename;
    public int chunkIndex;
    public int totalChunks;
    public byte[] data;

    public MusicFileChunkMessage() {}

    public MusicFileChunkMessage(String filename, int chunkIndex, int totalChunks, byte[] data)
    {
        this.filename = filename;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.data = data;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.MUSIC_FILE_CHUNK;
    }
}
