package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.animal.Animal;
import ap.project.model.game.Farm;
import ap.project.model.game.Player;
import ap.project.visual.AnimalActor;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import java.util.List;


public class AnimalWindow {

    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private final Table animalTable;
    private boolean isVisible = false;
    private AnimalManager animalManager;
    private static final int SLOTS_SIZE = 80;
    private Drawable slotBackground;
    private Drawable slotHighlight;
    private Animal selectedAnimal;

    public AnimalWindow(Stage stage) {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Create window
        window = new Window("Animals", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.setResizable(true);
        window.setSize(600, 400);

        // Create slot backgrounds
        this.slotBackground = GameAssetsManager.getGameAssetsManager().createColoredDrawable(
            SLOTS_SIZE, SLOTS_SIZE, new Color(0.3f, 0.3f, 0.3f, 0.7f));
        this.slotHighlight = GameAssetsManager.getGameAssetsManager().createColoredDrawable(
            SLOTS_SIZE, SLOTS_SIZE, new Color(0.5f, 0.5f, 0.5f, 0.9f));

        // Create animal table
        animalTable = new Table();
        animalTable.defaults().size(SLOTS_SIZE).pad(5);

        // Add scrollable area
        ScrollPane scrollPane = new ScrollPane(animalTable, skin);
        scrollPane.setFadeScrollBars(false);
        window.add(scrollPane).expand().fill();

        stage.addActor(window);
        center();
    }

    public void refresh() {
        animalTable.clear();

        Array<AnimalActor> animalActors = animalManager.getAnimalActors();
        int col = 0;

        for (AnimalActor actor : animalActors) {
            Animal animal = actor.getAnimal();

            // Create container for each animal
            Table animalEntry = new Table();
            animalEntry.setBackground(skin.getDrawable("default-round"));
            animalEntry.pad(10);

            // Add animal icon
            TextureRegion icon = animal.getAnimalType().getIcon();
            Image iconImage = new Image(icon);
            animalEntry.add(iconImage).size(64, 64).padBottom(5).row();

            // Add animal name
            Label nameLabel = new Label(animal.getName(), skin);
            animalEntry.add(nameLabel);

            if (animal == selectedAnimal) {
                animalEntry.setBackground(skin.getDrawable("default-round-selected"));
            }

            // Add click listener
            animalEntry.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectAnimal(animal);
                }
            });

            animalTable.add(animalEntry).pad(5);

            Table buttonTable = new Table();
            buttonTable.defaults().pad(2).minWidth(120);

            addInteractionButton(buttonTable, "Pet", animal);
            addInteractionButton(buttonTable, "Feed", animal);
            addInteractionButton(buttonTable, "Release", animal);
            addInteractionButton(buttonTable, "Collect", animal);
            addInteractionButton(buttonTable, "Sell", animal);

            animalEntry.add(buttonTable).padTop(5).row();

            if (++col % 4 == 0) { // 4 animals per row
                animalTable.row();
            }
        }
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible) {
            refresh();
            window.toFront();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    private void center() {
        float x = (stage.getWidth() - window.getWidth()) / 2;
        float y = (stage.getHeight() - window.getHeight()) / 2;
        window.setPosition(x, y);
    }

    // In showAndSelect() method - AnimalSlot isn't defined
// Fix: Replace with Table-based check
    public void showAndSelect(Animal animal) {
        if (!isVisible) {
            toggleVisibility();
        }

        for (Actor actor : animalTable.getChildren()) {
            if (actor instanceof Table) {  // Check for Table instead of AnimalSlot
                Table entry = (Table) actor;
                // Check if this is the animal we want to select
                // You'll need to store animal reference in each entry
            }
        }
    }

    private void selectAnimal(Animal animal) {
        this.selectedAnimal = animal;
        refresh(); // Refresh to update highlights
    }

    public AnimalManager getAnimalManager() {
        return animalManager;
    }

    public void setAnimalManager(AnimalManager animalManager) {
        this.animalManager = animalManager;
    }

    private void addInteractionButton(Table table, String text, Animal animal) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleAnimalInteraction(text, animal);
            }
        });
        table.add(button).fillX().padBottom(2).row();
    }

    private void handleAnimalInteraction(String action, Animal animal) {
        WorldScreen worldScreen = WorldScreen.getInstance();

        switch (action) {
            case "Pet":
                animal.pet();
                UIRenderer.showTextBox("You just pet your animal");
                break;
            case "Feed":
                animal.feed();
                break;
            case "Release":
                animal.goOut();
                break;
            case "Collect":
                if (animal.hasProduct()) {
                    // Handle product collection
                }
                break;
            case "Sell":
                worldScreen.getCurrentPlayer().increaseMoney(animal.getPrice());
                worldScreen.getCurrentPlayer().getAnimals().remove(animal);
                break;
        }

        refresh();
    }
}
