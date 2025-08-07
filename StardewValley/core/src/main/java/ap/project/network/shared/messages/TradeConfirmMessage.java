package ap.project.network.shared.messages;

import ap.project.model.game.GameObject;
import ap.project.network.shared.enums.MessageType;

import java.util.List;

public class TradeConfirmMessage extends Message {
    public int receiverID;
    public List<GameObject> playerTrade;
    public List<GameObject> friendTrade;

    public TradeConfirmMessage() {
    }

    public TradeConfirmMessage(int receiverID, List<GameObject> playerTrade, List<GameObject> friendTrade) {
        this.receiverID = receiverID;
        this.playerTrade = playerTrade;
        this.friendTrade = friendTrade;
    }

    @Override
    public MessageType getType() {
        return MessageType.TRADE_CONFIRM;
    }
}
