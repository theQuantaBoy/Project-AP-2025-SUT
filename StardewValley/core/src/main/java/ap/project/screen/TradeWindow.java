package ap.project.screen;

import ap.project.control.game.activities.TradeController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.TradeResponseMessage;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.List;

public class TradeWindow {
    private Player selectedFriend;
    private InventoryWindow inventoryWindow;
    private TradeController controller;

    private Dialog popup;
    private Table contentTable;
    private TextButton acceptBtn, cancelBtn;
    private Label requestLabel;

    // Main trade screen components
    private Table mainTradeTable;
    private Table playerInventoryTable;
    private Table friendInventoryTable;
    private Table playerTradeSlots;
    private Table friendTradeSlots;
    private TextButton confirmTradeBtn;
    private TextButton cancelTradeBtn;
    private Label playerNameLabel;
    private Label friendNameLabel;

    private Stage stage;
    private GameClient client;
    private Skin skin;

    // Trade slots data using GameObject lists
    private ArrayList<GameObject> playerTradeItems = new ArrayList<>();
    private ArrayList<GameObject> friendTradeItems = new ArrayList<>();

    // Grid constants
    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_COLS = 4;
    private static final int TRADE_ROWS = 2;
    private static final int TRADE_COLS = 4;
    private static final int SLOT_SIZE = 48;
    private static final int GRID_SPACING = 4;

    // Selection tracking
    private GameObject selectedItem;
    private boolean isPlayerInventorySource;
    private boolean isTradeSlotSource;
    private int selectedIndex = -1;

    // UI styling
    private Drawable selectionBorderDrawable;
    private Drawable slotBackground;
    private static final int BORDER_THICKNESS = 3;
    private static final Color BORDER_COLOR = new Color(0.2f, 0.6f, 1f, 1f); // Blue selection

    public TradeWindow(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
        this.client = GameClient.getInstance();

        // Initialize trade item lists with empty slots (max 8 items each)
        for (int i = 0; i < TRADE_ROWS * TRADE_COLS; i++) {
            playerTradeItems.add(null);
            friendTradeItems.add(null);
        }

        // Create selection border and slot background
        createSelectionBorder();
        slotBackground = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(SLOT_SIZE, SLOT_SIZE, new Color(0.3f, 0.3f, 0.3f, 0.7f));

        // Create modal dialog
        popup = new Dialog("", skin);
        popup.setModal(true);
        popup.setMovable(false);
        popup.getContentTable().pad(20);
        popup.getButtonTable().padTop(10);

        // Build initial request/response content table
        contentTable = new Table(skin);
        contentTable.defaults().center().expandX().pad(10);

        // Request label (initially empty)
        requestLabel = new Label("", skin);
        contentTable.add(requestLabel).colspan(2).row();

        // Buttons
        acceptBtn = new TextButton("Accept", skin);
        cancelBtn = new TextButton("Reject", skin);
        contentTable.add(acceptBtn).padRight(20);
        contentTable.add(cancelBtn).padLeft(20);

        // Button listeners
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

        // Initialize main trade screen
        initializeMainTradeScreen();

        // Add the content table into the dialog initially
        popup.getContentTable().add(contentTable).grow();
    }

