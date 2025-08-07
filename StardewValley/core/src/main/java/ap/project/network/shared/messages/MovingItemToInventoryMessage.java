package ap.project.network.shared.messages;

import ap.project.model.game.GameObject;
import ap.project.network.shared.enums.MessageType;

public class MovingItemToInventoryMessage extends Message{
    public int receiverID;
    public GameObject object;

    public MovingItemToInventoryMessage() {}

    public MovingItemToInventoryMessage(int receiverID, GameObject object) {
        this.receiverID = receiverID;
        this.object = object;
    }

    @Override
    public MessageType getType() {
        return MessageType.MOVING_ITEM_TO_INVENTORY;
    }
}
