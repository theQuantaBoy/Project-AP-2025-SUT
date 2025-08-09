package ap.project.network.shared.messages;

import ap.project.model.game.DummyGame;
import ap.project.network.shared.enums.MessageType;

import java.util.ArrayList;

public class UserSavedGameResponseMessage extends Message
{
    public ArrayList<DummyGame> gameList = new ArrayList<>();

    public UserSavedGameResponseMessage() {}

    public UserSavedGameResponseMessage(ArrayList<DummyGame> gameList)
    {
        this.gameList = gameList;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_SAVED_GAME_RESPONSE;
    }
}
