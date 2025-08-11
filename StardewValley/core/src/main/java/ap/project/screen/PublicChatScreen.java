package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.game.Player;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.NewPublicChatMessage;
import ap.project.network.shared.messages.PlayerTaggedNotification;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PublicChatScreen {
    private final Window chatWindow;
    private final Stage stage;
    private final Skin skin;
    private final Table chatContentTable;
    private final ScrollPane chatScrollPane;
    private final Label chatHistoryLabel;
    private final TextField messageInput;
    private final TextButton sendButton;
    private final TextButton closeButton;
    private Player currentPlayer;
    private boolean isVisible = false;
    private final WorldScreen worldScreen;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final GameClient client;

    // Local storage for chat messages
    private final List<String> chatHistory = new ArrayList<>();

    public PublicChatScreen(Stage stage, WorldScreen worldScreen) {
        this.stage = stage;
        this.worldScreen = worldScreen;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        this.client = GameClient.getInstance();

        // Window setup
        chatWindow = new Window("Public Chat", skin);
        chatWindow.setVisible(false);
        chatWindow.setMovable(true);
        chatWindow.setModal(true);
        chatWindow.defaults().pad(5);

        // Chat history label
        chatContentTable = new Table(skin);
        chatContentTable.top().left();
        chatHistoryLabel = new Label("", skin);
        chatHistoryLabel.setWrap(true);
        chatHistoryLabel.setAlignment(Align.topLeft);
        chatContentTable.add(chatHistoryLabel).growX().pad(10).row();

        // ScrollPane
        chatScrollPane = new ScrollPane(chatContentTable, skin);
        chatScrollPane.setFadeScrollBars(false);
        chatScrollPane.setScrollingDisabled(true, false); // only vertical
        chatScrollPane.setOverscroll(false, false);

        // Input field & buttons
        messageInput = new TextField("", skin);
        messageInput.setMessageText("Type your message...");
        sendButton = new TextButton("Send", skin);
        closeButton = new TextButton("Close", skin);

        setupLayout();
        setupEventHandlers();

        stage.addActor(chatWindow);
    }

    private void setupLayout() {
        // Create a light-grey background
        Drawable scrollBg = GameAssetsManager
            .getGameAssetsManager()
            .createColoredDrawable(1, 1, new Color(0.9f, 0.9f, 0.9f, 1f));

        // Wrap the ScrollPane in a Container
        Container<ScrollPane> scrollContainer = new Container<>(chatScrollPane);
        scrollContainer.background(scrollBg);
        scrollContainer.fill();

        chatWindow.add(scrollContainer)
            .expand().fill()
            .colspan(2)
            .pad(10);
        chatWindow.row();

        chatWindow.add().height(10).colspan(2).row();

        // Input + send
        chatWindow.add(messageInput)
            .expandX().fillX()
            .pad(5);
        chatWindow.add(sendButton)
            .width(100).pad(5);
        chatWindow.row();

        // Close button
        chatWindow.add(closeButton)
            .colspan(2)
            .width(150)
            .pad(10);

        chatWindow.pack();
        chatWindow.setSize(800, 600);
    }

    private void setupEventHandlers() {
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendMessage(messageInput.getText().trim());
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideChat();
            }
        });
    }

    public void showChat(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        chatWindow.getTitleLabel().setText("Public Chat");
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

    public void sendMessage(String msg) {
        if (msg.isEmpty()) return;

        detectAndNotifyTags(msg); // <-- NEW

        String stamp = timeFormat.format(new Date());
        String line = "[" + stamp + "] " + currentPlayer.getNickName() + ": " + msg;

        chatHistory.add(line);
        appendToChat(line);

        client.send(new NewPublicChatMessage(currentPlayer.getUser().getHashId(), msg));

        messageInput.setText("");
    }


    public void loadChatHistory() {
        StringBuilder sb = new StringBuilder();
        for (String m : chatHistory) {
            sb.append(m).append("\n");
        }
        chatHistoryLabel.setText(sb.toString());
        chatHistoryLabel.setHeight(chatHistoryLabel.getPrefHeight());
        chatContentTable.invalidateHierarchy();

        // Scroll to bottom
        Gdx.app.postRunnable(() -> {
            chatScrollPane.layout();
            chatScrollPane.setScrollY(chatScrollPane.getMaxY());
        });
    }

    public void addMessage(String message) {
        // Add to local history
        chatHistory.add(message);

        // Update UI if visible
        if (isVisible) {
            appendToChat(message);
        }
    }

    private void appendToChat(String message) {
        String currentText = chatHistoryLabel.getText().toString();
        chatHistoryLabel.setText(currentText + message + "\n");
        chatHistoryLabel.setHeight(chatHistoryLabel.getPrefHeight());
        chatContentTable.invalidateHierarchy();

        // Scroll to bottom
        Gdx.app.postRunnable(() -> {
            chatScrollPane.layout();
            chatScrollPane.setScrollY(chatScrollPane.getMaxY());
        });
    }

    private void detectAndNotifyTags(String msg) {
        // Simple regex to detect tags like @Name
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(msg);

        while (matcher.find()) {
            String taggedNick = matcher.group(1);
            Player taggedPlayer = App.getCurrentGame().getPlayerByNickname(taggedNick);
            if (taggedPlayer == null || taggedPlayer.equals(currentPlayer)) {
                System.out.println("tagged player not found: " + taggedNick);
                return;
            }
            client.send(new PlayerTaggedNotification(currentPlayer.getUser().getHashId(), taggedPlayer.getUser().getHashId()));
        }
    }


    private void centerWindow() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        chatWindow.setPosition((w - chatWindow.getWidth()) / 2f,
            (h - chatWindow.getHeight()) / 2f);
    }

    public boolean handleKeyDown(int keycode) {
        if (!isVisible) return false;
        if (keycode == Input.Keys.ENTER) {
            sendMessage(messageInput.getText().trim());
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
