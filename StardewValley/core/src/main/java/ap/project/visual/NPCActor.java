package ap.project.visual;

import ap.project.model.game.NPC;
import ap.project.screen.WorldScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class NPCActor extends Actor {
    private final NPC npc;
    private final TextureRegion texture;
    private Button dialogueButton;
    private Window contextMenu;
    private final Skin skin;

    public NPCActor(NPC npc, TextureRegion texture, Skin skin) {
        this.npc = npc;
        this.texture = texture;
        this.skin = skin;
        setBounds(npc.getLocation().getX(), npc.getLocation().getY(), 1, 1);

        // Create dialogue button (hidden initially)
        dialogueButton = new Button(skin);
        dialogueButton.setSize(30, 30);
        dialogueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDialogue();
                dialogueButton.remove();
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void showInteractionButton() {
        dialogueButton.setPosition(getX() + 0.5f, getY() + 1.5f);
        getStage().addActor(dialogueButton);
    }

    public void showContextMenu() {
        if (contextMenu == null) {
            contextMenu = new Window(npc.getName() + " Menu", skin);

            Button giftButton = new Button(skin, "Gift");
            giftButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleGift();
                    contextMenu.remove();
                }
            });

            Button questsButton = new Button(skin, "Quests");
            questsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showQuests();
                    contextMenu.remove();
                }
            });

            Button friendshipButton = new Button(skin, "Friendship");
            friendshipButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showFriendship();
                    contextMenu.remove();
                }
            });

            contextMenu.add(giftButton).row();
            contextMenu.add(questsButton).row();
            contextMenu.add(friendshipButton).row();
            contextMenu.pack();
        }

        contextMenu.setPosition(getX(), getY() + 1.5f);
        getStage().addActor(contextMenu);
    }

    private void showDialogue() {
        String dialogue = npc.talk();
        // Implement dialogue display logic
        System.out.println("Dialogue: " + dialogue);
    }

    private void handleGift() {
        // Open inventory for gift selection
        WorldScreen.getInstance().toggleInventoryWindow();
    }

    private void showQuests() {
        // Show quests UI
        System.out.println("Showing quests for: " + npc.getName());
    }

    private void showFriendship() {
        // Show friendship level
        int friendshipLevel = WorldScreen.getInstance()
            .getPlayer()
            .getNpcFriendship(npc)
            .getLevel();
        System.out.println("Friendship Level: " + friendshipLevel);
    }
}
