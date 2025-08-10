package ap.project.screen;

import ap.project.model.animal.Animal;
import ap.project.visual.AnimalActor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class AnimalManager implements Disposable {
    private final Stage animalStage;
    private final Array<AnimalActor> animalActors;
    private float animalMoveTimer = 0f;

    public AnimalManager(float viewportWidth, float viewportHeight) {
        this.animalStage = new Stage(new ExtendViewport(viewportWidth, viewportHeight));
        this.animalActors = new Array<>();
    }

    public void addAnimal(Animal animal) {
        AnimalActor actor = new AnimalActor(animal);
        //actor.setPosition(x, y);
        animalActors.add(actor);
        animalStage.addActor(actor);
    }

    public void update(float deltaTime) {
        animalMoveTimer += deltaTime;
        if (animalMoveTimer > 3f) {
            animalMoveTimer = 0;
            updateAnimalMovement();
        }
        animalStage.act(deltaTime);
    }

    private void updateAnimalMovement() {
        for (AnimalActor actor : animalActors) {
            Animal animal = actor.getAnimal();
            if (!animal.isIn() && animal.getCurrentState() == Animal.State.IDLE && MathUtils.randomBoolean(0.5f)) {
                float moveDist = 50f;
                float targetX = actor.getX() + MathUtils.random(-moveDist, moveDist);
                float targetY = actor.getY() + MathUtils.random(-moveDist, moveDist);

                // Clamp to stay within bounds (you might want to pass map dimensions)
                targetX = MathUtils.clamp(targetX, 0, 1000); // Example bounds
                targetY = MathUtils.clamp(targetY, 0, 1000);

                actor.moveTo(targetX, targetY);
            }
        }
    }

    public void render(Batch batch) {
        animalStage.getViewport().apply();
        animalStage.draw();
    }

    public void resize(int width, int height) {
        animalStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        animalStage.dispose();
    }

    public Stage getStage() {
        return animalStage;
    }

    public Array<AnimalActor> getAnimalActors() {
        return animalActors;
    }


}
