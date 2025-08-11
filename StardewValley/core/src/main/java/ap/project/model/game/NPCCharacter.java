package ap.project.model.game;

import ap.project.model.enums.CharacterType;
import com.badlogic.gdx.math.Vector2;

public class NPCCharacter extends AbstractCharacter
{
    private long lastUpdateTime = 0;

    public NPCCharacter(CharacterType type, Vector2 spawnPoint, String nickName)
    {
        super(type, spawnPoint, nickName);
    }

    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
    }
}
