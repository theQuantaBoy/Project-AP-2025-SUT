package ap.project.model.game;

import ap.project.model.enums.CharacterType;
import com.badlogic.gdx.math.Vector2;

public class PlayerCharacter extends AbstractCharacter
{
    private final Player player;
    private long lastUpdateTime = 0;

    public PlayerCharacter(CharacterType type, Vector2 spawnPoint, String nickName, Player player)
    {
        super(type, spawnPoint, nickName);
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setLastUpdateTime(long time)
    {
        this.lastUpdateTime = time;
    }

    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }
}

