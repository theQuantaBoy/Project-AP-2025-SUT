package ap.project.network.shared.messages;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.network.shared.enums.MessageType;

public class UserProfileMessage extends Message
{
    public String username;
    public String nickname;
    public String gender;
    public int userId;

    // Kryo requires this
    public UserProfileMessage() {}

    public UserProfileMessage(String username, String nickname, String gender, int userId)
    {
        this.username = username;
        this.nickname = nickname;
        this.gender = gender;
        this.userId = userId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_PROFILE;
    }
}
