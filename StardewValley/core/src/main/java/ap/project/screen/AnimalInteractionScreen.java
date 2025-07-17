package ap.project.screen;

import ap.project.model.animal.Animal;
import ap.project.view.GameMenu;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AnimalInteractionScreen {

    private final Stage stage;
    private final Skin skin;
    private final Table table;
    private Animal currentAnimal;

    public AnimalInteractionScreen(Viewport viewport, Skin skin) {
        this.stage = new Stage(viewport);
        this.skin = skin;

        table = new Table();
        table.setVisible(false);
        stage.addActor(table);
    }

    public void show(Animal animal, float x, float y) {
        currentAnimal = animal;
        table.clear();
        table.setVisible(true);
        table.setPosition(x, y, Align.bottomLeft);

        table.setBackground("default-round");

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
    }

    private void addButton(String label, Runnable action) {
        TextButton button = new TextButton(label, skin);
        button.pad(5);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        table.row().pad(2);
        table.add(button).expandX().fillX();
    }

    public void hide() {
        table.setVisible(false);
        currentAnimal = null;
    }

    public void update(float dt) {
        stage.act(dt);
    }

    public void render() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isVisible() {
        return table.isVisible();
    }

    public Animal getCurrentAnimal() {
        return currentAnimal;
    }
}
