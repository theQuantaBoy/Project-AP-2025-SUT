package ap.project.screen;

import ap.project.Main;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.CharacterType;
import ap.project.model.enums.Gender;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.Season;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.*;
import ap.project.visual.TextBoxSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

public class PreLobbyScreen implements Screen
{
    private static PreLobbyScreen instance;
    private final User user;
    private final GameClient client;

    private final TextBoxSystem textBoxSystem = new TextBoxSystem();

    private String onlineUsersText = "";
    private String activeLobbiesText = "";

    private Stage stage;
    private Image background;
    private Label statusLabel;
    private Label usernameLabel;

    // Selection state tracking
    private int currentAvatarIndex = 0;
    private int currentMapIndex = 0;
    private Gender selectedGender = Gender.MALE;

    // UI Components
    private TextButton gamePrefsButton;
    private TextButton createLobbyButton;
    private TextButton joinLobbyButton;
    private TextButton onlineUsersButton;
    private TextButton activeLobbiesButton;
    private TextButton backButton;

    // Dialogs
    private Dialog gamePrefsDialog;
    private Dialog avatarSelectionDialog;
    private Dialog mapSelectionDialog;
    private Dialog createLobbyDialog;
    private Dialog joinLobbyDialog;
    private Dialog onlineUsersDialog;
    private Dialog activeLobbiesDialog;

    // Dialog components
    private TextField lobbyNameField;
    private TextField lobbyPasswordField;
    private CheckBox privateCheckbox;
    private CheckBox visibleCheckbox;
    private TextField joinLobbyIDField;
    private TextField joinLobbyPasswordField;
    private Label avatarNameLabel;
    private Label mapNameLabel;

    // Preview actors
    private TexturePreviewActor avatarPreviewActor;
    private TexturePreviewActor mapPreviewActor;

    // User/lobby lists
    private Table onlineUsersTable;
    private Table activeLobbiesTable;

    public boolean isConnected;
    public float lastConnectionTime;

    // Custom actor for texture preview
    private static class TexturePreviewActor extends Actor
    {
        private TextureRegion textureRegion;

        public void setTexture(Texture texture)
        {
            this.textureRegion = texture != null ? new TextureRegion(texture) : null;
        }

        @Override
        public void draw(Batch batch, float parentAlpha)
        {
            if (textureRegion != null)
            {
                // Calculate aspect ratio
                float textureWidth = textureRegion.getRegionWidth();
                float textureHeight = textureRegion.getRegionHeight();
                float aspectRatio = textureWidth / textureHeight;

                // Calculate dimensions to fit in actor bounds
                float drawWidth = getWidth();
                float drawHeight = getHeight();

                if (drawWidth / drawHeight > aspectRatio)
                {
                    drawWidth = drawHeight * aspectRatio;
                } else
                {
                    drawHeight = drawWidth / aspectRatio;
                }

                // Center the texture
                float x = getX() + (getWidth() - drawWidth) / 2;
                float y = getY() + (getHeight() - drawHeight) / 2;

                // Draw the texture
                batch.draw(textureRegion, x, y, drawWidth, drawHeight);
            }
        }
    }

    public PreLobbyScreen()
    {
        user = App.getCurrentUser();
        client = GameClient.getInstance();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Setup UI components
        createBackground();
        createStatusLabels();
        createButtons();
        createDialogs();

        arrangeLayout();
        addListeners();

        currentAvatarIndex = user.getCharacterChoice();
        currentMapIndex = user.getMapChoice();

        instance = this;
    }

    private void createBackground()
    {
        background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        background.setFillParent(true);
        stage.addActor(background);
    }

    private void createStatusLabels()
    {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();
        statusLabel = new Label("Status: Online", skin, "default");
        usernameLabel = new Label(App.getCurrentUser().getUsername(), skin, "default");
    }

    private void createButtons()
    {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        gamePrefsButton = new TextButton("Game Preferences", skin);
        createLobbyButton = new TextButton("Create Lobby", skin);
        joinLobbyButton = new TextButton("Join Lobby", skin);
        onlineUsersButton = new TextButton("Online Users", skin);
        activeLobbiesButton = new TextButton("Active Lobbies", skin);
        backButton = new TextButton("Go Back", skin);
    }

