package ap.project.network.shared.messages;

import ap.project.model.player_data.FriendshipData;
import ap.project.network.shared.enums.MessageType;

public class UpdateFriendshipMessage extends Message {
    public int updatedID;
    public int updatingID;
    public FriendshipData friendshipData;

    public UpdateFriendshipMessage() {
    }

    public UpdateFriendshipMessage(int updatedID, int updatingID, FriendshipData friendshipData) {
        this.updatedID = updatedID;
        this.updatingID = updatingID;
        this.friendshipData = friendshipData;
    }

    @Override
    public MessageType getType() {
        return MessageType.UPDATING_FRIENDSHIP;
    }
}
