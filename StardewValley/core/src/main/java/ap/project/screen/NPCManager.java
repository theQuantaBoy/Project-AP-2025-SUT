//package ap.project.screen;
//
//import ap.project.model.App.GameAssetsManager;
//import ap.project.model.enums.CharacterType;
//import ap.project.model.game.*;
//import ap.project.screen.WorldScreen;
//import ap.project.visual.NPCActor;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.utils.Disposable;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class NPCManager implements Disposable {
//    private final WorldScreen worldScreen;
//    private final TextureRegion bubbleTexture;
//
//    public NPCManager(WorldScreen worldScreen) {
//        this.worldScreen = worldScreen;
//        this.bubbleTexture = GameAssetsManager.getGameAssetsManager().getTexture("dialogue_bubble");
//    }
//
//    public void resetAllDialogues() {
//        for (NPCActor actor : npcActors.values()) {
//            actor.getNPC().resetDialogueAvailability();
//        }
//    }
//
//    public void update(float delta) {
//        // Update logic can be added here if needed
//    }
//
//    public void render(SpriteBatch batch) {
//        // Rendering is handled by uiStage
//    }
//
//    public void interactWithNPC(String npcName) {
//        if (npcActors.containsKey(npcName)) {
//            currentInteractingNpc = npcActors.get(npcName);
//            // Context menu logic can be added here
//        }
//    }
//
//    public void giveGiftToCurrentNPC(GameObject gift) {
//        if (currentInteractingNpc == null) return;
//
//        NPCCharacter npc = currentInteractingNpc.getNPC();
//        if (npc.acceptsGift(gift.getType())) {
//            int friendshipIncrease = npc.isFavorite(gift.getType()) ? 20 : 10;
//            npc.increaseFriendship(friendshipIncrease);
//
//            worldScreen.getCommunicationWindow().showNotification(
//                "Gift accepted! +" + friendshipIncrease + " friendship"
//            );
//        } else {
//            worldScreen.getCommunicationWindow().showNotification(
//                npc.getNickName() + " doesn't like this gift"
//            );
//        }
//    }
//
//    @Override
//    public void dispose() {
//        for (NPCActor actor : npcActors.values()) {
//            actor.remove();
//        }
//        npcActors.clear();
//    }
//}
