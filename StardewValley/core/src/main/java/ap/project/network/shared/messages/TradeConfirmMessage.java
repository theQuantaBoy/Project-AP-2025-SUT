package ap.project.network.shared.messages;

import ap.project.model.game.GameObject;
import ap.project.network.shared.enums.MessageType;

import java.util.List;

public class TradeConfirmMessage extends Message {
    public int receiverID;

    public TradeConfirmMessage() {
    }

    public TradeConfirmMessage(int receiverID) {
        this.receiverID = receiverID;
    }

    @Override
    public MessageType getType() {
        return MessageType.TRADE_CONFIRM;
    }
}
