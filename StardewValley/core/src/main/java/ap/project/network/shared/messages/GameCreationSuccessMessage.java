package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class GameCreationSuccessMessage extends Message
{
    public int player_1_id = -1;
    public int player_2_id = -1;
    public int player_3_id = -1;
    public int player_4_id = -1;

    public GameCreationSuccessMessage() {}

    public GameCreationSuccessMessage(int player_1_id, int player_2_id, int player_3_id, int player_4_id)
    {
        this.player_1_id = player_1_id;
        this.player_2_id = player_2_id;
        this.player_3_id = player_3_id;
        this.player_4_id = player_4_id;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_CREATED_SUCCESSFULLY;
    }
}
