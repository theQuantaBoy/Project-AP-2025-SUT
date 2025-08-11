package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class RadioChangedMessage extends Message {
    public int hostID;
    public String trackName;

    public RadioChangedMessage() {
    }

    public RadioChangedMessage(int hostID, String trackName) {
        this.hostID = hostID;
        this.trackName = trackName;
    }

    @Override
    public MessageType getType() {
        return MessageType.RADIO_CHANGED;
    }
}
