package ap.project.screen;

import ap.project.Main;
import ap.project.control.MainMenuController;
import ap.project.control.PreGameController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.CharacterType;
import ap.project.model.enums.MapTypes;
import ap.project.model.enums.Season;
import ap.project.model.game.DummyGame;
import ap.project.model.game.Game;
import ap.project.model.game.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

public class OfflinePreGameScreen implements Screen
{
    private PreGameController controller;
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private Label nickname;
    private Table table;
    TextField username;
    private TextButton addPlayerButton;
    private TextButton newGameButton;
    private TextButton loadGame;
    private TextButton backButton;
    private TextButton exitButton;

    private ArrayList<Player> players;
    private Table playerListTable;

    private Dialog gamePrefsDialog;
    private Dialog avatarSelectionDialog;
    private Dialog mapSelectionDialog;

    private Label avatarNameLabel;
    private Label mapNameLabel;

    private TexturePreviewActor avatarPreviewActor;
    private TexturePreviewActor mapPreviewActor;

    private int currentAvatarIndex = 0;
    private int currentMapIndex = 0;
    private User prefsTargetUser;
    private Label activeRowMapLabel;
    private Label activeRowAvatarLabel;

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

    public OfflinePreGameScreen(PreGameController controller)
    {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.controller = controller;

        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("NEW\nGAME", GameAssetsManager.getGameAssetsManager().getSkin(), "Impact");
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);
        this.nickname = new Label(App.getCurrentUser().getNickname(), GameAssetsManager.getGameAssetsManager().getSkin());
        this.nickname.setAlignment(Align.center);
        this.nickname.setColor(Color.GOLD);
        this.table = new Table();

        this.username = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.username.setMessageText("Username");
        this.username.setAlignment(Align.center);

        this.addPlayerButton = new TextButton("Add Player", GameAssetsManager.getGameAssetsManager().getSkin());
        this.newGameButton = new TextButton("Create Game", GameAssetsManager.getGameAssetsManager().getSkin());
        this.loadGame = new TextButton("Load Game", GameAssetsManager.getGameAssetsManager().getSkin());
        this.backButton = new TextButton("Back", GameAssetsManager.getGameAssetsManager().getSkin());
        this.exitButton = new TextButton("Exit", GameAssetsManager.getGameAssetsManager().getSkin());

        this.playerListTable = new Table();
        this.playerListTable.defaults().pad(5);

        players = new ArrayList<>();
        Player player = new Player(App.getCurrentUser());
        players.add(player);

        addPlayerRow(player);
        table.add(playerListTable).pad(10).row();

        table.add(username).width(500).height(50).pad(10).row();
        table.add(addPlayerButton).width(500).height(50).pad(10).row();
        table.add(newGameButton).width(500).height(50).pad(10).row();
        table.add(loadGame).width(500).height(50).pad(10).row();
        table.add(backButton).width(500).height(50).pad(10).row();
        table.add(exitButton).width(500).height(50).pad(10).row();

