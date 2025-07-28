package ap.project.screen;

import ap.project.Main;
import ap.project.control.MainMenuController;
import ap.project.control.ProfileController;
import ap.project.control.RegisterController;
import ap.project.model.App.*;
import ap.project.model.enums.regex_enums.RegisterCommands;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ProfileMenu implements Screen {
    private ProfileController controller;
    private Stage stage;
    private Image background;
    private Image logo;
    private Image avatar;
    private Label menuName;
    private Label nickname;
    private Table rootTable;
    private TextButton changeUsernameButton;
    private TextButton changePasswordButton;
    private TextButton changeEmailButton;
    private TextButton changeNicknameButton;
    private TextField field1;
    private TextField field2;
    private TextButton random;
    private SelectBox<AvatarOptions> avatarOption;
    private TextButton change;
    private TextButton backButton;
    private Label error;
    private String changeWhat;
    private User user;

    public ProfileMenu(ProfileController controller) {
        stage =  new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.controller = controller;
        this.user = App.getCurrentUser();
        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.avatar = new Image(user.getAvatar());
        this.menuName = new Label("PROFILE\nMENU", GameAssetsManager.getGameAssetsManager().getSkin(), "Impact");
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);
        this.nickname = new Label(user.getNickname() + "\n" + user.getGender().name(), GameAssetsManager.getGameAssetsManager().getSkin());
        this.nickname.setAlignment(Align.center);
        this.nickname.setColor(Color.GOLD);
        this.changeUsernameButton = new TextButton("Change Username", GameAssetsManager.getGameAssetsManager().getSkin());
        this.changeUsernameButton.getStyle().font.getData().setScale(0.8f);
        this.changePasswordButton = new TextButton("Change Password", GameAssetsManager.getGameAssetsManager().getSkin());
        this.changeEmailButton = new TextButton("Change Email", GameAssetsManager.getGameAssetsManager().getSkin());
        this.changeNicknameButton = new TextButton("Change Nickname", GameAssetsManager.getGameAssetsManager().getSkin());
        this.field1 = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.field2 = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.random = new TextButton("Random", GameAssetsManager.getGameAssetsManager().getSkin());
        this.avatarOption = new SelectBox<>(GameAssetsManager.getGameAssetsManager().getSkin());
        this.avatarOption.setItems(GameAssetsManager.getGameAssetsManager().getAvatars());
        this.avatarOption.setWidth(300);
        this.avatarOption.getStyle().fontColor = Color.CYAN;
        this.change = new TextButton("Change", GameAssetsManager.getGameAssetsManager().getSkin());
        this.backButton = new TextButton("Back", GameAssetsManager.getGameAssetsManager().getSkin());
        this.error = new Label("", GameAssetsManager.getGameAssetsManager().getSkin());
        field1.setVisible(false);
        field2.setVisible(false);
        change.setVisible(false);
        random.setVisible(false);

        rootTable = new Table();

        Table topButtonTable = new Table();
        topButtonTable.add(changeUsernameButton).width(400).pad(15);
        topButtonTable.add(changePasswordButton).width(400).pad(15);
        topButtonTable.add(changeEmailButton).width(400).pad(15);
        topButtonTable.add(changeNicknameButton).width(400).pad(15);

        rootTable.top().padTop(50);
        rootTable.add(topButtonTable).center().row();
        rootTable.add(field1).width(600).pad(10).expandY().center().row();
        Table passTable = new Table();
        passTable.add(field2).width(400).pad(10).expandY().center();
        passTable.add(random).width(200).pad(10).expandY().center().row();
        rootTable.add(passTable).center().row();
        rootTable.add(change).width(600).pad(10).expandY().center().row();
        rootTable.add(backButton).width(600).pad(10).padBottom(30).bottom();

        addListeners();
    }

    private void addListeners() {
        avatarOption.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                field1.setVisible(false);
                field2.setVisible(false);
                random.setVisible(false);
                change.setVisible(false);
                avatar.setDrawable(new TextureRegionDrawable(avatarOption.getSelected().texture));
                user.setAvatar(avatarOption.getSelected().texture);
            }
        });
        changeUsernameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                field1.setVisible(true);
                field2.setVisible(false);
                random.setVisible(false);
                change.setVisible(true);
                field1.setMessageText("Change Username");
                changeWhat = "username";
            }
        });
        changePasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                field1.setVisible(true);
                field2.setVisible(true);
                random.setVisible(true);
                change.setVisible(true);
                field1.setMessageText("Change Password");
                field2.setMessageText("Confirm Password");
                changeWhat = "password";
            }
        });
        changeEmailButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                field1.setVisible(true);
                field2.setVisible(false);
                random.setVisible(false);
                change.setVisible(true);
                field1.setMessageText("Change Email");
                changeWhat = "email";
            }
        });
        changeNicknameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                field1.setVisible(true);
                field2.setVisible(false);
                random.setVisible(false);
                change.setVisible(true);
                field1.setMessageText("Change Nickname");
                changeWhat = "nickname";
            }
        });

        random.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String generatedPassword = RegisterController.generatePassword();
                field1.setText(generatedPassword);
                field2.setText(generatedPassword);
            }
        });

        change.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (changeWhat.equals("username")) {
                    String username = field1.getText();
                    if (RegisterCommands.CHECK_USERNAME.getMatcher(username) == null) {
                        error.setText("username is not valid!");
                        error.setColor(Color.RED);
                    } else if (App.getPlayerByUsername(username) != null) {
                        error.setText("username is already taken!");
                        error.setColor(Color.RED);
                    } else {
                        user.setUsername(username);
                        error.setText("username changed successfully!");
                        error.setColor(Color.GREEN);
                    }
                } else if (changeWhat.equals("password")) {
                    String newPass = field1.getText();
                    String newConfirm = field2.getText();
                    if (RegisterCommands.CHECK_PASSWORD.getMatcher(newPass) == null) {
                        error.setText("password is invalid!");
                        error.setColor(Color.RED);
                    } else if (newPass.length() < 8) {
                        error.setText("password is short!");
                        error.setColor(Color.RED);
                    } else if (!newPass.matches(".*[a-z].*")) {
                        error.setText("your password should include small letters!");
                        error.setColor(Color.RED);
                    } else if (!newPass.matches(".*[A-Z].*")) {
                        error.setText("your password should include capital letters!");
                        error.setColor(Color.RED);
                    } else if (!newPass.matches(".*[0-9].*")) {
                        error.setText("your password should include numbers!");
                        error.setColor(Color.RED);
                    } else if (!newPass.matches(".*[!@#$%^&*)(=+}{\\[\\]|\\\\/:;'\",><?].*")) {
                        error.setText("your password should include special character!");
                        error.setColor(Color.RED);
                    } else if (!newPass.equals(newConfirm)) {
                        error.setText("your password doesn't match confirm password!");
                        error.setColor(Color.RED);
                    } else {
                        user.setPassword(SHA256Hasher.hash(newPass));
                        error.setText("your password changed successfully!");
                        error.setColor(Color.GREEN);
                    }
                } else if (changeWhat.equals("email")) {
                    String email = field1.getText();
                    if (RegisterCommands.CHECK_EMAIL.getMatcher(email) == null) {
                        error.setText("email is not valid!");
                        error.setColor(Color.RED);
                    } else {
                        user.setEmail(email);
                        error.setText("email changed successfully!");
                        error.setColor(Color.GREEN);
                    }
                } else  if (changeWhat.equals("nickname")) {
                    String nickname = field1.getText();
                    if (nickname.equals("")) {
                        error.setText("nickname is not valid!");
                        error.setColor(Color.RED);
                    } else {
                        user.setNickname(nickname);
                        error.setText("nickname changed successfully!");
                        error.setColor(Color.GREEN);
                    }
                }
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new MainScreen(new MainMenuController()));
            }
        });
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(menuName);
        stage.addActor(nickname);
        stage.addActor(rootTable);
        stage.addActor(avatar);
        stage.addActor(avatarOption);
        positionElements();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1/30f));
        stage.getBatch().begin();
        this.nickname.setText("Nickname: " + user.getNickname() + "\n\n" +
            "Gender: " + user.getGender().name().toLowerCase() + "\n\n" +
            "Max money: " + user.getMaxMoney() + "\n\n" +
            "Number of games: " + user.getNumberOfGames());
        stage.getBatch().end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        positionElements();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void positionElements() {
        // Position logo at top center
        float logoCenterX = stage.getWidth() / 2 - logo.getWidth() / 2;
        float logoTopY = stage.getHeight() - logo.getHeight() - 20;
        logo.setPosition(logoCenterX, logoTopY);

        // Position label at top left
        float labelLeftX = 20;
        float labelRightX = stage.getWidth() - nickname.getPrefWidth() - 100;
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);
        avatar.setPosition( labelLeftX + 10, labelTopY - avatar.getHeight() - 40);
        avatarOption.setPosition(labelLeftX + 100, labelTopY - avatar.getHeight() - 43);
        nickname.setPosition(labelRightX - nickname.getPrefWidth(), labelTopY - nickname.getHeight());


        float tableWidth = rootTable.getPrefWidth();
        float tableHeight = rootTable.getPrefHeight();
        float x = (stage.getWidth())  / 2;
        float y = (stage.getHeight()) - 200;
        rootTable.setPosition(x, y);
    }
}
