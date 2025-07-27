package ap.project.screen;

import ap.project.model.App.GameAssetsManager;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipData;
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

    private Player currentPlayer, chatPartner;
    private boolean isVisible = false;
    private final WorldScreen worldScreen;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ChatScreen(Stage stage, WorldScreen worldScreen) {
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
                sendMessage();
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

    private void sendMessage() {
        String msg = messageInput.getText().trim();
        if (msg.isEmpty()) return;

        String stamp = timeFormat.format(new Date());
        String line  = "["+stamp+"] "+currentPlayer.getNickName()+": "+msg;

        FriendshipData meData = currentPlayer.getFriendships().get(chatPartner);
        FriendshipData themData = chatPartner.getFriendships().get(currentPlayer);
        if (meData   != null) meData.getMessageHistory().add(line);
        if (themData != null) themData.getMessageHistory().add(line);

        messageInput.setText("");
        loadChatHistory();
        // scroll to bottom of content
        Gdx.app.postRunnable(() -> {
            chatScrollPane.layout();
            chatScrollPane.setScrollY(chatScrollPane.getMaxY());
        });
    }

    private void loadChatHistory() {
        StringBuilder sb = new StringBuilder();
        FriendshipData fd = currentPlayer.getFriendships().get(chatPartner);
        if (fd != null && !fd.getMessageHistory().isEmpty()) {
            for (String m : fd.getMessageHistory()) sb.append(m).append("\n");
        } else {
            sb.append("No chat history yet. Start a conversation!\n");
        }
        chatHistoryLabel.setText(sb.toString());
        // force the label/table to resize
        chatHistoryLabel.setHeight(chatHistoryLabel.getPrefHeight());
        chatContentTable.invalidateHierarchy();
        chatScrollPane.layout();
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
            sendMessage();
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
