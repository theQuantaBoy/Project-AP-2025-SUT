package ap.project.network.shared.messages;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.network.shared.enums.MessageType;

public class UserProfileMessage extends Message
{
    public int userId;

    public UserProfileMessage() {}

    public UserProfileMessage(int userId)
    {
        this.userId = userId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_PROFILE;
    }
}