    private void createSelectionBorder() {
        Pixmap borderPixmap = new Pixmap(SLOT_SIZE, SLOT_SIZE, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(BORDER_COLOR);
        borderPixmap.fillRectangle(0, 0, SLOT_SIZE, BORDER_THICKNESS); // Top border
        borderPixmap.fillRectangle(0, SLOT_SIZE - BORDER_THICKNESS, SLOT_SIZE, BORDER_THICKNESS); // Bottom border
        borderPixmap.fillRectangle(0, 0, BORDER_THICKNESS, SLOT_SIZE); // Left border
        borderPixmap.fillRectangle(SLOT_SIZE - BORDER_THICKNESS, 0, BORDER_THICKNESS, SLOT_SIZE); // Right border
        selectionBorderDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();
    }

    public void initializeMainTradeScreen() {
        mainTradeTable = new Table(skin);
        mainTradeTable.setFillParent(true);

        // Player and friend name labels
        playerNameLabel = new Label("You", skin);
        friendNameLabel = new Label("Friend", skin);

        // Create inventory tables
        playerInventoryTable = new Table();
        friendInventoryTable = new Table();

        // Create trade slot tables
        playerTradeSlots = new Table();
        friendTradeSlots = new Table();

        // Trade control buttons
        confirmTradeBtn = new TextButton("Confirm Trade", skin);
        cancelTradeBtn = new TextButton("Cancel Trade", skin);

        confirmTradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                confirmTrade();
            }
        });

        cancelTradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelTrade();
            }
        });

        // Layout the main trade screen
        // Top row - Player names
        mainTradeTable.add(playerNameLabel).expandX().center().pad(10);
        mainTradeTable.add(friendNameLabel).expandX().center().pad(10);
        mainTradeTable.row();

        // Second row - Trade slots
        Table playerTradeContainer = new Table();
        playerTradeContainer.add(new Label("Your Offer", skin)).row();
        playerTradeContainer.add(playerTradeSlots).pad(5);

        Table friendTradeContainer = new Table();
        friendTradeContainer.add(new Label("Friend's Offer", skin)).row();
        friendTradeContainer.add(friendTradeSlots).pad(5);

        mainTradeTable.add(playerTradeContainer).pad(10).fill();
        mainTradeTable.add(friendTradeContainer).pad(10).fill();
        mainTradeTable.row();

        // Third row - Inventories
        Table playerInvContainer = new Table();
        playerInvContainer.add(new Label("Your Inventory", skin)).row();
        playerInvContainer.add(playerInventoryTable).pad(5);

        Table friendInvContainer = new Table();
        friendInvContainer.add(new Label(  "Friend's Inventory", skin)).row();
        friendInvContainer.add(friendInventoryTable).pad(5);

        mainTradeTable.add(playerInvContainer).pad(10).fill().expand();
        mainTradeTable.add(friendInvContainer).pad(10).fill().expand();
        mainTradeTable.row();

        // Bottom row - Control buttons
        Table buttonTable = new Table();
        buttonTable.add(confirmTradeBtn).padRight(20);
        buttonTable.add(cancelTradeBtn).padLeft(20);
        mainTradeTable.add(buttonTable).colspan(2).pad(20);
    }

    private void buildGrid(Table grid, List<GameObject> items, int rows, int cols, GridType gridType) {
        grid.clear();
        grid.defaults().size(SLOT_SIZE).pad(GRID_SPACING);

        BitmapFont quantityFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 16, Color.WHITE);

        for (int i = 0; i < rows * cols; i++) {
            final int index = i;
            final GridType currentGridType = gridType;

            Stack slotStack = new Stack();
            slotStack.setSize(SLOT_SIZE, SLOT_SIZE);

            // Add slot background
            Image bgImage = new Image(slotBackground);
            slotStack.add(bgImage);

            // Add click listener
            slotStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleSlotClick(index, currentGridType, items);
                }
            });

            // Add item if present
            if (i < items.size() && items.get(i) != null) {
                GameObject item = items.get(i);
                Texture texture = item.getObjectType().getTexture();

                Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
                image.setSize(SLOT_SIZE, SLOT_SIZE);
                slotStack.addActor(image);

                // Add quantity text if more than 1
                if (item.getNumber() > 1) {
                    Table quantityTable = new Table();
                    quantityTable.setFillParent(true);
                    quantityTable.bottom().right();

                    Label quantityLabel = new Label(String.valueOf(item.getNumber()),
                        new Label.LabelStyle(quantityFont, Color.WHITE));
                    quantityTable.add(quantityLabel).pad(2);
                    slotStack.add(quantityTable);
                }

                // Add selection border if selected
                if (item == selectedItem && selectedIndex == index) {
                    Image borderImage = new Image(selectionBorderDrawable);
                    slotStack.addActor(borderImage);
                }
            }

            grid.add(slotStack).size(SLOT_SIZE, SLOT_SIZE);
            if ((i + 1) % cols == 0) grid.row();
        }
    }

    private enum GridType {
        PLAYER_INVENTORY, FRIEND_INVENTORY, PLAYER_TRADE_SLOTS, FRIEND_TRADE_SLOTS
    }

    private void handleSlotClick(int index, GridType gridType, List<GameObject> items) {
        GameObject clickedItem = (index < items.size()) ? items.get(index) : null;

        // First click: select item
        if (clickedItem != null) {
            selectedItem = clickedItem;
            selectedIndex = index;

            // Set source flags
            isPlayerInventorySource = (gridType == GridType.FRIEND_INVENTORY);
            isTradeSlotSource = (gridType == GridType.PLAYER_TRADE_SLOTS/* || gridType == GridType.FRIEND_TRADE_SLOTS*/);

            refreshTradeScreen();
            UIRenderer.showTextBox("Selected " + selectedItem.getObjectType());
        }
        // Second click: transfer one item
        else if (selectedItem != null && clickedItem == null) {
            // Determine if we can move to this slot
            boolean canMove = false;

            if (isPlayerInventorySource && gridType == GridType.PLAYER_TRADE_SLOTS) {
                canMove = true;
            } else if (isTradeSlotSource && gridType == GridType.FRIEND_INVENTORY) {
                canMove = true;
            }

            if (canMove) {
                transferOneItem(gridType);
            } else {
                UIRenderer.showTextBox("Cannot move item here");
            }

            selectedItem = null;
            selectedIndex = -1;
            refreshTradeScreen();
        }
    }

    private void transferOneItem(GridType targetGridType) {
        if (selectedItem == null) return;

        if (isPlayerInventorySource && targetGridType == GridType.PLAYER_TRADE_SLOTS) {
            // Move one item from player inventory to trade slots
            moveOneItemFromInventoryToTrade();
        } else if (isTradeSlotSource && targetGridType == GridType.FRIEND_INVENTORY) {
            // Move one item from trade slots back to inventory
            moveOneItemFromTradeToInventory();
        }
    }

    private void moveOneItemFromInventoryToTrade() {
        Player player = App.getCurrentGame().getCurrentPlayer();

        // Find empty slot in player trade area
        for (int i = 0; i < playerTradeItems.size(); i++) {
            if (playerTradeItems.get(i) == null) {
                // Create new GameObject with quantity 1
                GameObject tradeItem = new GameObject(selectedItem.getObjectType(), 1);
                playerTradeItems.set(i, tradeItem);

                // Remove one from inventory
                selectedItem.setNumber(selectedItem.getNumber() - 1);
                if (selectedItem.getNumber() <= 0) {
                    selectedFriend.removeFromInventory(selectedItem);
                }

                UIRenderer.showTextBox("Added " + tradeItem.getObjectType() + " to trade offer");
                return;
            }
        }

        UIRenderer.showTextBox("Trade slots are full!");
    }

    private void moveOneItemFromTradeToInventory() {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (selectedIndex >= 0 && selectedIndex < playerTradeItems.size()) {
            GameObject tradeItem = playerTradeItems.get(selectedIndex);
            if (tradeItem != null) {
                // Add back to inventory
                GameObject existingInInventory = player.getItemInInventory(tradeItem.getObjectType());
                if (existingInInventory != null) {
                    existingInInventory.setNumber(existingInInventory.getNumber() + 1);
                } else {
                    GameObject newItem = new GameObject(tradeItem.getObjectType(), 1);
                    selectedFriend.addToInventory(newItem);
                }

                // Remove from trade slots
                playerTradeItems.set(selectedIndex, null);

                UIRenderer.showTextBox("Removed " + tradeItem.getObjectType() + " from trade offer");
            }
        }
    }

    private void refreshTradeScreen() {
        Player player = App.getCurrentGame().getCurrentPlayer();
        // Build grids with current data
        buildGrid(playerInventoryTable, player.getInventory(), INVENTORY_ROWS, INVENTORY_COLS, GridType.PLAYER_INVENTORY);
        buildGrid(playerTradeSlots, playerTradeItems, TRADE_ROWS, TRADE_COLS, GridType.PLAYER_TRADE_SLOTS);

        // For friend's inventory and trade slots - use mock data for now
        // TODO: Replace with actual friend's data when available
        ArrayList<GameObject> friendInventory = selectedFriend.getInventory(); // Mock empty for now
        buildGrid(friendInventoryTable, friendInventory, INVENTORY_ROWS, INVENTORY_COLS, GridType.FRIEND_INVENTORY);
        buildGrid(friendTradeSlots, friendTradeItems, TRADE_ROWS, TRADE_COLS, GridType.FRIEND_TRADE_SLOTS);
    }

    private void confirmTrade() {
        // TODO: Send trade confirmation with actual trade items to server
        // TradeConfirmMessage message = new TradeConfirmMessage(playerTradeItems, friendTradeItems);
        // client.send(message);
        UIRenderer.showTextBox("Trade confirmed! (Not implemented yet)");
        hide();
    }

    private void cancelTrade() {
        // Return all trade items to inventories
        returnTradeItemsToInventory();
        hide();
    }

    private void returnTradeItemsToInventory() {
        Player player = App.getCurrentGame().getCurrentPlayer();

        // Return player's trade items to inventory
        for (int i = 0; i < playerTradeItems.size(); i++) {
            GameObject tradeItem = playerTradeItems.get(i);
            if (tradeItem != null) {
                GameObject existingInInventory = player.getItemInInventory(tradeItem.getObjectType());
                if (existingInInventory != null) {
                    existingInInventory.setNumber(existingInInventory.getNumber() + tradeItem.getNumber());
                } else {
                    player.addToInventory(new GameObject(tradeItem.getObjectType(), tradeItem.getNumber()));
                }
                playerTradeItems.set(i, null);
            }
        }

        // Clear friend's trade items (they would be handled by network messages in real implementation)
        for (int i = 0; i < friendTradeItems.size(); i++) {
            friendTradeItems.set(i, null);
        }
    }

    private void respondToTrade(boolean accepted) {
        if (selectedFriend == null) return;

        TradeResponseMessage message = new TradeResponseMessage(
            selectedFriend.getUser().getHashId(),
            App.getCurrentGame().getCurrentPlayer().getUser().getHashId(),
            accepted
        );
        client.send(message);

        if (accepted) {
            showMainTradeScreen();
        } else {
            hide();
        }
    }

    public void showMainTradeScreen() {
        popup.text("Trading with " + (selectedFriend != null ? selectedFriend.getNickName() : "Friend"));
        // Update player names
        playerNameLabel.setText(App.getCurrentGame().getCurrentPlayer().getNickName());
        if (selectedFriend != null) {
            friendNameLabel.setText(selectedFriend.getNickName());
        }

        // Clear previous content and show main trade screen
        popup.getContentTable().clear();
        popup.getContentTable().add(mainTradeTable).grow();

        // Refresh the trade screen with current data
        refreshTradeScreen();

        popup.pack();
        centerPopup();
        popup.show(stage);
    }

    public void setDependencies(InventoryWindow inv, TradeController controller) {
        //this.selectedFriend = null;
        this.inventoryWindow = inv;
        this.controller = controller;
    }

    public void showLoadingFor(Player friend) {
        this.selectedFriend = friend;
        popup.text("Trade Request Sent");

        popup.getContentTable().clear();
        Label loading = new Label("Waiting for response...", popup.getSkin());
        popup.getContentTable().add(loading).expandX().center().pad(20);

        popup.pack();
        centerPopup();
        popup.show(stage);
    }

    public void showRequestFrom(Player friend) {
        this.selectedFriend = friend;
        popup.text("Trade Request");

        // Update label text
        requestLabel.setText(friend.getNickName() + " wants to trade with you!");

        // Reset dialog content
        popup.getContentTable().clear();
        popup.getContentTable().add(contentTable).grow();

        popup.pack();
        centerPopup();
        popup.show(stage);
    }

    public void hide() {
        if (popup.isVisible()) {
            popup.hide();
        }
        selectedFriend = null;
        selectedItem = null;
        selectedIndex = -1;
        returnTradeItemsToInventory();
    }

    private void centerPopup() {
        float pw = popup.getWidth();
        float ph = popup.getHeight();
        float sw = stage.getWidth();
        float sh = stage.getHeight();
        popup.setPosition((sw - pw) / 2f, (sh - ph) / 2f);
    }

    public TradeController getController() {
        return controller;
    }

    public Dialog getPopup() {
        return popup;
    }
}
