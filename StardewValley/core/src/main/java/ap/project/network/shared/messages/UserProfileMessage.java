package ap.project.network.shared.messages;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.network.shared.enums.MessageType;

public class UserProfileMessage extends Message
{
    public User user;

    // Kryo requires this
    public UserProfileMessage() {}

    public UserProfileMessage(User user)
    {
        this.user = user;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_PROFILE;
    }
}
