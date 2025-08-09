package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.enums.MessageType;

public class GameStartedMessage extends Message
{
    public String gameID;
    public PlayerDTO playerDTO;

    public GameStartedMessage() {}

    public GameStartedMessage(String gameID, PlayerDTO playerDTO)
    {
        this.gameID = gameID;
        this.playerDTO = playerDTO;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_STARTED;
    }
}
