package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.UserDTO;
import ap.project.network.shared.enums.MessageType;

import java.util.ArrayList;

public class UserSyncResponseMessage extends Message
{
    public ArrayList<UserDTO> userDTOs;

    public UserSyncResponseMessage() {}

    public UserSyncResponseMessage(ArrayList<UserDTO> userDTOs)
    {
        this.userDTOs = userDTOs;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.USER_SYNC_RESPONSE;
    }
}
