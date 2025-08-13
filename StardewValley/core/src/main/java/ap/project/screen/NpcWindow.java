package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
import ap.project.model.enums.GameAnimationType;
import ap.project.model.game.Game;
import ap.project.model.game.GameObject;
import ap.project.model.game.NPC;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipWithNpcData;
import ap.project.visual.MapVisual;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class NpcWindow
{
    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private boolean isVisible = false;
    private NPC currentNpc;
    private GiftWindow giftWindow;

    public NpcWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        window = new Window("NPC Manager", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(10);

        // Initialize gift window
        giftWindow = new GiftWindow(stage);

        stage.addActor(window);
    }

    public void setNpc(NPC npc)
    {
        this.currentNpc = npc;
        refresh();
    }

    public void toggleVisibility()
    {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible)
        {
            refresh();
            center();
        }
    }

    private void refresh()
    {
        window.clear();
        if (currentNpc == null) return;

        Table content = new Table();
        content.add(new Label("Interacting with: " + currentNpc.getName(), skin)).padBottom(20).row();

        Table buttonTable = new Table();
        buttonTable.defaults().minWidth(120).pad(10);

        // Gift Button
        TextButton giftButton = new TextButton("Gift", skin);
        giftButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                giftWindow.setNpc(currentNpc);
                giftWindow.toggleVisibility();
                toggleVisibility();
            }
        });

        // Quests Button (placeholder)
        TextButton questsButton = new TextButton("Quests", skin);
        questsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showQuestsDialog();
            }
        });

        // Friendship Button (placeholder)
        TextButton friendshipButton = new TextButton("Friendship", skin);
        friendshipButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showFriendshipDialog();
            }
        });

        // Close Button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toggleVisibility();
            }
        });

        buttonTable.add(giftButton).padRight(20);
        buttonTable.add(questsButton).padRight(20);
        buttonTable.add(friendshipButton).padRight(20);
        buttonTable.row();
        buttonTable.add(closeButton).colspan(3).padTop(20);

        content.add(buttonTable);
        window.add(content);
        window.pack();
    }

    private void showFriendshipDialog()
    {
        Dialog friendshipDialog = new Dialog("Friendship", skin);
        friendshipDialog.setModal(true);
        friendshipDialog.setMovable(true);
        friendshipDialog.setResizable(true);

        Player player = App.getCurrentGame().getCurrentPlayer();
        FriendshipWithNpcData friendship = player.getNpcFriendship(currentNpc);

        StringBuilder output = new StringBuilder();
        output.append("your friendship with ").append(currentNpc.getName()).append(":\n ");
        output.append("    xp: ").append(friendship.getXp()).append("\n");
        output.append("    level: ").append(friendship.getLevel()).append("\n");

        String friendText = output.toString();

        ScrollPane scrollPane = new ScrollPane(new Label(friendText, skin));
        scrollPane.setFadeScrollBars(false);
        friendshipDialog.getContentTable().add(scrollPane).width(500).height(350).pad(20);

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                friendshipDialog.hide();
            }
        });
        friendshipDialog.getButtonTable().add(closeButton);

        friendshipDialog.show(stage);
        friendshipDialog.setSize(800, 550);
        friendshipDialog.setPosition(
            (stage.getWidth() - friendshipDialog.getWidth()) / 2,
            (stage.getHeight() - friendshipDialog.getHeight()) / 2
        );
    }

    private void showQuestsDialog()
    {
        Dialog questsDialog = new Dialog("Quests", skin);
        questsDialog.setModal(true);
        questsDialog.setMovable(true);
        questsDialog.setResizable(true);

        Player player = App.getCurrentGame().getCurrentPlayer();

        // Create scrollable content
        Table contentTable = new Table();
        contentTable.pad(10);
        contentTable.defaults().pad(5).left();

        // Title row
        contentTable.add(new Label(currentNpc.getName() + "'s Quests:", skin))
            .colspan(2).padBottom(15).row();

        // Create quest rows
        for (int i = 0; i < 3; i++) {
            final int questIndex = i;

            // Get quest status
            String status;
            switch (questIndex) {
                case 0:
                    status = currentNpc.isFirstQuestAvailable() ? "available" :
                        currentNpc.getFirstQuestUser().isEmpty() ? "requires more xp" :
                            "finished by " + currentNpc.getFirstQuestUser();
                    break;
                case 1:
                    status = currentNpc.isSecondQuestAvailable() ? "available" :
                        currentNpc.getSecondQuestUser().isEmpty() ? "requires more xp" :
                            "finished by " + currentNpc.getSecondQuestUser();
                    break;
                case 2:
                    status = currentNpc.isThirdQuestAvailable() ? "available" :
                        currentNpc.getThirdQuestUser().isEmpty() ? "requires more xp" :
                            "finished by " + currentNpc.getThirdQuestUser();
                    break;
                default:
                    status = "unknown";
            }

            // Create quest row
            Table questRow = new Table();
            questRow.defaults().pad(5);

            // Quest description
            String description = currentNpc.getQuestDescription(questIndex) +
                "\nStatus: " + status;
            Label descriptionLabel = new Label(description, skin);
            descriptionLabel.setWrap(true);
            questRow.add(descriptionLabel).width(600).left();

            // Complete button
            TextButton completeButton = new TextButton("Complete", skin);
            completeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (handleQuestCompletion(questIndex, player))
                    {
                        questsDialog.hide();
                        toggleVisibility();

                        Vector2 loc = new Vector2(player.getPosition().x - 9, player.getPosition().y - 4);
                        MapVisual.playAnimationAt(GameAnimationType.CELEBRATION, loc);
                    }
                }
            });
            questRow.add(completeButton).padLeft(20);

            contentTable.add(questRow).colspan(2).padBottom(15).row();
        }

        contentTable.setSize(1000, 800);

        // Add scroll pane
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        questsDialog.getContentTable().add(scrollPane).width(900).height(650).pad(20);

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                questsDialog.hide();
            }
        });
        questsDialog.getButtonTable().add(closeButton).padTop(15);

        questsDialog.show(stage);
        questsDialog.pack(); // Auto-size the dialog

        // Center dialog
        questsDialog.setPosition(
            (stage.getWidth() - questsDialog.getWidth()) / 2,
            (stage.getHeight() - questsDialog.getHeight()) / 2
        );
    }

    private boolean handleQuestCompletion(int questIndex, Player player) {
        // Check availability
        boolean isAvailable;
        switch (questIndex) {
            case 0: isAvailable = currentNpc.isFirstQuestAvailable(); break;
            case 1: isAvailable = currentNpc.isSecondQuestAvailable(); break;
            case 2: isAvailable = currentNpc.isThirdQuestAvailable(); break;
            default: isAvailable = false;
        }

        if (!isAvailable) {
            UIRenderer.showTextBox("This quest is not available");
            return false;
        }

        // Get request and reward
        GameObject request = currentNpc.getNpcDetails().getQuestRequest(questIndex);
        GameObject reward = currentNpc.getNpcDetails().getQuestReward(questIndex);

        // Check inventory
        if (!player.hasEnoughInInventory(request.getObjectType(), request.getNumber())) {
            UIRenderer.showTextBox("You don't have the requested item");
            return false;
        }

        if (!player.inventoryHasCapacity()) {
            UIRenderer.showTextBox("Your inventory is full");
            return false;
        }

        // Complete quest
        player.removeAmountFromInventory(request.getObjectType(), request.getNumber());
        player.addToInventory(reward);

        switch (questIndex) {
            case 0:
                currentNpc.firstQuestDone(player.getUser().getNickname());
                break;
            case 1:
                currentNpc.secondQuestDone(player.getUser().getNickname());
                break;
            case 2:
                currentNpc.thirdQuestDone(player.getUser().getNickname());
                break;
        }

        UIRenderer.showTextBox("You completed a quest!");
        player.setCompletedQuests(player.getCompletedQuests() + 1);
        player.addToJournal("completed 1 quest for: " + currentNpc.getName() + ":\n");
        player.addToJournal("  gave " + request.getNumber() + " " + request.getObjectType() + "\n");
        player.addToJournal("  received " + reward.getNumber() + " " + reward.getObjectType() + "\n\n");

        player.setCompletedQuests(player.getCompletedQuests() + 1);

        return true;
    }

    private void center()
    {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public Window getWindow()
    {
        return window;
    }

    public GiftWindow getGiftWindow()
    {
        return giftWindow;
    }
}
