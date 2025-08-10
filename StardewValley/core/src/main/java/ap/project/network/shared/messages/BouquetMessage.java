package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class BouquetMessage extends Message {
    public int senderID;
    public int  receiverID;

    public BouquetMessage() {
    }

    public BouquetMessage(int senderID, int receiverID) {
        this.senderID = senderID;
        this.receiverID = receiverID;
    }

    @Override
    public MessageType getType() {
        return MessageType.NEW_FLOWER;
    }
}
