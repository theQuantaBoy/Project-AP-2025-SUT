package ap.project.screen;

import ap.project.Main;
import ap.project.control.game.activities.CommunicateController;
import ap.project.control.game.activities.TradeController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
import ap.project.model.game.*;
import ap.project.model.player_data.FriendshipData;
import ap.project.model.tools.Tool;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
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
    private final Table tradeHistoryTable;
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
    private TradeWindow tradeWindow;
    private GameClient client;
    private PublicChatScreen publicChatScreen;

    private Table musicControlsTable;
    private SelectBox<String> trackSelectBox;
    private RadioPlayer radioPlayer;
    private TextButton playPauseButton;
    private TextButton nextButton;
    private TextButton prevButton;
    private TextButton shuffleButton;
    private TextButton repeatButton;
    private TextButton backButton;


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
        tradeHistoryTable = new Table();
        newGiftTable = new Table();

        giftOptionsTable.setVisible(false);
        giftHistoryTable.setVisible(false);
        tradeHistoryTable.setVisible(false);
        newGiftTable.setVisible(false);

        ScrollPane scrollPane = new ScrollPane(friendsTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        radioPlayer = Main.getApp().getRadio();
        setupMusicControls();

        contentStack = new Stack();
        contentStack.setFillParent(true);
        contentStack.add(scrollPane);
        contentStack.add(giftOptionsTable);
        contentStack.add(giftHistoryTable);
        contentStack.add(tradeHistoryTable);
        contentStack.add(newGiftTable);
        contentStack.add(musicControlsTable);

        popup.setSize(1200, 800);
        popup.add(contentStack).expand().fill();
        popup.row();

        center(stage);
        stage.addActor(popup);
        this.controller = new  CommunicateController();
        this.inventoryWindow = new InventoryWindow(stage, worldScreen);
        controller.setFriendsWindow(this);
        tradeWindow = new TradeWindow(stage, skin);
        client = GameClient.getInstance();
        publicChatScreen = new PublicChatScreen(stage, worldScreen);



    }

    private void setupMusicControls() {
        musicControlsTable = new Table();
        musicControlsTable.defaults().pad(1);

        // Track selection dropdown
        trackSelectBox = new SelectBox<>(skin);
        trackSelectBox.setAlignment(Align.center);
        updateTrackList();

        // Buttons
        playPauseButton = new TextButton("Play", skin);
        prevButton = new TextButton("<<", skin);
        nextButton = new TextButton(">>", skin);
        shuffleButton = new TextButton("Shuffle", skin);
        repeatButton = new TextButton("Repeat", skin);
        backButton = new TextButton("Back", skin);


        // Button listeners
        playPauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int selectedIndex = trackSelectBox.getSelectedIndex();

                // If nothing is playing, start the selected track
                if (radioPlayer.getCurrentTrackName() == null && selectedIndex >= 0) {
                    radioPlayer.playTrack(selectedIndex);
                    playPauseButton.setText("Pause");
                }
                // If music is playing, pause it
                else if (radioPlayer.isPlaying()) {
                    radioPlayer.pause();
                    client.send(new RadioPlayMessage(App.getCurrentGame().getCurrentPlayer().getUser().getHashId(), true));
                    playPauseButton.setText("Play");
                }
                // If music is paused, resume it
                else if (radioPlayer.getCurrentTrackName() != null) {
                    radioPlayer.resume();
                    client.send(new RadioPlayMessage(App.getCurrentGame().getCurrentPlayer().getUser().getHashId(), false));
                    playPauseButton.setText("Pause");
                }
                // If no track is selected, do nothing
                else {
                    Gdx.app.log("RadioPlayer", "No track selected to play");
                }
            }
        });

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                radioPlayer.playNext();
                updateTrackSelection();
                client.send(new RadioChangedMessage(App.getCurrentGame().getCurrentPlayer().getUser().getHashId(), radioPlayer.getCurrentTrackName()));
            }
        });

        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                radioPlayer.playPrevious();
                updateTrackSelection();
                client.send(new RadioChangedMessage(App.getCurrentGame().getCurrentPlayer().getUser().getHashId(), radioPlayer.getCurrentTrackName()));
            }
        });

        shuffleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                radioPlayer.toggleShuffle();
                shuffleButton.setChecked(radioPlayer.isShuffleOn());
            }
        });

        repeatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                radioPlayer.toggleRepeatOne();
                repeatButton.setChecked(radioPlayer.isRepeatOn());
            }
        });

        // Replace your current click listener with this:
        trackSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedTrack = trackSelectBox.getSelected();
                if (selectedTrack != null && !selectedTrack.isEmpty()) {
                    radioPlayer.playTrackByName(selectedTrack);
                    playPauseButton.setText("Pause");
                    if (client != null) {
                        client.send(new RadioChangedMessage(
                            App.getCurrentGame().getCurrentPlayer().getUser().getHashId(),
                            selectedTrack
                        ));
                    }
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicControlsTable.setVisible(false);
                friendsTable.setVisible(true);
                refreshFriendsTable();
            }
        });

        radioPlayer.addPlaybackListener(() -> {
            Gdx.app.postRunnable(() -> {
                updateTrackSelection();
            });
        });

        // Add a periodic update check
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (radioPlayer.getCurrentTrackName() != null &&
                    !radioPlayer.getCurrentTrackName().equals(trackSelectBox.getSelected())) {
                    updateTrackSelection();
                }
            }
        }, 0, 0.5f); // Check every 0.5 seconds



        // Layout
        musicControlsTable.add(trackSelectBox).width(550).center().row();
        Table navTable = new Table();
        navTable.defaults().width(150).pad(10); // Smaller buttons with minimal padding
        navTable.add(prevButton);
        navTable.add(playPauseButton);
        navTable.add(nextButton);
        musicControlsTable.add(navTable).padTop(5).row(); // Small top padding

        // Bottom row: mode buttons
        Table modeTable = new Table();
        modeTable.defaults().width(150).pad(10); // Wider buttons for text
        modeTable.add(shuffleButton);
        modeTable.add(repeatButton);
        musicControlsTable.add(modeTable).padTop(5).row(); // Minimal top padding

        Table backTable = new Table();
        backTable.defaults().width(150).pad(10);
        backTable.add(backButton);
        musicControlsTable.add(backTable).padTop(5);
    }

    private void refreshFriendsTable() {
        friendsTable.clearChildren();

        Player player = App.getCurrentGame().getCurrentPlayer();
        HashMap<Player, FriendshipData> friendships = player.getFriendships();

        for (Map.Entry<Player, FriendshipData> entry : friendships.entrySet()) {
            Player friend = entry.getKey();
            FriendshipData data = entry.getValue();

            Label nameLabel = new Label(friend.getNickName() + "  ", skin);

            ProgressBar bar = new ProgressBar(0, data.getThresholdForLevel(data.getLevel()), 1, false, skin);
            bar.setValue(data.getXp());

            String tooltipText = friend.getNickName() + " (Level " + data.getLevel() + ")\n"
                + "XP: " + data.getXp();
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

            TextButton tradeButton = new TextButton("Trade", skin);
            tradeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // 1) Inject dependencies:
                    tradeWindow.setDependencies(
                        inventoryWindow,               // your InventoryWindow instance
                        new TradeController()
                    );
                    TradeRequestMessage msg = new TradeRequestMessage(player.getUser().getHashId(), friend.getUser().getHashId());
                    client.send(msg);

                    // 2) Show the popup:
                    popup.setVisible(false);
                    tradeWindow.showLoadingFor(friend);
                }
            });

            TextButton radio = new TextButton("Radio", skin);
            radio.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.getCurrentListeningTo() != null && player.getCurrentListeningTo().equals(friend)) {
                        // If already listening to this friend, stop
                        radioPlayer.stop();
                        player.setCurrentListeningTo(null);
                        radio.setText("Radio");
                    } else {
                        // If not listening, start listening
                        client.send(new RadioRequestMessage(
                            player.getUser().getHashId(),
                            friend.getUser().getHashId()
                        ));
                        radio.setText("Stop");
                    }
                }
            });

            //stage.addActor(tooltip.getContainer());
            friendsTable.center();
            Table friendsPart = new Table();
            friendsPart.defaults().pad(10);
            friendsPart.add(nameLabel);
            friendsPart.add(bar);
            friendsPart.add(giftButton);
            friendsPart.add(tradeButton);
            friendsPart.add(radio);
            friendsTable.add(friendsPart).row();
        }

        TextButton tradeHistoryButton = new TextButton("Trade History", skin);
        tradeHistoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showTradeHistoryUI();
            }
        });

        TextButton publicChat = new TextButton("PublicChat", skin);
        publicChat.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                publicChatScreen.showChat(App.getCurrentGame().getCurrentPlayer());
                toggleVisibility();
            }
        });

        TextButton musicButton = new TextButton("My Radio", skin);
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMusic();
            }
        });

        Table generalTable = new Table();
        generalTable.defaults().width(250).pad(10);
        generalTable.add(tradeHistoryButton);
        generalTable.add(publicChat);
        generalTable.add(musicButton);
        friendsTable.add(generalTable);


        center(stage);
    }

    private void showMusic() {
        giftOptionsTable.clear();
        giftOptionsTable.setVisible(false);
        friendsTable.setVisible(false);
        giftHistoryTable.setVisible(false);
        tradeHistoryTable.setVisible(false);
        newGiftTable.setVisible(false);
        musicControlsTable.setVisible(true);

        updateTrackSelection();
    }

    private void showGiftOptions(Player friend) {
        this.selectedFriend = friend;
        giftOptionsTable.clear();
        giftOptionsTable.setVisible(true);
        friendsTable.setVisible(false);
        giftHistoryTable.setVisible(false);
        tradeHistoryTable.setVisible(false);
        newGiftTable.setVisible(false);
        musicControlsTable.setVisible(false);

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

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGiftTable.setVisible(false);
                giftOptionsTable.setVisible(true);
            }
        });

        // Inventory grid
        Table inventoryGrid = new Table();
        List<GameObject> giftableItems = getGiftableItems();
        buildGiftGrid(inventoryGrid, giftableItems, 3, 8);

        // Amount display (Label - non editable)
        final Label amountLabel = new Label("1", skin);
        amountLabel.setAlignment(Align.center);
        amountLabel.setWidth(48);
        amountLabel.setHeight(28);

        // - button
        TextButton minusButton = new TextButton("-", skin);
        minusButton.getLabel().setFontScale(1f);
        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    int current = Integer.parseInt(amountLabel.getText().toString());
                    if (current > 1) {
                        amountLabel.setText(String.valueOf(current - 1));
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        // + button
        TextButton plusButton = new TextButton("+", skin);
        plusButton.getLabel().setFontScale(1f);
        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    int current = Integer.parseInt(amountLabel.getText().toString());
                    if (selectedGiftItem != null) {
                        if (current < selectedGiftItem.getNumber()) {
                            amountLabel.setText(String.valueOf(current + 1));
                        }
                    } else {
                        amountLabel.setText(String.valueOf(current + 1));
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        // Amount table: consistent sizes and padding (no crazy tiny numbers)
        Table amountTable = new Table();
        amountTable.add(minusButton).pad(4).fillX().expand();
        amountTable.add(amountLabel).width(48).height(28).pad(4);
        amountTable.add(plusButton).pad(4).fillX().expand();

        // Error label
        final Label errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);

        // Gift button
        TextButton giftButton = new TextButton("Gift", skin);
        giftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedGiftItem == null) {
                    errorLabel.setText("No item selected.");
                    errorLabel.setColor(Color.RED);
                    return;
                }

                int amount;
                try {
                    amount = Integer.parseInt(amountLabel.getText().toString());
                    if (amount <= 0 || amount > selectedGiftItem.getNumber()) {
                        errorLabel.setText("Invalid amount (1-" + selectedGiftItem.getNumber() + ")");
                        errorLabel.setColor(Color.RED);
                        return;
                    }
                } catch (NumberFormatException e) {
                    errorLabel.setText("Invalid input.");
                    errorLabel.setColor(Color.RED);
                    return;
                }

                Result result = controller.gift(selectedFriend, selectedGiftItem.getObjectType(), amount);
                errorLabel.setText(result.message());
                errorLabel.setColor(result.isSuccessful() ? Color.GREEN : Color.RED);

                if (result.isSuccessful()) {
                    client.send(new NewGiftMessage(App.getCurrentGame().getCurrentPlayer().getUser().getHashId(),
                        selectedFriend.getUser().getHashId(), selectedGiftItem));
                    amountLabel.setText("1"); // reset to default 1
                    selectedGiftItem = null;
                    List<GameObject> updatedItems = getGiftableItems();
                    buildGiftGrid(inventoryGrid, updatedItems, 3, 8);
                }
            }
        });

        // Layout
        newGiftTable.add(title).padBottom(10).row();
        newGiftTable.add(inventoryGrid).padBottom(10).row();
        newGiftTable.add(amountTable).padBottom(10).row();
        newGiftTable.add(giftButton).padBottom(5).row();
        newGiftTable.add(backButton).padTop(5).row();
        newGiftTable.add(errorLabel).padTop(5).row();
    }




    // Field to track selected gift item
    private GameObject selectedGiftItem;

    // Get non-tool items for gifting
    private List<GameObject> getGiftableItems() {
        Player player = App.getCurrentGame().getCurrentPlayer();
        List<GameObject> allItems = player.getCurrentBackPack().getNonEmptyItems();
        List<GameObject> giftableItems = new ArrayList<>();

        for (GameObject item : allItems) {
            if (!(item instanceof Tool)) {
                giftableItems.add(item);
            }
        }
        return giftableItems;
    }

    // Build the gift grid (similar to trade screen)
    private void buildGiftGrid(Table grid, List<GameObject> items, int rows, int cols) {
        grid.clear();
        grid.defaults().size(64).pad(4); // Slot size and padding

        BitmapFont quantityFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 16, Color.WHITE);

        // Create slot background
        Drawable slotBackground = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(64, 64, new Color(0.3f, 0.3f, 0.3f, 0.7f));

        // Create selection border
        Pixmap borderPixmap = new Pixmap(68, 68, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(Color.YELLOW);
        borderPixmap.drawRectangle(0, 0, 67, 67);
        Drawable selectionBorder = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();

        for (int i = 0; i < rows * cols; i++) {
            Stack slotStack = new Stack();
            slotStack.setSize(64, 64);

            // Background
            slotStack.add(new Image(slotBackground));

            // Item content
            if (i < items.size()) {
                GameObject item = items.get(i);

                // Item image
                Texture texture = item.getObjectType().getTexture();
                Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
                itemImage.setScaling(Scaling.fit);
                slotStack.add(itemImage);

                // Quantity label
                if (item.getNumber() > 1) {
                    Label quantityLabel = new Label(String.valueOf(item.getNumber()),
                        new Label.LabelStyle(quantityFont, Color.WHITE));

                    Table labelTable = new Table();
                    labelTable.setFillParent(true);
                    labelTable.bottom().right();
                    labelTable.add(quantityLabel).pad(0, 0, 4, 4);
                    slotStack.add(labelTable);
                }

                // Selection border if selected
                if (item == selectedGiftItem) {
                    slotStack.add(new Image(selectionBorder));
                }

                // Click listener
                final GameObject finalItem = item;
                slotStack.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectedGiftItem = finalItem;
                        buildGiftGrid(grid, items, rows, cols); // Rebuild to show selection
                    }
                });
            }

            grid.add(slotStack);
            if ((i + 1) % cols == 0) grid.row();
        }
    }

    public void showGiftHistoryUI(Player friend) {
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

    private void showTradeHistoryUI() {
        tradeHistoryTable.clear();
        tradeHistoryTable.setVisible(true);
        friendsTable.setVisible(false);
        giftOptionsTable.setVisible(false);
        giftHistoryTable.setVisible(false);
        newGiftTable.setVisible(false);
        musicControlsTable.setVisible(false);

        Label title = new Label("Trade History", skin, "Impact");
        title.setFontScale(1.2f);

        Player currentPlayer = App.getCurrentGame().getCurrentPlayer();
        List<Trade> tradeList = currentPlayer.getArchiveTrades();

        // Filter trades that involve the selected friend
        Table historyContent = new Table();
        boolean hasTradesWithFriend = false;

        for (Trade trade : tradeList) {
            // Check if this trade involves the selected friend
            hasTradesWithFriend = true;

            // Determine if current player was requester or responder
            boolean isRequester = trade.getRequest().equals(currentPlayer);
            Player otherPlayer = isRequester ? trade.getResponse() : trade.getRequest();

            // Create trade entry
            Table tradeEntry = new Table();
            tradeEntry.defaults().pad(5);

            // Trade ID
            Label tradeIdLabel = new Label("Trade #" + trade.getTradeID() + " -> " + currentPlayer.getNickName() + " & " + otherPlayer.getNickName(), skin);
            tradeIdLabel.setFontScale(1.1f);
            tradeEntry.add(tradeIdLabel).colspan(2).left().row();


            // Add separator
            Label separator = new Label("─────────────────────────────", skin);
            separator.setColor(Color.GRAY);
            tradeEntry.add(separator).colspan(2).padTop(10).padBottom(5).row();

            historyContent.add(tradeEntry).fillX().row();

        }

        if (!hasTradesWithFriend) {
            Label noTradesLabel = new Label("No trades completed", skin, "Impact");
            noTradesLabel.setColor(Color.GRAY);
            historyContent.add(noTradesLabel).pad(20);
        }

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tradeHistoryTable.setVisible(false);
                friendsTable.setVisible(true);
                refreshFriendsTable();
            }
        });

        // Create scrollable content for trade history
        ScrollPane historyScrollPane = new ScrollPane(historyContent, skin);
        historyScrollPane.setFadeScrollBars(false);
        historyScrollPane.setScrollingDisabled(true, false);

        // Layout
        tradeHistoryTable.add(title).padBottom(10).row();
        tradeHistoryTable.add(historyScrollPane).expand().fill().padBottom(10).row();
        tradeHistoryTable.add(backButton).padTop(5);
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
                client.send(new GiftRateMessage(App.getCurrentGame().getCurrentPlayer().getUser().getHashId(),
                    selectedFriend.getUser().getHashId(), gift.getId(), rating));
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
            tradeHistoryTable.setVisible(false);
            newGiftTable.setVisible(false);
            musicControlsTable.setVisible(false);
            refreshFriendsTable();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        popup.remove();
    }

    public Window getPopup() {
        return popup;
    }

    public TradeWindow getTradeWindow() {
        return tradeWindow;
    }

    public PublicChatScreen getPublicChatScreen() {
        return publicChatScreen;
    }

    private void updateTrackList() {
        if (radioPlayer.getPlaylist().isEmpty()) {
            trackSelectBox.setItems("No tracks found");
        } else {
            trackSelectBox.setItems(radioPlayer.getPlaylist().toArray(new String[0]));
            // Select first track by default
            trackSelectBox.setSelectedIndex(0);
        }
    }

    private void updateTrackSelection() {
        if (radioPlayer.getCurrentTrackName() != null) {
            int index = radioPlayer.getPlaylist().indexOf(radioPlayer.getCurrentTrackName());
            if (index >= 0 && index != trackSelectBox.getSelectedIndex()) {
                trackSelectBox.setSelectedIndex(index);
            }
            playPauseButton.setText(radioPlayer.isPlaying() ? "Pause" : "Play");
        }
    }
}
