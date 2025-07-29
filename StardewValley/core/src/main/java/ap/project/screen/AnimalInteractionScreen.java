package ap.project.screen;

import ap.project.model.animal.Animal;
import ap.project.view.GameMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AnimalInteractionScreen {

    private final Stage stage;
    private final Skin skin;
    private final Window window;
    private Animal currentAnimal;

    public AnimalInteractionScreen(Viewport viewport, Skin skin) {
        this.stage = new Stage(viewport);
        this.skin = skin;

        // Create window with empty title (we'll set it when showing)
        window = new Window("", skin, "default");
        window.setVisible(false);
        window.setModal(true);
        window.setMovable(false);
        window.setResizable(false);
        window.pad(10);
        stage.addActor(window);

        // Click listener for closing when clicking outside
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (window.isVisible() && window.hit(x, y, false) == null) {
                    hide();
                }
            }
        });
    }



    public void show(Animal animal, float x, float y) {
        currentAnimal = animal;
        window.clearChildren();
        window.setName(animal.getName());
        window.getTitleLabel().setAlignment(Align.center);

        // Set background with some transparency
        window.setBackground(skin.getDrawable("background"));

        // Add interaction buttons
        addButton("🐾 Pet", () -> {
            animal.pet();
            GameMenu.println("You pet " + animal.getName() + ".");
            hide();
        });

        addButton("🥕 Feed", () -> {
            animal.feed();
            GameMenu.println("You fed " + animal.getName() + ".");
            hide();
        });

        addButton("🚪 Release", () -> {
            animal.goOut();
            GameMenu.println(animal.getName() + " is now outside.");
            hide();
        });

        addButton("🎁 Collect Product", () -> {
            if (animal.hasProduct()) {
                GameMenu.println("You collected product from " + animal.getName());
            } else {
                GameMenu.println(animal.getName() + " has nothing to give.");
            }
            hide();
        });

        addButton("💰 Sell", () -> {
            GameMenu.println(animal.getName() + " was sold for $" + animal.getPrice());
            hide();
        });

        // Position and show the window
        window.pack();

        // Ensure window stays on screen
        float finalX = Math.min(x, Gdx.graphics.getWidth() - window.getWidth() - 10);
        float finalY = Math.min(y, Gdx.graphics.getHeight() - window.getHeight() - 10);

        window.setPosition(finalX, finalY);
        window.setVisible(true);
        window.toFront();
    }

    private void addButton(String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.pad(8);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
                hide(); // Close window after any button click
            }
        });
        window.add(button).fillX().padBottom(5).row();
    }

    public void hide() {
        window.setVisible(false);
        currentAnimal = null;
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isVisible() {
        return window.isVisible();
    }

    public Animal getCurrentAnimal() {
        return currentAnimal;
    }

    public Window getWindow() {
        return window;
    }
}
