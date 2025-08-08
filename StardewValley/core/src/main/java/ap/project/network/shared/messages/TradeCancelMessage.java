package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class TradeCancelMessage extends Message {
    public int receiverID;

    public TradeCancelMessage() {
    }

    public TradeCancelMessage(int receiverID) {
        this.receiverID = receiverID;
    }

    @Override
    public MessageType getType() {
        return MessageType.TRADE_CANCELLED;
    }
}
