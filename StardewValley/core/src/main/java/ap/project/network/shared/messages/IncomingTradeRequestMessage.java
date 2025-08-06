package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class IncomingTradeRequestMessage extends Message {
    public int senderId;

    public IncomingTradeRequestMessage(int senderId) {
        this.senderId = senderId;
    }

    public IncomingTradeRequestMessage() {
    }

    @Override
    public MessageType getType() {
        return MessageType.INCOMING_TRADE_REQUEST;
    }
}