    private void createDialogs()
    {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Game Preferences Dialog (larger size)
        gamePrefsDialog = new Dialog("Game Preferences", skin);
        gamePrefsDialog.getContentTable().pad(50);
        createGamePrefsDialogContent();

        // Avatar Selection Dialog (larger size)
        avatarSelectionDialog = new Dialog("Select Avatar", skin);
        avatarSelectionDialog.getContentTable().pad(50);
        createAvatarDialogContent();

        // Map Selection Dialog (larger size)
        mapSelectionDialog = new Dialog("Select Map", skin);
        mapSelectionDialog.getContentTable().pad(50);
        createMapDialogContent();

        // Create Lobby Dialog (larger size)
        createLobbyDialog = new Dialog("Create Lobby", skin);
        createLobbyDialog.getContentTable().pad(50);
        createLobbyDialogContent();

        // Join Lobby Dialog (larger size)
        joinLobbyDialog = new Dialog("Join Lobby", skin);
        joinLobbyDialog.getContentTable().pad(50);
        createJoinLobbyDialogContent();

        // Online Users Dialog (larger size)
        onlineUsersDialog = new Dialog("Online Users", skin);
        onlineUsersDialog.getContentTable().pad(50);
        createOnlineUsersDialogContent();

        // Active Lobbies Dialog (larger size)
        activeLobbiesDialog = new Dialog("Active Lobbies", skin);
        activeLobbiesDialog.getContentTable().pad(50);
        createActiveLobbiesDialogContent();
    }

