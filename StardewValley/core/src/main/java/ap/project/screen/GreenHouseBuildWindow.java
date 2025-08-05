package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.game.GreenHouse;
import ap.project.model.game.Player;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ap.project.model.App.GameAssetsManager;
import com.badlogic.gdx.utils.Align;

public class GreenHouseBuildWindow
{
    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private boolean isVisible = false;

    public GreenHouseBuildWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Create main window
        window = new Window("Greenhouse Builder", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(20);

        // Create main content label
        Label infoLabel = new Label("You should first build the greenhouse to be able to use it.\n\n" +
            "cost:\n" +
            "Gold: 1000 units\n" +
            "Wood: 500 units", skin);
        infoLabel.setAlignment(Align.center);
        infoLabel.setWrap(true);

        // Create buttons
        TextButton buildButton = new TextButton("Build", skin);
        TextButton closeButton = new TextButton("Close", skin);

        // Button listeners
        buildButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showBuildPopUp();
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toggleVisibility();
            }
        });

        // Button container
        Table buttonContainer = new Table();
        buttonContainer.defaults().pad(10).width(150);
        buttonContainer.add(buildButton);
        buttonContainer.add(closeButton);

        // Main container layout
        Table container = new Table();
        container.add(infoLabel).width(400).padBottom(30).row();
        container.add(buttonContainer).padTop(20);

        window.add(container).pad(30);
        window.pack();
        center();
        stage.addActor(window);
    }

    private void center() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible) {
            window.toFront();  // Bring to top if overlapping
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose()
    {
        window.remove();
    }

    private void showBuildPopUp()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();
        GreenHouse greenHouse = player.getGreenHouse();

        if (player.canAffordGreenhouse())
        {
            greenHouse.build();
            UIRenderer.showTextBox("Yippee! You successfully built the greenhouse.");
            toggleVisibility();
        } else
        {
            UIRenderer.showTextBox("You can't afford the greenhouse.\nYou are poor :(");
        }
    }

    public Window getWindow() {
        return window;
    }
}
