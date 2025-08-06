package ap.project.network.shared.messages;

import ap.project.model.game.Player;
import ap.project.network.shared.enums.MessageType;

public class TradeRequestMessage extends Message {

    int  requestID;
    int responseID;

    public TradeRequestMessage(int request, int response) {
        this.requestID = request;
        this.responseID = response;
    }

    public int getRequestID() {
        return requestID;
    }

    public int getResponseID() {
        return responseID;
    }

    @Override
    public MessageType getType() {
        return MessageType.TRADE_REQUEST;
    }
}
