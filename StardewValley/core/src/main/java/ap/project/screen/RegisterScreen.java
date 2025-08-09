package ap.project.screen;

import ap.project.Main;
import ap.project.control.LoginController;
import ap.project.control.RegisterController;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
import ap.project.model.enums.Gender;
import ap.project.model.enums.SecurityQuestionType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.security.SecureRandom;

import static ap.project.control.RegisterController.generatePassword;

public class RegisterScreen implements Screen {
    private Stage stage;
    private Image background;
    private Image logo;
    private Label menuName;
    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextField email;
    private TextField nickname;
    private SelectBox<String> gender;
    private SelectBox<String> secQuestion;
    private TextField secAnswer;
    private TextButton enter;
    private TextButton randomPass;
    private TextButton login;
    private TextButton exit;
    private Label errorLabel;
    private Table table;
    private RegisterController controller;

    public RegisterScreen(RegisterController controller) {
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(this.stage);
        this.controller = controller;

        // Load assets
        this.background = new Image(GameAssetsManager.getGameAssetsManager().getRegisterBackground());
        this.background.setFillParent(true);
        this.logo = new Image(GameAssetsManager.getGameAssetsManager().getLogo());
        this.menuName = new Label("REGISTRATION\nMENU", GameAssetsManager.getGameAssetsManager().getSkin(), "Impact");
        this.menuName.setAlignment(Align.center);

        this.table = new Table();


        this.username = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.username.setMessageText("Username");
        this.username.setAlignment(Align.center);

        this.password = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.password.setMessageText("Password");
        this.password.setAlignment(Align.center);
//        this.password.setPasswordMode(true);
//        this.password.setPasswordCharacter('*');

        this.confirmPassword = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.confirmPassword.setMessageText("Confirm");
        this.confirmPassword.setAlignment(Align.center);
//        this.confirmPassword.setPasswordMode(true);
//        this.confirmPassword.setPasswordCharacter('*');

        this.email = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.email.setMessageText("Email");
        this.email.setAlignment(Align.center);

        this.nickname = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.nickname.setMessageText("Nickname");
        this.nickname.setAlignment(Align.center);

        // Initialize gender select box

        Array<String> genderOptions = new Array<>(); genderOptions.add("Male"); genderOptions.add("Female");
        this.gender = new SelectBox<>(GameAssetsManager.getGameAssetsManager().getSkin());
        this.gender.setItems(genderOptions);
//        this.gender.getStyle().fontColor.set(Color.WHITE);
//        this.username.getStyle().fontColor = Color.WHITE;
        Array<String> secQuestionOptions = new Array<>();
        secQuestionOptions.add(SecurityQuestionType.ANIMAL.getQuestion());
        secQuestionOptions.add(SecurityQuestionType.GAME.getQuestion());
        secQuestionOptions.add(SecurityQuestionType.TA.getQuestion());
        this.secQuestion = new SelectBox<>(GameAssetsManager.getGameAssetsManager().getSkin());
        this.secQuestion.setItems(secQuestionOptions);
        this.secAnswer = new TextField("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.secAnswer.setMessageText("Answer");
        this.secAnswer.setAlignment(Align.center);
        this.enter = new TextButton("Create Account", GameAssetsManager.getGameAssetsManager().getSkin());
        this.login = new TextButton("Login", GameAssetsManager.getGameAssetsManager().getSkin());
        this.randomPass = new TextButton("random", GameAssetsManager.getGameAssetsManager().getSkin());
        this.errorLabel = new Label("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.errorLabel.setAlignment(Align.center);
        this.errorLabel.setColor(Color.RED);
//        this.errorLabel.setFontScale(2);
        this.exit = new TextButton("Exit", GameAssetsManager.getGameAssetsManager().getSkin());


        // Build form layout
        table.add(username).width(500).height(45).pad(10).row();

        Table passwordRow = new Table();
        Table nicknameRow = new Table();
        table.add(password).width(500).height(45).pad(10).row();
        passwordRow.add(confirmPassword).width(300).height(45).pad(10);
        passwordRow.add(randomPass).width(180).height(45).pad(10).row();
        table.add(passwordRow).row();
        table.add(email).width(500).height(45).pad(10).row();
        nicknameRow.add(nickname).width(300).height(45).pad(10);
        nicknameRow.add(gender).width(180).height(45).pad(10).row();
        table.add(nicknameRow).row();
        table.add(secQuestion).width(500).height(45).pad(10).row();
        table.add(secAnswer).width(500).height(45).pad(10).row();
        table.add(enter).width(500).height(55).pad(20).row();
        table.add(login).width(500).height(55).pad(20).row();
        table.add(exit).width(500).height(55).pad(20).row();
        table.add(errorLabel).width(300).height(45).pad(5).row();

        // Add button listeners
        addButtonListeners();
    }

    private void addButtonListeners() {
        // Enter button listener
        enter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String user = username.getText();
                String pass = password.getText();
                String confirmPass = confirmPassword.getText();
                String emailText = email.getText();
                String nick = nickname.getText();
                String selectedGender = gender.getSelected();
                Gender genderEnum = Gender.valueOf(selectedGender.toUpperCase());
                String questionSelected = secQuestion.getSelected();
                String answerSelected = secAnswer.getText();



                // Call controller to handle registration
                Result result = controller.register(user, pass, confirmPass, emailText, nick, genderEnum, questionSelected, answerSelected);
                errorLabel.setText(result.message());
                if (result.isSuccessful()) {
                    Main.getApp().setScreen(new LoginScreen(new LoginController()));
                }
            }
        });

        // Random password button listener
        randomPass.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String generatedPassword = generatePassword();
                password.setText(generatedPassword);
                confirmPassword.setText(generatedPassword);
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        login.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Main.getApp().setScreen(new LoginScreen(new LoginController()));
            }
        });
    }

    @Override
    public void show() {
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
        float y = (stage.getHeight()) - tableHeight/2 - 140;
        table.setPosition(x, y);

    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
