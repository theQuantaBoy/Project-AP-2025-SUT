//package ap.project.visual;
//
//import ap.project.model.game.NPCCharacter;
//import ap.project.screen.WorldScreen;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.scenes.scene2d.Actor;
//import com.badlogic.gdx.scenes.scene2d.InputEvent;
//import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//
//public class DialogueIndicator extends Actor {
//    private final NPCCharacter npc;
//    private final TextureRegion bubbleTexture;
//
//    public DialogueIndicator(NPCCharacter npc, TextureRegion bubbleTexture) {
//        this.npc = npc;
//        this.bubbleTexture = bubbleTexture;
//        this.setSize(bubbleTexture.getRegionWidth(), bubbleTexture.getRegionHeight());
//
//        addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (npc.hasDialogueAvailable()) {
//                    WorldScreen.getInstance().triggerNPCDialogue(npc);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void draw(Batch batch, float parentAlpha) {
//        if (npc.hasDialogueAvailable()) {
//            batch.draw(bubbleTexture, getX(), getY());
//        }
//    }
//}
