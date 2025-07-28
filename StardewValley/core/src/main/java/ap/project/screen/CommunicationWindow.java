package ap.project.screen;

import ap.project.control.game.activities.CommunicateController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
import ap.project.model.enums.GameObjectType;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import javax.swing.*;

public class CommunicationWindow {
    private final Window popup;
    private final Stage stage;
    private Table options;
    private final Stack contentStack;
    private final Skin skin;
    private boolean isVisible = false;
    private final Drawable tooltipBg;
    private Player selectedFriend;
    private final CommunicateController controller;
    private Label errorField;
    private final ChatScreen chatScreen;
    private final WorldScreen worldScreen;

    public CommunicationWindow(Stage stage, WorldScreen worldScreen) {
        this.stage = stage;
        this.worldScreen = worldScreen;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        popup = new Window("Communication", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);
        errorField = new Label("", skin);

        tooltipBg = GameAssetsManager.getGameAssetsManager().createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        options = new Table();
        contentStack = new Stack();
        contentStack.add(options);
        popup.add(contentStack).expand().fill();
        popup.row();
        popup.setSize(1000, 700);
        center(stage);
        stage.addActor(popup);
        this.controller = new CommunicateController();

        // Initialize chat screen
        this.chatScreen = new ChatScreen(stage, worldScreen, controller);
    }

    public Table buildOptions() {
        Table table = new Table(skin);
        table.defaults().pad(10).width(300).height(70);

        TextButton chatButton = new TextButton("Chat", skin);
        TextButton hugButton = new TextButton("Hug", skin);
        TextButton bouquetButton = new TextButton("Bouquet", skin);
        TextButton marryButton = new TextButton("Marry", skin);
        TextButton purpose = new TextButton("Purpose Answer", skin);
        TextButton backButton = new TextButton("Back", skin);
        errorField.setAlignment(Align.center);

        // Chat button listener
        chatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    openChatScreen();
                }
            }
        });

        // Hug button listener
        hugButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    Result result = controller.giveHug(selectedFriend);
                    showResult(result);
                }
            }
        });

        // Bouquet button listener
        bouquetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    Result result = controller.giveFlower(selectedFriend);
                    showResult(result);
                }
            }
        });

        // Marry button listener
        marryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    Player currentPlayer = App.getCurrentGame().getCurrentPlayer();

                    if (currentPlayer.hasRingInInventory()) {
                        showRingSelectionDialog();
                    } else {
                        Result result = new Result(false, "You don't have any rings to propose with");
                        showResult(result);
                    }
                }
            }
        });

        purpose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    Player currentPlayer = App.getCurrentGame().getCurrentPlayer();

                    if (currentPlayer.getPurposeList().containsKey(selectedFriend)) {
                        showPurposeAnswerDialog();
                    } else {
                        Result result = new Result(false, "No pending proposal from " + selectedFriend.getNickName());
                        showResult(result);
                    }
                }
            }
        });

        // Back button listener
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                errorField.setText("");
                hide();
            }
        });

        // Add buttons to table
        table.add(chatButton).padBottom(15).row();
        table.add(hugButton).padBottom(15).row();
        table.add(bouquetButton).padBottom(15).row();

        // Dynamic button based on purpose list status
        if (App.getCurrentGame().getCurrentPlayer().getPurposeList().containsKey(selectedFriend)) {
            table.add(purpose).padBottom(15).row();
        } else {
            table.add(marryButton).padBottom(15).row();
        }

        table.add(backButton).padTop(30).row();
        table.add(errorField).padTop(30);

        return table;
    }

    private void refreshOptions() {
        // Clear the stack
        contentStack.clearChildren();

        // Rebuild options table
        options = buildOptions();

        // Add back to stack
        contentStack.add(options);
    }

    private void showPurposeAnswerDialog() {
        Dialog dialog = new Dialog("Marriage Proposal", skin);
        dialog.setSize(400, 250);

        // Title
        Label title = new Label(selectedFriend.getNickName() + " has proposed to you!", skin);
        title.setWrap(true);
        dialog.getContentTable().add(title).width(350).pad(10).row();

        // Question
        Label question = new Label("Will you accept this proposal?", skin);
        question.setWrap(true);
        dialog.getContentTable().add(question).width(350).pad(10).row();

        // Button container
        Table buttonTable = new Table(skin);
        buttonTable.defaults().pad(10).width(120).height(50);

        // Yes button
        TextButton yesButton = new TextButton("Yes", skin);
        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Result result = controller.purposeRespond(selectedFriend, true);
                showResult(result);
                dialog.hide();
                refreshOptions();
            }
        });

        // No button
        TextButton noButton = new TextButton("No", skin);
        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Result result = controller.purposeRespond(selectedFriend, false);
                showResult(result);
                dialog.hide();
                refreshOptions();
            }
        });

        buttonTable.add(yesButton);
        buttonTable.add(noButton);

        dialog.getContentTable().add(buttonTable).padTop(15).row();
        dialog.show(stage);
    }

    private void showRingSelectionDialog() {
        Dialog dialog = new Dialog("Select a Ring", skin);
        dialog.setSize(500, 400);

        // Title
        Label title = new Label("Choose a ring for your proposal:", skin);
        dialog.getContentTable().add(title).padBottom(15).row();

        // Scrollable ring list
        Table ringTable = new Table(skin);
        ScrollPane scrollPane = new ScrollPane(ringTable, skin);
        scrollPane.setFadeScrollBars(false);
        dialog.getContentTable().add(scrollPane).width(450).height(250).pad(10).row();

        Player currentPlayer = App.getCurrentGame().getCurrentPlayer();

        // Add rings to the table
        for (GameObject item : currentPlayer.getInventory()) {
            if (GameObjectType.getRings().contains(item.getObjectType())) {
                TextButton ringButton = new TextButton(item.getObjectType().toString() + " x" + item.getNumber(), skin);

                ringButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                        Result result = controller.purposeAsk(selectedFriend, item);
                        showResult(result);
                        if (result.isSuccessful()) {
                            selectedFriend.setNewShohar(true);
                            refreshOptions();
                        }
                        dialog.hide();
                    }
                });

                ringTable.add(ringButton).width(400).height(50).pad(5).row();
            }
        }

        // Cancel button
        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                dialog.hide();
            }
        });

        dialog.getButtonTable().add(cancelButton).pad(10);
        dialog.show(stage);
    }

    private void openChatScreen() {
        // Hide communication window and open chat screen
        hide();

        // Get current player from the game/app context
        Player currentPlayer = worldScreen.getCurrentPlayer();

        if (currentPlayer != null && selectedFriend != null) {
            chatScreen.showChat(currentPlayer, selectedFriend);
        }
    }

    private void center(Stage stage) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        popup.setPosition((w - popup.getWidth()) / 2f, (h - popup.getHeight()) / 2f);
    }

    public void show(Player friend) {
        this.selectedFriend = friend;
        popup.getTitleLabel().setText("Communicate with " + friend.getNickName());
        isVisible = true;
        popup.setVisible(true);
        refreshOptions();
        center(stage);
    }

    private void showResult(Result result) {
        errorField.setText(result.message());
        errorField.setColor(result.isSuccessful() ? Color.GREEN : Color.RED);
    }

    public void hide() {
        isVisible = false;
        popup.setVisible(false);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public ChatScreen getChatScreen() {
        return chatScreen;
    }

    public void dispose() {
        popup.remove();
        chatScreen.dispose();
    }
}
