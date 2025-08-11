package ap.project;

import ap.project.control.MainMenuController;
import ap.project.control.RegisterController;
import ap.project.model.App.App;
import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.game.RadioPlayer;
import ap.project.screen.*;
import ap.project.util.GameObjectAssetLoader;
import ap.project.view.AppView;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.net.Socket;
import java.util.Scanner;

public class Main extends com.badlogic.gdx.Game
{
    private static Main app;
    private SpriteBatch batch;
    private RadioPlayer radio;

    @Override
    public void create()
    {
        app = this;
        batch = new SpriteBatch();

        GameObjectAssetLoader.queueAllTextures();
        GameObjectAssetLoader.finishLoadingAndAssign();
        App.initialize();

        // set custom cursor
        Pixmap cursorPixmap = new Pixmap(Gdx.files.internal("general/cursor/cursor.png"));
        int hotspotX = 0; // adjust to your cursor's "click point"
        int hotspotY = 0;
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursorPixmap, hotspotX, hotspotY));
        cursorPixmap.dispose();
         // Path inside assets
        radio = getRadio();
//        if (!radio.getPlaylist().isEmpty()) {
//            radio.playTrack(0);
//        }

//        System.out.println("enter name: ");
//        Scanner scanner = new Scanner(System.in);
//        String name = scanner.nextLine().trim();
//        String name = "mohsen";
//
//        User user = new User(name, "1234", name, "mail", Gender.MALE, "", "");
//        App.setCurrentUser(user);
//        app.setScreen(new PreGameScreen());

        app.setScreen(new RegisterScreen(new RegisterController()));
    }

    @Override
    public void render()
    {
        super.render();
//        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
//            radio.playNext();
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
//            radio.playPrevious();
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
//            radio.toggleShuffle();
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
//            radio.toggleRepeatOne();
//        }
    }

    @Override
    public void dispose()
    {
       super.dispose();
       GameObjectAssetLoader.dispose();
       radio.dispose();
    }

    public static Main getApp()
    {
        return app;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public RadioPlayer getRadio() {
        if (radio == null) {
            return radio = new RadioPlayer("C:/Users/arash/OneDrive/Desktop/uni/AP/kakasiahMazrae/advanced-programming-phase-1-group-26/StardewValley/core/assets/music/");
        }
        return radio;
    }
}
