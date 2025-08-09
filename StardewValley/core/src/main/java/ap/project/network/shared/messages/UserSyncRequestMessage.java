package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class UserSyncRequestMessage extends Message
{
    public UserSyncRequestMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.USER_SYNC_REQUEST;
    }
}
