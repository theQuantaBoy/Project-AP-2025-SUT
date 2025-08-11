package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class NpcServerDetailsMessage extends Message
{
    public String gameId;
    public String npcName;

    public float x;
    public float y;
    public byte direction;
    public boolean isMoving;

    public NpcServerDetailsMessage() {}

    public NpcServerDetailsMessage(String gameId, String npcName, float x, float y, byte direction, boolean isMoving)
    {
        this.gameId = gameId;
        this.npcName = npcName;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.isMoving = isMoving;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.NPC_SERVER_DETAILS;
    }
}
