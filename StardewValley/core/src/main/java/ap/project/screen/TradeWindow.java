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
        loadingTable = new Table();
        respondTable = new Table();
        acceptBtn = new TextButton("Accept", skin);
        cancelBtn = new TextButton("Cancel", skin);
        requestLabel = new Label("", skin);

        // Set up respondTable layout
        respondTable.add(requestLabel).colspan(2).padTop(10);
        respondTable.row();
        respondTable.add(acceptBtn).pad(5);
        respondTable.add(cancelBtn).pad(5);

        // Add loading and respond tables to the popup dialog
        popup.getContentTable().add(loadingTable).row();
        popup.getContentTable().add(respondTable);

        // Hide both tables initially
        loadingTable.setVisible(false);
        respondTable.setVisible(false);

        // Set up accept button listener
        acceptBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TradeResponseMessage message = new TradeResponseMessage(
                    selectedFriend.getUser().getHashId(),
                    App.getCurrentGame().getCurrentPlayer().getUser().getHashId(),
                    true
                );
                client.send(message);
                // TODO: Proceed to open trade UI here if needed
            }
        });

        // Set up cancel button listener
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TradeResponseMessage message = new TradeResponseMessage(
                    selectedFriend.getUser().getHashId(),
                    App.getCurrentGame().getCurrentPlayer().getUser().getHashId(),
                    false
                );
                client.send(message);
                hide();
            }
        });
    }

    public void setDependencies(InventoryWindow inv, TradeController controller) {
        this.selectedFriend = null;
        this.inventoryWindow = inv;
        this.controller = controller;
    }

    public void showLoadingFor(Player friend) {
        this.selectedFriend = friend;

        popup.text("Trade with " + friend.getNickName());
        loadingTable.clear();
        loadingTable.add(new Label("Waiting for " + friend.getNickName() + " to accept…", popup.getSkin()));

        loadingTable.setVisible(true);
        respondTable.setVisible(false);
        popup.show(stage);
    }

    public void showRequestFrom(Player friend) {
        this.selectedFriend = friend;

        popup.text("Trade Request");
        requestLabel.setText(friend.getNickName() + " wants to trade with you");

        loadingTable.setVisible(false);
        respondTable.setVisible(true);
        popup.show(stage);
    }

    public void hide() {
        popup.hide();
        selectedFriend = null;
    }

    public TradeController getController() {
        return controller;
    }
}
