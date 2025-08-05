// ConnectionFailedMessage.java
package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class ConnectionFailedMessage extends Message {
    private String reason;

    public ConnectionFailedMessage() {}

    public ConnectionFailedMessage(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public MessageType getType() {
        return MessageType.CONNECTION_FAILED;
    }
}
