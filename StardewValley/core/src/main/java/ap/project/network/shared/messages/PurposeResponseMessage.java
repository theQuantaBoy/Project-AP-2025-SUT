package ap.project.network.shared.messages;

import ap.project.model.game.GameObject;
import ap.project.network.shared.enums.MessageType;

public class PurposeResponseMessage extends Message {
    public int groomID;
    public int brideID;
    public GameObject ring;
    public boolean answer;

    public PurposeResponseMessage() {
    }

    public PurposeResponseMessage(int groomID, int brideID, GameObject ring, boolean answer) {
        this.groomID = groomID;
        this.brideID = brideID;
        this.ring = ring;
        this.answer = answer;
    }

    @Override
    public MessageType getType() {
        return MessageType.PURPOSE_RESPONSE;
    }
}
