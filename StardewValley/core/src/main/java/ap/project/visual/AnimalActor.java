package ap.project.visual;

import ap.project.model.animal.Animal;
import ap.project.model.enums.animal_enums.Direction;
import ap.project.screen.AnimalInteractionScreen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class AnimalActor extends Actor {

    private final Animal animal;
    private float stateTime = 0f;
    private final AnimalInteractionScreen interactionScreen;

    public AnimalActor(Animal animal, AnimalInteractionScreen interactionScreen) {
        this.animal = animal;
        this.interactionScreen = interactionScreen;
        setPosition(animal.getX(), animal.getY());
        setSize(16, 16);
        setTouchable(Touchable.enabled);

        // Right-click listener for animal interaction
        addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Only show interaction if not already visible
                if (!interactionScreen.isVisible()) {
                    Vector2 screenPos = localToStageCoordinates(new Vector2(x, y));
                    interactionScreen.show(animal, screenPos.x, screenPos.y + getHeight());

                    // Bring interaction screen to front
                    interactionScreen.getStage().getRoot().addActor(interactionScreen.getWindow());
                    interactionScreen.getWindow().toFront();
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        animal.setX(getX());
        animal.setY(getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Animation<TextureRegion> animation;
        if (animal.getCurrentState() == Animal.State.WALKING) {
            animation = animal.getAnimalType().getWalkAnimation(animal.getDirection());
        } else {
            animation = animal.getAnimalType().getIdleAnimation();
        }

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    public void moveTo(float targetX, float targetY) {
        animal.setTargetX(targetX);
        animal.setTargetY(targetY);
        animal.setCurrentState(Animal.State.WALKING);

        if (Math.abs(targetX - getX()) > Math.abs(targetY - getY())) {
            animal.setDirection(targetX > getX() ? Direction.RIGHT : Direction.LEFT);
        } else {
            animal.setDirection(targetY > getY() ? Direction.UP : Direction.DOWN);
        }

        float distance = Vector2.dst(getX(), getY(), targetX, targetY);
        float duration = distance / 30f;

        this.addAction(Actions.sequence(
            Actions.moveTo(targetX, targetY, duration),
            Actions.run(() -> animal.setCurrentState(Animal.State.IDLE))
        ));
    }

    public Animal getAnimal() {
        return animal;
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

}
