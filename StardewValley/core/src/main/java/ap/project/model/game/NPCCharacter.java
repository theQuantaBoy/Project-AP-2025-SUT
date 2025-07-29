package ap.project.model.game;

import ap.project.model.enums.CharacterType;
import com.badlogic.gdx.math.Vector2;

public class NPCCharacter extends AbstractCharacter
{
    public NPCCharacter(CharacterType type, Vector2 spawnPoint, String nickName)
    {
        super(type, spawnPoint, nickName);
    }
}
