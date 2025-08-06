package ap.project.screen;

import ap.project.control.game.activities.TradeController;
import ap.project.model.App.App;
import ap.project.model.game.Player;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.TradeResponseMessage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TradeWindow {
    private Player selectedFriend;
    private InventoryWindow inventoryWindow;
    private TradeController controller;

    private Dialog popup;
    private Table loadingTable, respondTable;
    private TextButton acceptBtn, cancelBtn;
    private Label requestLabel;

    private Stage stage;
    private GameClient client;

    public TradeWindow(Stage stage, Skin skin) {
        this.stage = stage;
        this.client = GameClient.getInstance();

        popup = new Dialog("", skin);
        popup.setModal(true);
        popup.setMovable(false);
        popup.setSize(400, 200); // Set a fixed size

        // Create the loading content
        Table loadingContent = new Table(skin);
        loadingContent.add(new Label("Waiting for response...", skin)).pad(20);
        loadingTable = new Table(skin);
        loadingTable.add(loadingContent);

        // Create the response content
        Table respondContent = new Table(skin);
        requestLabel = new Label("", skin);
        acceptBtn = new TextButton("Accept", skin);
        cancelBtn = new TextButton("Reject", skin);

        respondContent.add(requestLabel).colspan(2).padBottom(15);
        respondContent.row();
        respondContent.add(acceptBtn).padRight(10);
        respondContent.add(cancelBtn).padLeft(10);
        respondContent.pad(20);
        respondTable = new Table(skin);
        respondTable.add(respondContent);

        // Add both tables to the popup
        popup.getContentTable().add(loadingTable);
        popup.getContentTable().row();
        popup.getContentTable().add(respondTable);

        // Hide both initially
        loadingTable.setVisible(false);
        respondTable.setVisible(false);

        // Set up button listeners
        acceptBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                respondToTrade(true);
            }
        });

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                respondToTrade(false);
            }
        });

        // Add contents to main tables
        loadingTable.add(loadingContent);
        respondTable.add(respondContent);

        // Hide both initially
        loadingTable.setVisible(false);
        respondTable.setVisible(false);
    }

    private void respondToTrade(boolean accepted) {
        if (selectedFriend == null) return;

        TradeResponseMessage message = new TradeResponseMessage(
            selectedFriend.getUser().getHashId(),
            App.getCurrentGame().getCurrentPlayer().getUser().getHashId(),
            accepted
        );

        client.send(message);
        hide();

        if (accepted) {
            // TODO: Open trade UI here
        }
    }

    public void setDependencies(InventoryWindow inv, TradeController controller) {
        this.selectedFriend = null;
        this.inventoryWindow = inv;
        this.controller = controller;
    }

    public void showLoadingFor(Player friend) {
        this.selectedFriend = friend;
        popup.getContentTable().clear();
        popup.text("Trade Request Sent");
        popup.getContentTable().add(loadingTable);
        loadingTable.setVisible(true);
        respondTable.setVisible(false);
        popup.show(stage);
    }

    public void showRequestFrom(Player friend) {
        this.selectedFriend = friend;

        // Clear any previous content
        popup.getContentTable().clear();

        popup.text("Trade Request");
        requestLabel.setText(friend.getNickName() + " wants to trade with you!");

        // Add the response table
        popup.getContentTable().add(respondTable);

        // Set visibility
        loadingTable.setVisible(false);
        respondTable.setVisible(true);

        // Show the popup
        if (!popup.isVisible()) {
            popup.show(stage);
        }

        // Center the popup on the stage
        popup.setPosition(
            (stage.getWidth() - popup.getWidth()) / 2,
            (stage.getHeight() - popup.getHeight()) / 2
        );
    }

    public void hide() {
        if (popup.isVisible()) {
            popup.hide();
        }
        selectedFriend = null;
    }

    public TradeController getController() {
        return controller;
    }
}
