package ap.project.screen;

import ap.project.control.game.activities.CommunicateController;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
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
    private final Table options;
    private final Stack contentStack;
    private final Skin skin;
    private boolean isVisible = false;
    private final Drawable tooltipBg;
    private Player selectedFriend;
    private final CommunicateController controller;
    private Label errorField;

    public CommunicationWindow(Stage stage) {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        popup = new Window("Communication", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);
        errorField = new Label("", skin);

        tooltipBg = GameAssetsManager.getGameAssetsManager().createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        options = buildOptions();
        contentStack = new Stack();
        contentStack.add(options);
        popup.add(contentStack).expand().fill();
        popup.row();
        popup.setSize(1000, 700);
        center(stage);
        stage.addActor(popup);
        this.controller = new CommunicateController();

    }

    private Table buildOptions() {
        Table table = new Table(skin);
        table.defaults().pad(10).width(300).height(70);

        TextButton talkButton = new TextButton("Talk", skin);
        TextButton hugButton = new TextButton("Hug", skin);
        TextButton bouquetButton = new TextButton("Bouquet", skin);
        TextButton marryButton = new TextButton("Marry", skin);
        TextButton backButton = new TextButton("Back", skin);
        errorField.setAlignment(Align.center);


        // Add button listeners
        talkButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    Result result = controller.talk(selectedFriend);
                    showResult(result);
                }
            }
        });

        hugButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    Result result = controller.giveHug(selectedFriend);
                    showResult(result);
                }
            }
        });

        bouquetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    Result result = controller.giveFlower(selectedFriend);
                    showResult(result);
                }
            }
        });

        marryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (selectedFriend != null) {
                    //controller.marry(selectedFriend);
                }
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                hide();
            }
        });

        // Add buttons to table with proper layout
        table.add(talkButton).padBottom(15);
        table.row();
        table.add(hugButton).padBottom(15);
        table.row();
        table.add(bouquetButton).padBottom(15);
        table.row();
        table.add(marryButton).padBottom(15);
        table.row();
        table.add(backButton).padTop(30);
        table.row();
        table.add(errorField).padTop(30);


        return table;
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

    public void dispose() {
        popup.remove();
    }
}
