package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class IncomingTradeResponseMessage extends Message{
    public int senderId;
    public boolean accepted;

    public IncomingTradeResponseMessage(int senderId, boolean accepted) {
        this.senderId = senderId;
        this.accepted = accepted;
    }

    public IncomingTradeResponseMessage() {
    }

    @Override
    public MessageType getType() {
        return MessageType.INCOMING_TRADE_RESPONSE;
    }
}