        addButtonListeners();
        createPrefsDialogs();
    }

    private void addButtonListeners()
    {
        addPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                User user = controller.getUser(username.getText());
                if (user != null)
                {
                    Player player = new Player(user);
                    addPlayerRow(player);
                    players.add(player);
                }
            }
        });

        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Game game = new Game(players);
                game.setCurrentPlayer(players.get(0));
                App.addGame(game);
                App.setCurrentGame(game);
                Gdx.graphics.setWindowedMode(1800, 960);
                App.getCurrentUser().setCurrentGame(game);
                Main.getApp().setScreen(new WorldScreen(game.getCurrentPlayer(), false, true));
            }
        });

        loadGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSavedGamesDialog();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new MainScreen(new MainMenuController()));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show()
    {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(menuName);
        stage.addActor(nickname);
        stage.addActor(table);
        positionElements();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
        positionElements();
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {
        stage.dispose();
    }

    private void positionElements()
    {
        // Position logo at top center
        float logoCenterX = stage.getWidth() / 2 - logo.getWidth() / 2;
        float logoTopY = stage.getHeight() - logo.getHeight() - 20;
        logo.setPosition(logoCenterX, logoTopY);

        // Position label at top left
        float labelLeftX = 20;
        float labelRightX = stage.getWidth() - nickname.getPrefWidth() - 100;
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);
        nickname.setPosition(labelRightX, labelTopY);


        float tableWidth = table.getPrefWidth();
        float tableHeight = table.getPrefHeight();
        float x = (stage.getWidth()) / 2;
        float y = (stage.getHeight()) / 2;
        table.setPosition(x, y);
    }

    private void addPlayerRow(Player player) {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        Label nameLabel = new Label(player.getUser().getUsername(), skin);
        nameLabel.setColor(Color.WHITE);

        String mapName = player.getMapType() != null ? player.getMapType().getName() : "No Map";
        Label mapLabel = new Label(mapName, skin);
        mapLabel.setColor(Color.SKY);

        int avatarIdx = player.getUser().getCharacterChoice();
        String avatarName = CharacterType.values()[Math.max(0, Math.min(avatarIdx, CharacterType.values().length - 1))].name();
        Label avatarLabel = new Label(avatarName, skin);
        avatarLabel.setColor(Color.GOLD);

        TextButton prefsBtn = new TextButton("Prefs…", skin);
        prefsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor)
            {
                // Set the context for dialogs
                prefsTargetUser = player.getUser();
                activeRowMapLabel = mapLabel;
                activeRowAvatarLabel = avatarLabel;

                // Initialize indices from current player
                currentAvatarIndex = player.getUser().getCharacterChoice();
                ArrayList<MapTypes> farms = MapTypes.getFarms();
                currentMapIndex = player.getUser().getMapChoice();

                updateAvatarPreview();
                updateMapPreview();

                gamePrefsDialog.show(stage);
            }
        });

        Table row = new Table();
        row.defaults().pad(6);
        row.add(nameLabel).width(200).left();
        row.add(new Label("Map:", skin)).right().padRight(8);
        row.add(mapLabel).width(200).left().padRight(20);
        row.add(new Label("Avatar:", skin)).right().padRight(8);
        row.add(avatarLabel).width(160).left().padRight(20);
        row.add(prefsBtn).width(140).right();

        playerListTable.add(row).row();
    }

    private void showSavedGamesDialog() {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        Dialog dialog = new Dialog("Saved Games", skin);

        // Responsive size (~85% of current stage)
        float w = Math.min(1100f, stage.getWidth() * 0.85f);
        float h = Math.min(800f,  stage.getHeight() * 0.75f);

        Table table = createSavedGamesTable();
        ScrollPane scrollPane = new ScrollPane(table, skin);

        // Make list fill width and scroll only vertically
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setOverscroll(false, false);

        dialog.getContentTable().clear();
        dialog.getContentTable().add(scrollPane).width(w).height(h).pad(20);
        dialog.button("Close", false);
        dialog.show(stage);
    }

    private Table createSavedGamesTable() {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        Table t = new Table();
        t.top().left();
        t.defaults()
            .pad(10)              // more vertical/horizontal padding between cells
            .left()
            .expandX()
            .fillX();

        int currentPlayerId = App.getLoggedInUser().getHashId();
        ArrayList<DummyGame> playerGames = App.getGamesForPlayer(currentPlayerId);

        if (playerGames == null || playerGames.isEmpty()) {
            Label empty = new Label("No saved games found.", skin);
            empty.setAlignment(Align.center);
            t.add(empty).colspan(5).expandX().pad(20);
            t.row();
            return t;
        }

        // Header with extra bottom padding
        t.add(new Label("Game ID", skin)).left().minWidth(200).padBottom(15);
        t.add(new Label("Players", skin)).left().width(100).padBottom(15);
        t.add(new Label("Duration", skin)).left().minWidth(180).padBottom(15);
        t.add().padBottom(15);
        t.row();

        for (DummyGame game : playerGames) {
            Label id       = new Label(game.getGameId(), skin);
            Label players  = new Label(String.valueOf(game.getPlayerIds().size()), skin);
            Label duration = new Label(game.getDurationString(), skin);

            TextButton loadButton = new TextButton("Load", skin);
            loadButton.pad(5, 15, 5, 15); // extra padding inside button
            loadButton.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    Game loadedGame = App.loadGame(game.getGameId());
                    if (loadedGame == null) return;

                    Player currentPlayer = null;
                    for (Player p : loadedGame.getPlayers()) {
                        if (p.getUser().getHashId() == currentPlayerId) {
                            currentPlayer = p;
                            break;
                        }
                    }
                    if (currentPlayer == null) return;

                    loadedGame.setCurrentPlayer(currentPlayer);
                    App.setCurrentGame(loadedGame);
                    Gdx.graphics.setWindowedMode(1800, 960);
                    App.getCurrentUser().setCurrentGame(loadedGame);
                    Main.getApp().setScreen(new WorldScreen(currentPlayer, false, false));
                }
            });

            // Row with wider spacing
            t.add(id)      .left().minWidth(200).padRight(20);
            t.add(players) .left().width(100).padRight(20);
            t.add(duration).left().minWidth(180).padRight(20);
            t.add(loadButton).right().width(130);
            t.row();
        }

        return t;
    }

    private void createPrefsDialogs() {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        gamePrefsDialog = new Dialog("Game Preferences", skin);
        gamePrefsDialog.getContentTable().pad(50);
        createGamePrefsDialogContent();

        avatarSelectionDialog = new Dialog("Select Avatar", skin);
        avatarSelectionDialog.getContentTable().pad(50);
        createAvatarDialogContent();

        mapSelectionDialog = new Dialog("Select Map", skin);
        mapSelectionDialog.getContentTable().pad(50);
        createMapDialogContent();
    }

    private void createGamePrefsDialogContent() {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();
        Table content = gamePrefsDialog.getContentTable();

        TextButton avatarButton = new TextButton("Avatar", skin);
        avatarButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                avatarSelectionDialog.show(stage);
            }
        });
        content.add(avatarButton).pad(15).minWidth(200).minHeight(60).row();

        TextButton mapButton = new TextButton("Map", skin);
        mapButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                mapSelectionDialog.show(stage);
            }
        });
        content.add(mapButton).pad(15).minWidth(200).minHeight(60).row();

        gamePrefsDialog.button("Close");
    }

    private void createAvatarDialogContent() {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();
        Table content = avatarSelectionDialog.getContentTable();

        avatarNameLabel = new Label("", skin);
        content.add(avatarNameLabel).padBottom(10).row();

        Table nav = new Table();
        TextButton prev = new TextButton("<", skin);
        TextButton next = new TextButton(">", skin);

        avatarPreviewActor = new TexturePreviewActor();

        prev.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                currentAvatarIndex = (currentAvatarIndex - 1 + CharacterType.values().length) % CharacterType.values().length;
                applyAvatarSelection();
                App.updateUser(prefsTargetUser);
            }
        });
        next.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                currentAvatarIndex = (currentAvatarIndex + 1) % CharacterType.values().length;
                applyAvatarSelection();
                App.updateUser(prefsTargetUser);
            }
        });

        nav.add(prev).width(80).padRight(20);
        nav.add(avatarPreviewActor).width(300).height(300).pad(20);
        nav.add(next).width(80).padLeft(20);

        content.add(nav).row();
        avatarSelectionDialog.button("Close");

        updateAvatarPreview();
    }

    private void createMapDialogContent() {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();
        Table content = mapSelectionDialog.getContentTable();

        mapNameLabel = new Label("", skin);
        content.add(mapNameLabel).padBottom(10).row();

        Table nav = new Table();
        TextButton prev = new TextButton("<", skin);
        TextButton next = new TextButton(">", skin);

        mapPreviewActor = new TexturePreviewActor();

        prev.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                ArrayList<MapTypes> farms = MapTypes.getFarms();
                currentMapIndex = (currentMapIndex - 1 + farms.size()) % farms.size();
                applyMapSelection();
                App.updateUser(prefsTargetUser);
            }
        });
        next.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                ArrayList<MapTypes> farms = MapTypes.getFarms();
                currentMapIndex = (currentMapIndex + 1) % farms.size();
                applyMapSelection();
                App.updateUser(prefsTargetUser);
            }
        });

        nav.add(prev).width(80).padRight(20);
        nav.add(mapPreviewActor).width(400).height(300).pad(20);
        nav.add(next).width(80).padLeft(20);

        content.add(nav).row();
        mapSelectionDialog.button("Close");

        updateMapPreview();
    }

    private void updateAvatarPreview() {
        CharacterType selected = CharacterType.values()[currentAvatarIndex];
        avatarNameLabel.setText(selected.name());
        Texture texture = selected.getAvatarTexture();
        avatarPreviewActor.setTexture(texture);
    }

    private void updateMapPreview() {
        ArrayList<MapTypes> farms = MapTypes.getFarms();
        if (farms.isEmpty()) return;
        MapTypes selected = farms.get(currentMapIndex);
        mapNameLabel.setText(selected.name());
        Texture texture = MapTypes.getMiniMapTexture(selected, Season.Spring);
        mapPreviewActor.setTexture(texture);
    }

    private void applyAvatarSelection()
    {
        updateAvatarPreview();
        if (prefsTargetUser != null)
        {
            prefsTargetUser.setCharacterChoice(currentAvatarIndex);

            if (activeRowAvatarLabel != null)
            {
                activeRowAvatarLabel.setText(CharacterType.values()[currentAvatarIndex].name());
            }
        }
    }

    private void applyMapSelection()
    {
        updateMapPreview();
        if (prefsTargetUser != null)
        {
            ArrayList<MapTypes> farms = MapTypes.getFarms();
            MapTypes sel = farms.get(currentMapIndex);
            prefsTargetUser.setMapChoice(currentMapIndex);

            for (Player player : players)
            {
                if (player.getUser().getHashId() == prefsTargetUser.getHashId())
                {
                    player.setMapType(sel);
                    break;
                }
            }

            if (activeRowMapLabel != null)
            {
                activeRowMapLabel.setText(sel.getName());
            }
        }
    }
}
