package ap.project.screen;

import ap.project.control.game.activities.CommunicateController;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipData;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.NewChatMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatScreen {
    private final Window chatWindow;
    private final Stage stage;
    private final Skin skin;

    private final Table chatContentTable;
    private final ScrollPane chatScrollPane;
    private final Label chatHistoryLabel;
    private final TextField messageInput;
    private final TextButton sendButton;
    private final TextButton closeButton;
    private final CommunicateController controller;
    private Player currentPlayer, chatPartner;
    private boolean isVisible = false;
    private final WorldScreen worldScreen;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final GameClient client;

    public ChatScreen(Stage stage, WorldScreen worldScreen, CommunicateController controller) {
        this.stage = stage;
        this.worldScreen = worldScreen;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // — Window setup —
        chatWindow = new Window("Chat", skin);
        chatWindow.setVisible(false);
        chatWindow.setMovable(true);
        chatWindow.setModal(true);
        chatWindow.defaults().pad(5);

        // — Chat history label inside a Table so it can grow —
        chatContentTable = new Table(skin);
        chatContentTable.top().left();
        chatHistoryLabel = new Label("", skin);
        chatHistoryLabel.setWrap(true);
        chatHistoryLabel.setAlignment(Align.topLeft);
        chatContentTable.add(chatHistoryLabel).growX().pad(10).row();

        // — ScrollPane with its own background so you see the border —
        chatScrollPane = new ScrollPane(chatContentTable, skin);
        chatScrollPane.setFadeScrollBars(false);
        chatScrollPane.setScrollbarsVisible(true);
        chatScrollPane.setScrollingDisabled(true, false); // only vertical
        chatScrollPane.setOverscroll(false, false);
        // give it a light grey bg so the edge is obvious:

        // — Input field & buttons —
        messageInput = new TextField("", skin);
        messageInput.setMessageText("Type your message…");
        sendButton = new TextButton("Send", skin);
        closeButton = new TextButton("Close", skin);
        this.controller = controller;
        this.client = GameClient.getInstance();

        setupLayout();
        setupEventHandlers();

        stage.addActor(chatWindow);
    }

    private void setupLayout() {
        // create a light-grey drawable once
        Drawable scrollBg = GameAssetsManager
            .getGameAssetsManager()
            .createColoredDrawable(1, 1, new Color(0.9f, 0.9f, 0.9f, 1f));

        // wrap the ScrollPane in a Container so its bg is drawn
        Container<ScrollPane> scrollContainer = new Container<>(chatScrollPane);
        scrollContainer.background(scrollBg);
        scrollContainer.fill();  // let the scrollPane expand inside

        // now add the container instead of the raw scrollPane
        chatWindow.add(scrollContainer)
            .expand().fill()
            .colspan(2)
            .pad(10);
        chatWindow.row();

        // spacer
        chatWindow.add().height(10).colspan(2).row();

        // input + send
        chatWindow.add(messageInput)
            .expandX().fillX()
            .pad(5);
        chatWindow.add(sendButton)
            .width(100).pad(5);
        chatWindow.row();

        // close button
        chatWindow.add(closeButton)
            .colspan(2)
            .width(150)
            .pad(10);

        chatWindow.pack();
        chatWindow.setSize(800, 600);
    }


    private void setupEventHandlers() {
        sendButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                client.send(new NewChatMessage(currentPlayer.getUser().getHashId(), chatPartner.getUser().getHashId(), messageInput.getText().trim()));
                sendMessage(messageInput.getText().trim(), currentPlayer, chatPartner);
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                hideChat();
            }
        });
    }

    public void showChat(Player currentPlayer, Player chatPartner) {
        this.currentPlayer = currentPlayer;
        this.chatPartner = chatPartner;
        chatWindow.getTitleLabel().setText("Chat with " + chatPartner.getNickName());
        loadChatHistory();
        isVisible = true;
        chatWindow.setVisible(true);
        centerWindow();
        stage.setKeyboardFocus(messageInput);
        messageInput.setText("");
    }

    public void hideChat() {
        isVisible = false;
        chatWindow.setVisible(false);
        stage.setKeyboardFocus(null);
        worldScreen.restoreInputFocus();
    }

    public void sendMessage(String msg, Player sender, Player receiver) {
        if (msg.isEmpty()) return;

        controller.talk(receiver);
        String stamp = timeFormat.format(new Date());
        String line = "[" + stamp + "] " + sender.getNickName() + ": " + msg;

        // Only update current player's data
        FriendshipData friendship = sender.getFriendships().get(receiver);
        if (friendship != null) {
            friendship.getMessageHistory().add(line);
        }

        messageInput.setText("");
        loadChatHistory();  // Refresh UI immediately
    }

    public void loadChatHistory() {
        // Always use current player's perspective
        FriendshipData friendshipData = currentPlayer.getFriendships().get(chatPartner);

        StringBuilder sb = new StringBuilder();
        if (friendshipData != null && !friendshipData.getMessageHistory().isEmpty()) {
            for (String m : friendshipData.getMessageHistory()) {
                sb.append(m).append("\n");
            }
        } else {
            sb.append("No chat history yet. Start a conversation!\n");
        }

        chatHistoryLabel.setText(sb.toString());
        chatHistoryLabel.setHeight(chatHistoryLabel.getPrefHeight());
        chatContentTable.invalidateHierarchy();

        // Scroll to bottom after layout update
        Gdx.app.postRunnable(() -> {
            chatScrollPane.layout();
            chatScrollPane.setScrollY(chatScrollPane.getMaxY());
        });
    }

    private void centerWindow() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        chatWindow.setPosition((w-chatWindow.getWidth())/2f,
            (h-chatWindow.getHeight())/2f);
    }

    public boolean handleKeyDown(int keycode) {
        if (!isVisible) return false;
        if (keycode == Input.Keys.ENTER) {
            client.send(new NewChatMessage(currentPlayer.getUser().getHashId(), chatPartner.getUser().getHashId(), messageInput.getText().trim()));
            sendMessage(messageInput.getText().trim(), currentPlayer, chatPartner);
            return true;
        } else if (keycode == Input.Keys.ESCAPE) {
            hideChat();
            return true;
        }
        return false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        chatWindow.remove();
    }
}
