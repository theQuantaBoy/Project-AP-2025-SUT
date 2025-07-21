package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.game.Gift;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsWindow {
    private final Window popup;
    private final Stage stage;
    private final Table friendsTable;
    private final Table giftOptionsTable;
    private final Table newGiftTable;
    private final Table giftHistoryTable;
    private final Stack contentStack;
    private final Skin skin;
    private boolean isVisible = false;
    private final TooltipManager tooltipManager = TooltipManager.getInstance();
    private final Drawable tooltipBg;
    private Player selectedFriend;

    public FriendsWindow(Stage stage) {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        popup = new Window("Friends", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);

        tooltipManager.initialTime = 0.5f;
        tooltipManager.subsequentTime = 0.1f;
        tooltipBg = GameAssetsManager.getGameAssetsManager().createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        friendsTable = new Table();
        giftOptionsTable = new Table();
        giftHistoryTable = new Table();
        newGiftTable = new Table();

        giftOptionsTable.setVisible(false);
        giftHistoryTable.setVisible(false);
        newGiftTable.setVisible(false);

        ScrollPane scrollPane = new ScrollPane(friendsTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        contentStack = new Stack();
        contentStack.setFillParent(true);
        contentStack.add(scrollPane);
        contentStack.add(giftOptionsTable);
        contentStack.add(giftHistoryTable);
        contentStack.add(newGiftTable);

        popup.setSize(1500, 800);
        popup.add(contentStack).expand().fill();
        popup.row();

        center(stage);
        stage.addActor(popup);
    }

    private void refreshFriendsTable() {
        friendsTable.clearChildren();

        Player player = App.getCurrentGame().getCurrentPlayer();
        HashMap<Player, FriendshipData> friendships = player.getFriendships();

        for (Map.Entry<Player, FriendshipData> entry : friendships.entrySet()) {
            Player friend = entry.getKey();
            FriendshipData data = entry.getValue();

            Label nameLabel = new Label("Name: " + friend.getNickName()
                    + " | Level: " + data.getLevel(), skin);

            ProgressBar bar = new ProgressBar(0, data.getThresholdForLevel(data.getLevel()), 1, false, skin);
            bar.setValue(data.getXp());

            String tooltipText = friend.getNickName() + " (Level " + data.getLevel() + ")\n"
                    + "XP: " + data.getXp() + "/" + data.getThresholdForLevel(data.getLevel());
            Label tooltipLabel = new Label(tooltipText, skin);
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(8);
            nameLabel.addListener(tooltip);
            bar.addListener(tooltip);

            TextButton giftButton = new TextButton("Gift", skin);
            giftButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showGiftOptions(friend);
                }
            });

            stage.addActor(tooltip.getContainer());

            friendsTable.add(nameLabel).left().padRight(10).padBottom(5);
            friendsTable.add(bar).width(120).padRight(10).padBottom(5);
            friendsTable.add(giftButton).size(80, 30).padRight(10).padBottom(5);
            friendsTable.row();
        }

        center(stage);
    }

    private void showGiftOptions(Player friend) {
        this.selectedFriend = friend;
        giftOptionsTable.clear();
        giftOptionsTable.setVisible(true);
        friendsTable.setVisible(false);
        giftHistoryTable.setVisible(false);
        newGiftTable.setVisible(false);

        Label title = new Label("Gift Options for " + friend.getNickName(), skin);

        // New Gift Button
        TextButton newGiftButton = new TextButton("New Gift", skin);
        newGiftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showNewGiftUI();
            }
        });

        // Gift History Button
        TextButton giftHistoryButton = new TextButton("History", skin);
        giftHistoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showGiftHistoryUI();
            }
        });

        // Back Button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                giftOptionsTable.setVisible(false);
                friendsTable.setVisible(true);
            }
        });

        giftOptionsTable.add(title).colspan(3).padBottom(10).row();
        Table buttonTable = new Table();
        buttonTable.add(newGiftButton).pad(5).width(120);
        buttonTable.add(giftHistoryButton).pad(5).width(120);
        buttonTable.add(backButton).pad(5).width(120);
        giftOptionsTable.add(buttonTable);
    }

    private void showNewGiftUI() {
        newGiftTable.clear();
        newGiftTable.setVisible(true);
        giftOptionsTable.setVisible(false);

        Label title = new Label("Send Gift to " + selectedFriend.getNickName(), skin);

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGiftTable.setVisible(false);
                giftOptionsTable.setVisible(true);
            }
        });

        // Placeholder components
        Label giftLabel = new Label("Select a gift to send:", skin);
        SelectBox<String> giftSelect = new SelectBox<>(skin);
        giftSelect.setItems("Small Gift", "Medium Gift", "Large Gift", "Special Gift");

        TextButton sendButton = new TextButton("Send", skin);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Placeholder: Implement actual gift sending logic
                System.out.println("Sending gift: " + giftSelect.getSelected());
                newGiftTable.setVisible(false);
                giftOptionsTable.setVisible(true);
            }
        });

        newGiftTable.add(title).padBottom(10).row();
        Table contentTable = new Table();
        contentTable.add(giftLabel).padRight(5);
        contentTable.add(giftSelect).width(200).row();
        contentTable.add(sendButton).colspan(2).padTop(5);
        newGiftTable.add(contentTable).pad(5);
        newGiftTable.row();
        newGiftTable.add(backButton).padTop(5);
    }

    private void showGiftHistoryUI() {
        giftHistoryTable.clear();
        giftHistoryTable.setVisible(true);
        giftOptionsTable.setVisible(false);

        Label title = new Label("Gift History with " + selectedFriend.getNickName(), skin);

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                giftHistoryTable.setVisible(false);
                giftOptionsTable.setVisible(true);
            }
        });

        // Create scrollable history table
        Table historyContent = new Table();
        historyContent.defaults().pad(2); // Reduced padding
        ScrollPane scrollPane = new ScrollPane(historyContent, skin);
        scrollPane.setFadeScrollBars(false);

        // Get gift history
        List<Gift> gifts = App.getCurrentGame().getCurrentPlayer().getGiftHistoryWith(selectedFriend);

        if (gifts.isEmpty()) {
            historyContent.add(new Label("No gift history found", skin)).pad(10);
        } else {
            for (Gift gift : gifts) {
                // Determine sender/receiver relationship
                String fromTo;
                if (gift.getGiver().equals(App.getCurrentGame().getCurrentPlayer())) {
                    fromTo = "You → " + selectedFriend.getNickName();
                } else {
                    fromTo = selectedFriend.getNickName() + " → You";
                }


                // Add rate button for received gifts
                if (gift.getTaker().equals(App.getCurrentGame().getCurrentPlayer()) && !gift.isRated()) {
                    TextButton rateButton = new TextButton("Rate", skin);
                    rateButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            showRatingDialog(gift);
                        }
                    });
                    historyContent.add(rateButton).width(70);
                } else if (gift.getTaker().equals(App.getCurrentGame().getCurrentPlayer())) {
                    // Show existing rating
                    historyContent.add(new Label("Rated: " + gift.getRate() + "/5", skin)).width(80);
                } else {
                    // Empty space for sent gifts
                    historyContent.add().width(70);
                }
                historyContent.row();
            }
        }

        giftHistoryTable.add(title).padBottom(5).row();
        giftHistoryTable.add(scrollPane).expand().fill().pad(5).row();
        giftHistoryTable.add(backButton).padTop(5);
    }

    private void showRatingDialog(Gift gift) {
        Dialog dialog = new Dialog("Rate Gift", skin);

        // Rating selection
        SelectBox<Integer> ratingSelect = new SelectBox<>(skin);
        ratingSelect.setItems(1, 2, 3, 4, 5);

        // Confirm button
        TextButton confirmButton = new TextButton("Submit", skin);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int rating = ratingSelect.getSelected();
                gift.setRate(rating);
                dialog.hide();
                showGiftHistoryUI(); // Refresh to show updated rating
            }
        });

        // Layout
        Table content = dialog.getContentTable();
        content.add(new Label("How would you rate this gift?", skin)).pad(5).row();
        content.add(ratingSelect).width(200).pad(5).row();
        content.add(confirmButton).pad(5);

        dialog.show(stage);
    }

    private void center(Stage stage) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        popup.setPosition((w - popup.getWidth()) / 2f, (h - popup.getHeight()) / 2f);
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        popup.setVisible(isVisible);
        if (isVisible) {
            friendsTable.setVisible(true);
            giftOptionsTable.setVisible(false);
            giftHistoryTable.setVisible(false);
            newGiftTable.setVisible(false);
            refreshFriendsTable();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        popup.remove();
    }
}
