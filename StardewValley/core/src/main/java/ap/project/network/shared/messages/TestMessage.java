package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class TestMessage extends Message
{
    private String text;

    public TestMessage() {}

    public TestMessage(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.TEST;
    }
}
