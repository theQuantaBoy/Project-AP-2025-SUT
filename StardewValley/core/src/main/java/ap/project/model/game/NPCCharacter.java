package ap.project.model.game;

import ap.project.model.enums.CharacterType;
import ap.project.model.game.AbstractCharacter;
import ap.project.model.game.NPC;
import ap.project.visual.DialogueIndicator;
import com.badlogic.gdx.math.Vector2;

public class NPCCharacter extends AbstractCharacter {
    private final NPC npc; // Reference to the NPC model

    public NPCCharacter(CharacterType type, Vector2 spawnPoint, String nickName, NPC npc) {
        super(type, spawnPoint, nickName);
        this.npc = npc; // Initialize the reference
    }

    // Getter for the NPC model
    public NPC getNPC() {
        return npc;
    }
}
