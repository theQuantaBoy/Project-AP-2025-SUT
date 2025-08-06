package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.enums.MessageType;

public class PlayerDTOMessage extends Message
{
    public PlayerDTO playerDTO;

    public PlayerDTOMessage() {}

    public PlayerDTOMessage(PlayerDTO playerDTO)
    {
        this.playerDTO = playerDTO;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYER_DTO;
    }
}
