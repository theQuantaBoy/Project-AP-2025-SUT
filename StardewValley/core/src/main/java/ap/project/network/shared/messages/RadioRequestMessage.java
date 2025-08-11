package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class RadioRequestMessage extends Message {
    public int requestID;
    public int hostID;

    public RadioRequestMessage() {
    }

    public RadioRequestMessage(int requestID, int hostID) {
        this.requestID = requestID;
        this.hostID = hostID;
    }

    @Override
    public MessageType getType() {
        return MessageType.RADIO_REQUEST;
    }
}
