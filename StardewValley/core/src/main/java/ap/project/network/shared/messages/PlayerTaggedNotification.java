package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PlayerTaggedNotification extends  Message {
    public int senderId;
    public int taggedId;

    public PlayerTaggedNotification() {} // Needed for KryoNet

    public PlayerTaggedNotification(int senderId, int taggedNickname) {
        this.senderId = senderId;
        this.taggedId = taggedNickname;
    }

    @Override
    public MessageType getType() {
        return MessageType.PLAYER_TAGGED;
    }
}
