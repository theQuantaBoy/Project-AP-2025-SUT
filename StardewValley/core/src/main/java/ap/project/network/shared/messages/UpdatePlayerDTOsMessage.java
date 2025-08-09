package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.enums.MessageType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpdatePlayerDTOsMessage extends Message
{
    public Map<Integer, PlayerDTO> playerStateCache = new ConcurrentHashMap<>();

    public UpdatePlayerDTOsMessage() {}

    public UpdatePlayerDTOsMessage(Map<Integer, PlayerDTO> playerStateCache)
    {
        this.playerStateCache = playerStateCache;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PLAYERS_DTO_UPDATE;
    }
}
