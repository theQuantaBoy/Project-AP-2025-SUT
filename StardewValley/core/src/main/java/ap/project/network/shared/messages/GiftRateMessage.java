package ap.project.network.shared.messages;

import ap.project.model.game.Gift;
import ap.project.network.shared.enums.MessageType;

public class GiftRateMessage extends Message {
    public int respondSender;
    public int respondReceiver;
    public int giftID;
    public int rate;

    public GiftRateMessage() {
    }

    public GiftRateMessage(int respondSender, int respondReceiver, int giftID, int rate) {
        this.respondSender = respondSender;
        this.respondReceiver = respondReceiver;
        this.giftID = giftID;
        this.rate = rate;
    }

    @Override
    public MessageType getType() {
        return MessageType.GIFT_RATE;
    }
}
