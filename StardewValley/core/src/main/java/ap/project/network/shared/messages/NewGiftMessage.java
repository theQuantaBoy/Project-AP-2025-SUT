package ap.project.network.shared.messages;

import ap.project.model.game.GameObject;
import ap.project.network.shared.enums.MessageType;

public class NewGiftMessage extends Message {
    public int senderID;
    public int receiverID;
    public GameObject gift;

    public NewGiftMessage() {
    }

    public NewGiftMessage(int senderID, int receiverID, GameObject gift) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.gift = gift;
    }

    @Override
    public MessageType getType() {
        return MessageType.NEW_GIFT;
    }
}
