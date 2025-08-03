// ConnectionConfirmedMessage.java
package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class ConnectionConfirmedMessage extends Message
{
    public ConnectionConfirmedMessage() {}

    @Override
    public MessageType getType()
    {
        return MessageType.CONNECTION_CONFIRMED;
    }
}
