package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class NewPublicChatMessage extends Message {
    public int senderHashId;
    public String message;

    public NewPublicChatMessage() {
    }

    public NewPublicChatMessage(int senderHashId, String message) {
        this.senderHashId = senderHashId;
        this.message = message;
    }


    @Override
    public MessageType getType() {
        return MessageType.NEW_PUBLIC_MESSAGE;
    }
}
