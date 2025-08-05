package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public abstract class Message implements java.io.Serializable
{
    public abstract MessageType getType();
}
