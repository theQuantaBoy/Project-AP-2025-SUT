package ap.project.network.shared.messages;

import ap.project.model.enums.Gender;
import ap.project.network.shared.enums.MessageType;

public class UserProfileMessage extends Message
{
    public String username;
    public String nickname;
    public Gender gender;
    public int avatarId;

    // Kryo requires this
    public UserProfileMessage() {}

    public UserProfileMessage(String username, String nickname, Gender gender, int avatarId)
    {
        this.username = username;
        this.nickname = nickname;
        this.gender = gender;
        this.avatarId = avatarId;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_PROFILE;
    }
}
