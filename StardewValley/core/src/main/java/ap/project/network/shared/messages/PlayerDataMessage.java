package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.enums.MessageType;

public class PlayerDataMessage extends Message
{
    public PlayerDTO playerDTO;
    public String gameId;

    public PlayerDataMessage() {}

    public PlayerDataMessage(PlayerDTO playerDTO, String gameId)
    {
        this.playerDTO = playerDTO;
        this.gameId = gameId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_DATA;
    }
}
