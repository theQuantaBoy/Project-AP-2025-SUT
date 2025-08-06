package ap.project.network.shared.messages;

import ap.project.model.game.Player;
import ap.project.network.shared.enums.MessageType;

public class TradeResponseMessage extends Message {
    private int responderID;
    private int requesterID;
    private boolean accepted;

    public TradeResponseMessage(int responder, int requester, boolean accepted) {
        this.responderID = responder;
        this.requesterID = requester;
        this.accepted = accepted;
    }

    @Override public MessageType getType() {
        return MessageType.TRADE_RESPONSE;
    }

    public int getResponderID() {
        return responderID;
    }

    public int getRequesterID() {
        return requesterID;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
