package ap.project.network.shared.messages;

import ap.project.model.game.GameObject;
import ap.project.network.shared.enums.MessageType;

public class MovingItemToTadeMessage extends Message{
    public int receiverID;
    public GameObject item;

    public MovingItemToTadeMessage() {
    }

    public MovingItemToTadeMessage(int receiverID, GameObject item) {
        this.receiverID = receiverID;
        this.item = item;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public GameObject getItem() {
        return item;
    }

    @Override
    public MessageType getType() {
        return MessageType.MOVING_ITEM_TO_TRADE;
    }
}
