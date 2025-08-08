package ap.project.screen;

import ap.project.Main;
import ap.project.control.MainMenuController;
import ap.project.control.PreGameController;
import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.User;
import ap.project.model.enums.CharacterType;
import ap.project.model.enums.MapTypes;
import ap.project.model.game.DummyGame;
import ap.project.model.game.Game;
import ap.project.model.game.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

    private MapTypes selectedMap;
    private CharacterType selectedCharacter;

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
    }

    private void addButtonListeners() {
        addPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                User user = controller.getUser(username.getText());
                if (user != null) {
                    Player player = new Player(user, MapTypes.STANDARD, 0);
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

    public void showMapSelectionDialog(MapTypes current)
    {
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        final int[] mapIndex = {0};
        ArrayList<MapTypes> farms = MapTypes.getFarms();

        if (current != null)
            mapIndex[0] = farms.indexOf(current);
        if (mapIndex[0] == -1) mapIndex[0] = 0;

        Label mapNameLabel = new Label(farms.get(mapIndex[0]).getName(), skin);
        mapNameLabel.setFontScale(1.1f);

        TextButton left = new TextButton("<", skin);
        TextButton right = new TextButton(">", skin);

        left.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                mapIndex[0] = (mapIndex[0] - 1 + farms.size()) % farms.size();
                mapNameLabel.setText(farms.get(mapIndex[0]).getName());
            }
        });

        right.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                mapIndex[0] = (mapIndex[0] + 1) % farms.size();
                mapNameLabel.setText(farms.get(mapIndex[0]).getName());
            }
        });

        Dialog dialog = new Dialog("Select Map", skin)
        {
            @Override
            protected void result(Object object)
            {
                if ((Boolean) object)
                {
                    selectedMap = farms.get(mapIndex[0]);
                }
            }
        };

        dialog.getTitleLabel().setFontScale(1.2f);

        Table content = new Table();
        content.defaults().pad(20);
        content.add(left).padRight(30);
        content.add(right).padLeft(30).row();
        content.add(mapNameLabel).colspan(2).center().padTop(10).row();
        dialog.getContentTable().add(content);

        TextButton confirmBtn = new TextButton("Confirm", skin);
        TextButton cancelBtn = new TextButton("Cancel", skin);

        dialog.button(confirmBtn, true);
        dialog.button(cancelBtn, false);

        dialog.show(stage);
    }

    private void addPlayerRow(Player player)
    {
        Label nameLabel = new Label(player.getUser().getUsername(), GameAssetsManager.getGameAssetsManager().getSkin());
        nameLabel.setColor(Color.WHITE);

        String mapName = player.getMapType() != null
            ? player.getMapType().getName()
            : "No Map";

        Label mapLabel = new Label(mapName, GameAssetsManager.getGameAssetsManager().getSkin());
        mapLabel.setColor(Color.SKY);

        TextButton selectMapBtn = new TextButton("Select Map", GameAssetsManager.getGameAssetsManager().getSkin());

        selectMapBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMapSelectionDialog(player.getMapType());
                // wait a tiny bit for selection to register, then update UI
                Gdx.app.postRunnable(() -> {
                    mapLabel.setText(selectedMap != null ? selectedMap.getName() : "No Map");
                    player.setMapType(selectedMap);
                });
            }
        });

        Table row = new Table();
        row.add(nameLabel).width(200);
        row.add(mapLabel).width(200);
        row.add(selectMapBtn).width(150);

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
}
