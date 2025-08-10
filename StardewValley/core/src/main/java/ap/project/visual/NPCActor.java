//package ap.project.visual;
//
//import ap.project.model.game.NPCCharacter;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.scenes.scene2d.Actor;
//import com.badlogic.gdx.scenes.scene2d.Group;
//import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//
//public class NPCActor extends Group {
//    private final NPCCharacter npc;
//    private final Actor npcVisual;
//
//    public NPCActor(NPCCharacter npc, TextureRegion npcTexture, TextureRegion bubbleTexture, Skin skin) {
//        this.npc = npc;
//
//        // NPC visual
//        npcVisual = new Actor() {
//            @Override
//            public void draw(Batch batch, float parentAlpha) {
//                batch.draw(npcTexture, getX(), getY());
//            }
//        };
//        npcVisual.setSize(npcTexture.getRegionWidth(), npcTexture.getRegionHeight());
//        addActor(npcVisual);
//
//        // Dialogue indicator
//        dialogueIndicator = new DialogueIndicator(npc, bubbleTexture);
//        addActor(dialogueIndicator);
//
//        // Set initial position
//        setPosition(npc.getLocation().getX(), npc.getLocation().getY());
//    }
//
//    @Override
//    public void act(float delta) {
//        super.act(delta);
//        // Position indicator above NPC's head
//        dialogueIndicator.setPosition(
//            npcVisual.getX() + (npcVisual.getWidth() - dialogueIndicator.getWidth()) / 2,
//            npcVisual.getY() + npcVisual.getHeight() + 10
//        );
//    }
//
//    public NPCCharacter getNPC() {
//        return npc;
//    }
//}
