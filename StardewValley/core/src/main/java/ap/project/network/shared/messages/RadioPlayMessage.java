package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class RadioPlayMessage extends Message {
    public int requestedID;
    public boolean isPaused;

    public RadioPlayMessage() {
    }

    public RadioPlayMessage(int requestedID, boolean isPaused) {
        this.requestedID = requestedID;
        this.isPaused = isPaused;
    }

    @Override
    public MessageType getType() {
        return MessageType.RADIO_PLAY;
    }
}
