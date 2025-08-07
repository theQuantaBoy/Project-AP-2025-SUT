package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.UserDTO;
import ap.project.network.shared.enums.MessageType;

public class UserUpdateMessage extends Message
{
    public UserDTO userDTO;

    public UserUpdateMessage() {}

    public UserUpdateMessage(UserDTO userDTO)
    {
        this.userDTO = userDTO;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_UPDATE;
    }
}
