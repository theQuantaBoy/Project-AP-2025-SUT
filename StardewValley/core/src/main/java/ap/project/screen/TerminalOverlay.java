package ap.project.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;

public final class TerminalOverlay {
    private final Stage stage;            // or keep your SpriteBatch + font
    private boolean visible;

    public TerminalOverlay() {
        stage = new Stage(new ScreenViewport());
        // port the UI construction from TerminalScreen here
    }
    public void render(float dt){
        if(!visible) return;
        stage.act(dt);
        stage.draw();
    }
    public void toggle(){ visible = !visible; }
    public boolean isVisible(){ return visible; }
    public InputProcessor getProcessor(){ return stage; }
    public void resize(int w,int h){ stage.getViewport().update(w,h,true); }
}

