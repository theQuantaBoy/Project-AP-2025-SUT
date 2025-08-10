package ap.project.network.shared.messages;

import ap.project.model.game.GameObject;
import ap.project.network.shared.enums.MessageType;

public class NewMarriageMessage extends Message {
    public int groomID;
    public int brideID;
    public GameObject ring;

    public NewMarriageMessage() {
    }

    public NewMarriageMessage(int groomID, int brideID, GameObject ring) {
        this.groomID = groomID;
        this.brideID = brideID;
        this.ring = ring;
    }

    @Override
    public MessageType getType() {
        return MessageType.NEW_PURPOSE;
    }
}
