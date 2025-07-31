package ap.project.model.game;

import ap.project.model.enums.BuffType;

public class Buff
{
    private final BuffType type;
    private final int startedTime;

    public Buff(BuffType type, int startedTime)
    {
        this.type = type;
        this.startedTime = startedTime;
    }

    public BuffType getType()
    {
        return type;
    }

    public int getStartedTime()
    {
        return startedTime;
    }
}
