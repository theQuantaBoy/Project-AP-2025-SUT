package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class RadioResponseMessage extends Message {
    public int requestedID;
    public int hostID;
    public boolean isPLaying;
    public String trackName;
    public float timestamp;

    public RadioResponseMessage() {
    }

    public RadioResponseMessage(int requestedID, int hostID, boolean isPLaying, String trackName,  float timestamp) {
        this.requestedID = requestedID;
        this.hostID = hostID;
        this.isPLaying = isPLaying;
        this.trackName = trackName;
        this.timestamp = timestamp;
    }

    @Override
    public MessageType getType() {
        return MessageType.RADIO_RESPONSE;
    }
}
