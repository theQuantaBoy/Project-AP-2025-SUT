package ap.project.screen;

import ap.project.Main;
import ap.project.control.LoginController;
import ap.project.control.MainMenuController;
import ap.project.control.RegisterController;
import ap.project.model.App.*;
import ap.project.model.enums.regex_enums.RegisterCommands;
import ap.project.model.game.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ForgetScreen implements Screen {
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private Table table;
    private User user;
    private Label question;
    private TextField answer;
    private TextButton secCheck;
    private TextButton backButton;
    private TextField password;
    private TextField confirmPassword;
    private TextButton resetPassword;
    private TextButton random;
    private Label errorLabel;

    public ForgetScreen(User user) {
        stage =  new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.user = user;
        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("FORGET PASSWORD\nMENU", GameAssetsManager.getGameAssetsManager().getSkin());
        this.menuName.setAlignment(Align.center);
        this.menuName.setColor(Color.GOLD);
        this.question = new Label(user.getQuestion(), GameAssetsManager.getGameAssetsManager().getSkin());
        this.answer = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.answer.setMessageText("Your Answer");
        this.answer.setAlignment(Align.center);
        this.secCheck = new TextButton("enter", GameAssetsManager.getGameAssetsManager().getSkin());
        this.password = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.password.setMessageText("password");
        this.password.setAlignment(Align.center);
        this.confirmPassword = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.confirmPassword.setMessageText("confirm password");
        this.confirmPassword.setAlignment(Align.center);
        this.resetPassword = new TextButton("reset password", GameAssetsManager.getGameAssetsManager().getSkin());
        this.random = new TextButton("random", GameAssetsManager.getGameAssetsManager().getSkin());
        this.backButton = new TextButton("Back", GameAssetsManager.getGameAssetsManager().getSkin());
        this.errorLabel = new Label("", GameAssetsManager.getGameAssetsManager().getSkin());

        answer.setVisible(true); secCheck.setVisible(true);
        password.setVisible(false); confirmPassword.setVisible(false); resetPassword.setVisible(false); random.setVisible(false);

        this.table = new Table();
        Table secTable = new Table();
        table.add(question).width(500).height(50).pad(10).row();
        secTable.add(answer).width(320).height(50).pad(10);
        secTable.add(secCheck).width(180).height(50).pad(10).row();
        this.table.add(secTable).row();
        Table passTable  = new Table();
        passTable.add(password).width(320).height(50).pad(10);
        passTable.add(random).width(180).height(50).pad(10).row();
        passTable.add(confirmPassword).width(320).height(50).pad(10);
        passTable.add(resetPassword).width(180).height(50).pad(10);
        this.table.add(passTable).row();
        table.add(backButton).width(500).height(50).pad(10).row();
        table.add(errorLabel).width(500).height(50).pad(10).row();

        addButtonListeners();
    }

    private void addButtonListeners() {
        // Enter button listener
        secCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (user.getAnswer().equals(answer.getText())) {
                    errorLabel.setText("reset your password");
                    answer.setVisible(false); secCheck.setVisible(false);
                    password.setVisible(true); confirmPassword.setVisible(true); resetPassword.setVisible(true); random.setVisible(true);
                }
            }
        });

        random.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String generatedPassword = RegisterScreen.generatePassword();
                password.setText(generatedPassword);
                confirmPassword.setText(generatedPassword);
            }
        });

        resetPassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String newPass = password.getText();
                String newConfirm = confirmPassword.getText();
                if (RegisterCommands.CHECK_PASSWORD.getMatcher(newPass) == null) {
                   errorLabel.setText("password is invalid!");
                   errorLabel.setColor(Color.RED);
                } else if (newPass.length() < 8) {
                    errorLabel.setText("password is short!");
                    errorLabel.setColor(Color.RED);
                } else if (!newPass.matches(".*[a-z].*")) {
                    errorLabel.setText("your password should include small letters!");
                    errorLabel.setColor(Color.RED);
                } else if (!newPass.matches(".*[A-Z].*")) {
                    errorLabel.setText("your password should include capital letters!");
                    errorLabel.setColor(Color.RED);
                } else if (!newPass.matches(".*[0-9].*")) {
                    errorLabel.setText("your password should include numbers!");
                    errorLabel.setColor(Color.RED);
                } else if (!newPass.matches(".*[!@#$%^&*)(=+}{\\[\\]|\\\\/:;'\",><?].*")) {
                    errorLabel.setText("your password should include special character!");
                    errorLabel.setColor(Color.RED);
                } else if (!newPass.equals(newConfirm)) {
                    errorLabel.setText("your password doesn't match confirm password!");
                    errorLabel.setColor(Color.RED);
                } else {
                    user.setPassword(SHA256Hasher.hash(newPass));
                    errorLabel.setText("your password changed successfully!");
                    errorLabel.setColor(Color.GREEN);
                }
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new LoginScreen(new LoginController()));
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
        stage.addActor(table);
        positionElements();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1/30f));
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
        float labelTopY = stage.getHeight() - menuName.getHeight() - 20;
        menuName.setPosition(labelLeftX, labelTopY);


        float tableWidth = table.getPrefWidth();
        float tableHeight = table.getPrefHeight();
        float x = (stage.getWidth())  / 2;
        float y = (stage.getHeight()) / 2;
        table.setPosition(x, y);

    }

}