    private void createGamePrefsDialogContent()
    {
        Table content = gamePrefsDialog.getContentTable();
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Avatar selection button
        TextButton avatarButton = new TextButton("Avatar", skin);
        avatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                avatarSelectionDialog.show(stage);
            }
        });
        content.add(avatarButton).pad(15).minWidth(200).minHeight(60).row();

        // Map selection button
        TextButton mapButton = new TextButton("Map", skin);
        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapSelectionDialog.show(stage);
            }
        });
        content.add(mapButton).pad(15).minWidth(200).minHeight(60).row();

        // Gender selection
        SelectBox<Gender> genderSelect = new SelectBox<>(skin);
        genderSelect.setItems(Gender.values());
        genderSelect.setSelected(selectedGender);
        genderSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedGender = genderSelect.getSelected();
            }
        });

        Table genderTable = new Table();
        genderTable.add(new Label("Gender:", skin)).padRight(10);
        genderTable.add(genderSelect).width(150);
        content.add(genderTable).pad(15).minWidth(200).minHeight(60).row();

        // Close button
        gamePrefsDialog.button("Close");
    }

    private void createAvatarDialogContent()
    {
        Table content = avatarSelectionDialog.getContentTable();
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Name label
        avatarNameLabel = new Label("", skin);
        content.add(avatarNameLabel).padBottom(10).row();

        // Navigation buttons and preview area
        Table navTable = new Table();

        TextButton prevButton = new TextButton("<", skin);
        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentAvatarIndex = (currentAvatarIndex - 1 + CharacterType.values().length) % CharacterType.values().length;
                user.setCharacterChoice(currentAvatarIndex);
                updateAvatarPreview();
            }
        });

        // Create preview actor
        avatarPreviewActor = new TexturePreviewActor();

        TextButton nextButton = new TextButton(">", skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentAvatarIndex = (currentAvatarIndex + 1) % CharacterType.values().length;
                user.setCharacterChoice(currentAvatarIndex);
                updateAvatarPreview();
            }
        });

        navTable.add(prevButton).width(80).padRight(20);
        navTable.add(avatarPreviewActor).width(300).height(300).pad(20); // Larger preview
        navTable.add(nextButton).width(80).padLeft(20);

        content.add(navTable).row();
        avatarSelectionDialog.button("Close");

        // Initialize preview
        updateAvatarPreview();
    }

    private void updateAvatarPreview()
    {
        CharacterType selected = CharacterType.values()[currentAvatarIndex];
        avatarNameLabel.setText(selected.name());

        // Get texture
        Texture texture = selected.getAvatarTexture();
        avatarPreviewActor.setTexture(texture);
    }

    private void createMapDialogContent()
    {
        Table content = mapSelectionDialog.getContentTable();
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Name label
        mapNameLabel = new Label("", skin);
        content.add(mapNameLabel).padBottom(10).row();

        // Navigation buttons and preview area
        Table navTable = new Table();

        TextButton prevButton = new TextButton("<", skin);
        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArrayList<MapTypes> farms = MapTypes.getFarms();
                currentMapIndex = (currentMapIndex - 1 + farms.size()) % farms.size();
                user.setMapChoice(currentMapIndex);
                updateMapPreview();
            }
        });

        // Create preview actor
        mapPreviewActor = new TexturePreviewActor();

        TextButton nextButton = new TextButton(">", skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArrayList<MapTypes> farms = MapTypes.getFarms();
                currentMapIndex = (currentMapIndex + 1) % farms.size();
                user.setMapChoice(currentMapIndex);
                updateMapPreview();
            }
        });

        navTable.add(prevButton).width(80).padRight(20);
        navTable.add(mapPreviewActor).width(400).height(300).pad(20); // Larger preview
        navTable.add(nextButton).width(80).padLeft(20);

        content.add(navTable).row();
        mapSelectionDialog.button("Close");

        // Initialize preview
        updateMapPreview();
    }

    private void updateMapPreview()
    {
        ArrayList<MapTypes> farms = MapTypes.getFarms();
        if (!farms.isEmpty()) {
            MapTypes selected = farms.get(currentMapIndex);
            mapNameLabel.setText(selected.name());

            // Get texture
            Texture texture = MapTypes.getMiniMapTexture(selected, Season.Spring);
            mapPreviewActor.setTexture(texture);
        }
    }

    private void createLobbyDialogContent()
    {
        Table content = createLobbyDialog.getContentTable();
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Name field
        lobbyNameField = new TextField("", skin);
        content.add(new Label("Lobby Name:", skin)).padRight(10);
        content.add(lobbyNameField).width(400).pad(10).row(); // Wider field

        // Password field
        lobbyPasswordField = new TextField("", skin);
        lobbyPasswordField.setPasswordMode(true);
        lobbyPasswordField.setPasswordCharacter('*');
        content.add(new Label("Password:", skin)).padRight(10);
        content.add(lobbyPasswordField).width(400).pad(10).row(); // Wider field

        // Checkboxes
        privateCheckbox = new CheckBox(" Private", skin);
        visibleCheckbox = new CheckBox(" Visible", skin);
        content.add(privateCheckbox).colspan(2).pad(10).row();
        content.add(visibleCheckbox).colspan(2).pad(10).row();

        // Action buttons
        TextButton createButton = new TextButton("Create", skin);
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                String name = lobbyNameField.getText();
                String password = lobbyPasswordField.getText();
                boolean isPrivate = privateCheckbox.isChecked();
                boolean isVisible = visibleCheckbox.isChecked();

                if (isPrivate && password.isEmpty())
                {
                    textBoxSystem.showTextBox("a private lobby must have a password!");
                } else
                {
                    handleLobbyCreation(name, password, isPrivate, isVisible);
                }
            }
        });

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createLobbyDialog.hide();
            }
        });

        createLobbyDialog.getButtonTable().add(createButton).pad(10);
        createLobbyDialog.getButtonTable().add(closeButton).pad(10);
    }

    private void createJoinLobbyDialogContent()
    {
        Table content = joinLobbyDialog.getContentTable();
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Name field
        joinLobbyIDField = new TextField("", skin);
        content.add(new Label("Lobby Id:", skin)).padRight(10);
        content.add(joinLobbyIDField).width(400).pad(10).row(); // Wider field

        // Password field
        joinLobbyPasswordField = new TextField("", skin);
        joinLobbyPasswordField.setPasswordMode(true);
        joinLobbyPasswordField.setPasswordCharacter('*');
        content.add(new Label("Password:", skin)).padRight(10);
        content.add(joinLobbyPasswordField).width(400).pad(10).row(); // Wider field

        // Action buttons
        TextButton joinButton = new TextButton("Join", skin);
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Handle lobby joining
                String id = joinLobbyIDField.getText().trim();
                String password = joinLobbyPasswordField.getText().trim();
                client.send(new LobbyJoinRequestMessage(id, password));
                joinLobbyDialog.hide();
            }
        });

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinLobbyDialog.hide();
            }
        });

        joinLobbyDialog.getButtonTable().add(joinButton).pad(10);
        joinLobbyDialog.getButtonTable().add(closeButton).pad(10);
    }

    private void createOnlineUsersDialogContent()
    {
        // wipe out old content *and* old buttons
        onlineUsersDialog.getContentTable().clearChildren();
        onlineUsersDialog.getButtonTable().clearChildren();

        Table content = onlineUsersDialog.getContentTable();
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Create scrollable table
        onlineUsersTable = new Table();
        onlineUsersTable.defaults().pad(5);

        if (onlineUsersText != null && !onlineUsersText.isEmpty())
        {
            String[] users = onlineUsersText.split("\n");
            for (String user : users)
            {
                onlineUsersTable.add(new Label(user, skin)).left().row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(onlineUsersTable, skin);
        scrollPane.setFadeScrollBars(false);
        content.add(scrollPane).width(800).height(600).row(); // Larger scroll area

        onlineUsersDialog.button("Close");
    }

    private void createActiveLobbiesDialogContent()
    {
        activeLobbiesDialog.getContentTable().clearChildren();
        activeLobbiesDialog.getButtonTable().clearChildren();

        Table content = activeLobbiesDialog.getContentTable();
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Create scrollable table
        activeLobbiesTable = new Table();
        activeLobbiesTable.defaults().pad(5).fillX().expandX();

        if (activeLobbiesText != null && !activeLobbiesText.isEmpty())
        {
            String[] lobbies = activeLobbiesText.split("\n");
            for (String lobby : lobbies)
            {
                activeLobbiesTable.add(new Label(lobby, skin)).left().row();
            }
        }


        ScrollPane scrollPane = new ScrollPane(activeLobbiesTable, skin);
        scrollPane.setFadeScrollBars(false);
        content.add(scrollPane).width(800).height(600).fill().row(); // Larger scroll area

        activeLobbiesDialog.button("Close");
    }

    private void arrangeLayout()
    {
        // Main button container
        Table mainTable = new Table();
        mainTable.defaults().width(500).height(100).pad(10);

        mainTable.add(gamePrefsButton).row();
        mainTable.add(createLobbyButton).row();
        mainTable.add(joinLobbyButton).row();

        // Side-by-side buttons
        Table sideBySideTable = new Table();
        sideBySideTable.defaults().width(245).height(100).pad(5);
        sideBySideTable.add(onlineUsersButton).padRight(5);
        sideBySideTable.add(activeLobbiesButton).padLeft(5);
        mainTable.add(sideBySideTable).row();

        mainTable.add(backButton).row();

        Container<Table> container = new Container<>(mainTable);
        container.setFillParent(true);
        container.center();

        // Status bar
        Table statusBar = new Table();
        statusBar.top();
        statusBar.setFillParent(true);
        statusBar.add(statusLabel).pad(10).left().expandX();
        statusBar.add(usernameLabel).pad(10).right().expandX();

        stage.addActor(container);
        stage.addActor(statusBar);
    }

    private void addListeners()
    {
        // Game Preferences button
        gamePrefsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gamePrefsDialog.show(stage);
            }
        });

        // Create Lobby button
        createLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Reset fields
                lobbyNameField.setText("");
                lobbyPasswordField.setText("");
                privateCheckbox.setChecked(true);
                visibleCheckbox.setChecked(true);
                createLobbyDialog.show(stage);
            }
        });

        // Join Lobby button
        joinLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Reset fields
                joinLobbyIDField.setText("");
                joinLobbyPasswordField.setText("");
                joinLobbyDialog.show(stage);
            }
        });

        // Online Users button
        onlineUsersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createOnlineUsersDialogContent();
                onlineUsersDialog.show(stage);
            }
        });

        // Active Lobbies button
        activeLobbiesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createActiveLobbiesDialogContent();
                activeLobbiesDialog.show(stage);
            }
        });

        // Back button
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getApp().setScreen(new PreGameScreen());
            }
        });

        // Close button for all dialogs
        setupDialogCloseListeners();
    }

    private void setupDialogCloseListeners()
    {
        Array<Dialog> dialogs = new Array<>(new Dialog[]{
            gamePrefsDialog, avatarSelectionDialog, mapSelectionDialog,
            createLobbyDialog, joinLobbyDialog, onlineUsersDialog, activeLobbiesDialog
        });

        for (Dialog dialog : dialogs)
        {
            TextButton closeButton = (TextButton) dialog.getButtonTable().getCells().get(0).getActor();
            if (closeButton != null)
            {
                closeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        dialog.hide();
                    }
                });
            }
        }
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
        positionElements();
    }

    private void positionElements()
    {
        // Position status bar elements
        statusLabel.setPosition(20, Gdx.graphics.getHeight() - statusLabel.getHeight() - 10);
        usernameLabel.setPosition(Gdx.graphics.getWidth() - usernameLabel.getWidth() - 20,
            Gdx.graphics.getHeight() - usernameLabel.getHeight() - 10);
    }

    @Override
    public void render(float delta)
    {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update network status
        updateNetworkStatus(delta);

        textBoxSystem.update(delta);

        // Render stage
        stage.act(delta);
        stage.draw();

        textBoxSystem.render(stage.getBatch());
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
        positionElements();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose()
    {
        if (stage != null)
        {
            stage.dispose();
        }

        textBoxSystem.dispose();
    }

    private float messageTimer = 0;
    private void updateNetworkStatus(float delta)
    {
        messageTimer += delta;
        client.processMessages();

        if (messageTimer >= 0.1f)
        {
            User currentUser = App.getCurrentUser();

            client.send(new PreLobbyPresenceMessage());
            client.send(new UserChoicesMessage(
                currentUser.getCharacterChoice(),
                currentUser.getMapChoice()
            ));

            setStatus("connected");
            messageTimer = 0;
        }

        if (!isConnected)
        {
            lastConnectionTime += delta;
            if (lastConnectionTime > 120) // 2 minutes
            {
                textBoxSystem.showTextBox("disconnected");
                setStatus("disconnected");
                lastConnectionTime = 0;
            }
        }
    }

    public void handlePreLobbyConfirmation(PreLobbyConfirmationMessage msg)
    {
        isConnected = true;
        lastConnectionTime = 0f;
    }

    public void handlePreLobbyError(PreLobbyErrorMessage msg)
    {
        isConnected = false;
    }

    private void handleLobbyCreation(String lobbyName, String lobbyPassword, boolean isPrivate, boolean isVisible)
    {
        LobbyCreationPermissionMessage msg = new LobbyCreationPermissionMessage(lobbyName, lobbyPassword, isPrivate, isVisible);
        client.send(msg);
    }

    // ======================
    // Getters and Setters
    // ======================

    public void setStatus(String status) {
        statusLabel.setText("Status: " + status);
    }

    public void setOnlineUsersText(String text) {
        this.onlineUsersText = text;
    }

    public String getOnlineUsersText() {
        return onlineUsersText;
    }

    public void setActiveLobbiesText(String text) {
        this.activeLobbiesText = text;
    }

    public String getActiveLobbiesText() {
        return activeLobbiesText;
    }

    public void setUsernameLabelText(String username) {
        usernameLabel.setText(username);
    }

    public static PreLobbyScreen getInstance()
    {
        return instance;
    }

    public void showTextBox(String text)
    {
        textBoxSystem.showTextBox(text);
    }

    public void joinLobby(String lobbyName, String lobbyId)
    {
        textBoxSystem.showTextBox("successfully joined: " + lobbyName);
        // TODO: implement later
    }

    public void refreshOnlineUsersList()
    {
        if (onlineUsersDialog.isVisible())
        {
            createOnlineUsersDialogContent();
        }
    }

    public void refreshActiveLobbiesList()
    {
        if (activeLobbiesDialog.isVisible())
        {
            createActiveLobbiesDialogContent();
        }
    }

    // ======================
    // Logic To Implement
    // ======================

    /*
     * Complete Logic Checklist:
     *
     * 1. Network Communication:
     *    + Send periodic requests to server for lobby/user lists
     *    + Implement message handlers for lobby/user list responses
     *    + Create message classes: LobbyListRequest, UserListRequest,
     *      LobbyListResponse, UserListResponse
     *
     * 2. Lobby Management:
     *    - Handle lobby creation (send CreateLobbyMessage to server)
     *    - Handle lobby joining (send JoinLobbyMessage to server)
     *    - Implement lobby creation response handling
     *    - Implement lobby join response handling (success/failure)
     *
     * 3. UI Updates:
     *    - Update onlineUsersTable with data from server
     *    - Update activeLobbiesTable with data from server
     *    - Implement click handlers for lobby entries (join directly)
     *    - Update status label based on connection state
     *
     * 4. Preference Management:
     *    + Save selected avatar, map, and gender to user profile
     *    + Send preferences to server when creating/joining lobby
     *
     * 5. Navigation:
     *    - Transition to LobbyScreen after creating/joining lobby
     *    - Implement back navigation properly
     *
     * 6. Error Handling:
     *    - Show error dialogs for failed lobby operations
     *    - Handle disconnections gracefully
     *
     * 7. Data Synchronization:
     *    - Periodically refresh lobby/user lists
     *    - Implement lobby/user list caching to minimize flickering
     *
     * 8. Security:
     *    - Handle password-protected lobbies
     *    - Validate lobby names/passwords
     *
     * 9. Admin Features:
     *    - Implement kick player functionality (for lobby admin)
     *    - Implement start game functionality
     *
     * 10. Performance:
     *     - Optimize texture loading
     *     - Implement object pooling for UI elements
     *     - Throttle network requests when appropriate
     */
}
