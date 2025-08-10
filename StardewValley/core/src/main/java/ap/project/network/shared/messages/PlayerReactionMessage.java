package ap.project.network.shared.messages;

import ap.project.model.enums.ReactionEmoji;
import ap.project.network.shared.enums.MessageType;

public class PlayerReactionMessage extends Message
{
    public String gameId;
    public int playerId;
    public ReactionEmoji currentEmoji;
    public String currentReactionText;

    public PlayerReactionMessage() {}

    public PlayerReactionMessage(String gameId, int playerId, ReactionEmoji currentEmoji)
    {
        this.gameId = gameId;
        this.playerId = playerId;
        this.currentEmoji = currentEmoji;
        this.currentReactionText = "";
    }

    public PlayerReactionMessage(String gameId, int playerId, String currentReactionText)
    {
        this.gameId = gameId;
        this.playerId = playerId;
        this.currentReactionText = currentReactionText;
        this.currentEmoji = null;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_REACTION;
    }
}
