package ap.project.network.shared.messages;

import ap.project.model.game.Time;
import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.enums.MessageType;

import java.util.ArrayList;

public class LoadedGameStartedMessage extends Message
{
    public String gameId;
    public Time gameTime;

    public LoadedGameStartedMessage() {}

    public LoadedGameStartedMessage(String gameId, Time gameTime)
    {
        this.gameId = gameId;
        this.gameTime = gameTime;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.LOADED_GAME_START;
    }
}
