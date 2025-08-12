package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class PlayerTaggedNotification extends  Message {
    public int senderId;
    public int taggedId;
    public String message;

    public PlayerTaggedNotification() {} // Needed for KryoNet

    public PlayerTaggedNotification(int senderId, int taggedNickname, String message) {
        this.senderId = senderId;
        this.taggedId = taggedNickname;
        this.message = message;
    }

    @Override
    public MessageType getType() {
        return MessageType.PLAYER_TAGGED;
    }
}
