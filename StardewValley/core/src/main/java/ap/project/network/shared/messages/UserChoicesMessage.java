package ap.project.network.shared.messages;

import ap.project.model.App.User;
import ap.project.network.shared.enums.MessageType;

public class UserChoicesMessage extends Message
{
    public int characterChoice;
    public int mapChoice;

    public UserChoicesMessage() {}

    public UserChoicesMessage(int characterChoice, int mapChoice)
    {
        this.characterChoice = characterChoice;
        this.mapChoice = mapChoice;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_CHOICE;
    }
}
