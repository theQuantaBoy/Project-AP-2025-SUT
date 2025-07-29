package ap.project.screen;

import ap.project.control.game.activities.CommunicateController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
import ap.project.model.game.GameObject;
import ap.project.model.game.Gift;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipData;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

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
    private CommunicateController controller;
    private InventoryWindow inventoryWindow;
    private Table inventoryTable;
    private WorldScreen  worldScreen;

    public FriendsWindow(Stage stage, WorldScreen worldScreen) {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        this.worldScreen = worldScreen;
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

        popup.setSize(1200, 700);
        popup.add(contentStack).expand().fill();
        popup.row();

        center(stage);
        stage.addActor(popup);
        this.controller = new  CommunicateController();
        this.inventoryWindow = new InventoryWindow(stage, worldScreen);
        controller.setFriendsWindow(this);
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
                    + "XP: " + data.getXp();
            Label tooltipLabel = new Label(tooltipText, skin, "WhiteText");
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

            //stage.addActor(tooltip.getContainer());

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
                showGiftHistoryUI(friend);
            }
        });

        // Back Button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                giftOptionsTable.setVisible(false);
                friendsTable.setVisible(true);
                refreshFriendsTable();
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
        title.setFontScale(1.2f);

        // Back button - fixed with proper UI reset
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Reset UI state properly
                inventoryWindow.clearSelection();  // Add this method to InventoryWindow
                stage.setKeyboardFocus(null);      // Clear keyboard focus
                newGiftTable.setVisible(false);
                giftOptionsTable.setVisible(true);
            }
        });

        // Inventory - ensure fresh state
        inventoryTable = inventoryWindow.buildLimitedInventoryTable();

        // Amount input field - fixed with proper input handling
        final TextField amountField = new TextField("1", skin);
        amountField.setAlignment(Align.center);
        amountField.setMaxLength(3);
        amountField.setTextFieldFilter((textField, c) ->
            Character.isDigit(c) || c == '\b' || c == 127);
        amountField.setMessageText("Amount");
        amountField.setWidth(60);

        // Add key listener to handle Enter/ESC
        amountField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    stage.setKeyboardFocus(null);  // Defocus on Enter
                    return true;
                } else if (keycode == Input.Keys.ESCAPE) {
                    backButton.toggle();  // Simulate back button press
                    return true;
                }
                return false;
            }
        });

        Table amountTable = new Table();
        amountTable.add(new Label("Amount:", skin)).padRight(10);
        amountTable.add(amountField);

        // Initialize error label
        final Label errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);

        // Gift button
        TextButton giftButton = new TextButton("Gift", skin);
        giftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameObject selected = inventoryWindow.getSelectedInventoryObject();
                if (selected == null) {
                    errorLabel.setText("No item selected.");
                    errorLabel.setColor(Color.RED);
                    return;
                }

                int amount;
                try {
                    amount = Integer.parseInt(amountField.getText());
                    if (amount <= 0 || amount > selected.getNumber()) {
                        errorLabel.setText("Invalid amount (1-" + selected.getNumber() + ")");
                        errorLabel.setColor(Color.RED);
                        return;
                    }
                } catch (NumberFormatException e) {
                    errorLabel.setText("Invalid input.");
                    errorLabel.setColor(Color.RED);
                    return;
                }

                Result result = controller.gift(selectedFriend, selected.getObjectType(), amount);
                errorLabel.setText(result.message());
                errorLabel.setColor(result.isSuccessful() ? Color.GREEN : Color.RED);

                // Reset UI on success
                if (result.isSuccessful()) {
                    amountField.setText("1");
                    inventoryWindow.clearSelection();
                    inventoryTable = inventoryWindow.buildLimitedInventoryTable();
                    newGiftTable.getCells().get(1).setActor(inventoryTable);
                }
            }
        });

        // Layout
        newGiftTable.add(title).padBottom(10).row();
        newGiftTable.add(inventoryTable).padBottom(10).row();
        newGiftTable.add(amountTable).padBottom(10).row();
        newGiftTable.add(giftButton).padBottom(5).row();
        newGiftTable.add(backButton).padTop(5).row();
        newGiftTable.add(errorLabel).padTop(5).row();
    }






    private void showGiftHistoryUI(Player friend) {
        giftHistoryTable.clear();
        giftHistoryTable.setVisible(true);
        giftOptionsTable.setVisible(false);

        Label title = new Label("Gift History with: " + friend.getNickName(), skin, "Impact");
        Table historyContent = new Table();
        List<Gift> giftList = App.getCurrentGame().getCurrentPlayer().getGiftHistoryWith(friend);

        if (giftList.isEmpty()) {
            Label errorLabel = new Label("No gifts send", skin, "Impact");
            giftHistoryTable.add(errorLabel).pad(10);
        } else {
            giftHistoryTable.add(title).padBottom(5).row();

//            for (Gift gift : giftList) {
//                Label name = new Label("Gift: " + gift.toString() + " x" + gift.getAmount() + "        ", skin);
//                historyContent.add(name).pad(5);
//                if (gift.isRated()) {
//                    Label rate = new Label("Rate: " + gift.getRate(), skin);
//                    historyContent.add(rate).pad(5);
//                } else {
//                    TextButton rateButton = new TextButton("Rate", skin);
//                    historyContent.add(rateButton).pad(5).row();
//                    rateButton.addListener(new ClickListener() {
//                        @Override
//                        public void clicked(InputEvent event, float x, float y) {
//                            showRatingDialog(gift);
//                        }
//                    });
//                }
//            }
            controller.giftHistory(selectedFriend, historyContent);
        }

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                giftHistoryTable.setVisible(false);
                giftOptionsTable.setVisible(true);
            }
        });


        giftHistoryTable.add(historyContent).pad(5).row();
        giftHistoryTable.add(backButton).padTop(5);
    }


    public void showRatingDialog(Gift gift) {
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
                controller.giftRate(gift, rating);
                dialog.hide();
                showGiftHistoryUI(selectedFriend); // Refresh to show updated rating
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
