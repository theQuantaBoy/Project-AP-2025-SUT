package ap.project.visual;

import ap.project.model.game.NPC;
import ap.project.model.game.NPCCharacter;
import ap.project.screen.WorldScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DialogueIndicator extends Actor {
    private final NPCCharacter character;
    private final TextureRegion bubbleTexture;

    public DialogueIndicator(NPCCharacter character, TextureRegion bubbleTexture) {
        this.character = character;
        this.bubbleTexture = bubbleTexture;
        this.setSize(bubbleTexture.getRegionWidth(), bubbleTexture.getRegionHeight());

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NPC npc = character.getNPC();
                if (npc != null) {
                    WorldScreen.getInstance().triggerNPCDialogue(npc);

                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        NPC npc = character.getNPC();
        if (npc != null) {
            // Always show bubble for demo purposes
            batch.draw(bubbleTexture, getX(), getY());
        }
    }
}
