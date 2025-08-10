package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class NewChatMessage extends Message {
    public int senderID;
    public int receiverID;
    public String message;

    public NewChatMessage() {
    }

    public NewChatMessage(int senderID, int receiverID, String message) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
    }

    @Override
    public MessageType getType() {
        return MessageType.NEW_MESSAGE;
    }
}
