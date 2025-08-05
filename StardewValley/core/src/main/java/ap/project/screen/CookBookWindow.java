package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.Result;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.building_enums.KitchenRecipe;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ap.project.model.App.GameAssetsManager;

import java.util.ArrayList;
import java.util.HashMap;

public class CookBookWindow
{
    private final Window window;
    private final Stage stage;
    private final Table recipesTable;
    private final Skin skin;
    private boolean isVisible = false;
    private static final int ROWS = 4;
    private static final int COLS = 6;
    private static final int SLOT_SIZE = 48;
    private static final float TOOLTIP_FONT_SCALE = 0.7f;
    private final TooltipManager tooltipManager = TooltipManager.getInstance();
    private KitchenRecipe selectedRecipe = null;
    private TextButton cookButton;
    private TextButton closeButton;
    private Drawable selectionBorderDrawable;

    private static final int BORDER_THICKNESS = 4; // Customizable border thickness
    private static final Color BORDER_COLOR = new Color(101/255f, 67/255f, 33/255f, 1f); // Dark brown
    private static final Color LOCKED_COLOR = new Color(0.6f, 0.6f, 0.6f, 0.5f); // Adjusted gray

    public CookBookWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        window = new Window("Cook Book", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(20);

        // Create selection border drawable (dark brown border with customizable thickness)
        Pixmap borderPixmap = new Pixmap(SLOT_SIZE, SLOT_SIZE, Pixmap.Format.RGBA8888);
        // Fill with transparent
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        // Set border color
        borderPixmap.setColor(BORDER_COLOR);
        // Draw border rectangles with customizable thickness
        borderPixmap.fillRectangle(0, 0, SLOT_SIZE, BORDER_THICKNESS); // Top border
        borderPixmap.fillRectangle(0, SLOT_SIZE - BORDER_THICKNESS, SLOT_SIZE, BORDER_THICKNESS); // Bottom border
        borderPixmap.fillRectangle(0, 0, BORDER_THICKNESS, SLOT_SIZE); // Left border
        borderPixmap.fillRectangle(SLOT_SIZE - BORDER_THICKNESS, 0, BORDER_THICKNESS, SLOT_SIZE); // Right border
        selectionBorderDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();

        recipesTable = new Table();
        recipesTable.defaults().size(SLOT_SIZE).pad(8); // Increased padding between slots
        refreshRecipesTable();

        // Create cook button (using regular skin with natural size)
        cookButton = new TextButton("Cook", skin);
        cookButton.setDisabled(true); // Initially disabled

        cookButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                if (selectedRecipe != null)
                {
                    processCooking(selectedRecipe);
                }
            }
        });

        closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleVisibility(); // Close the window
            }
        });

        // Create button container
        Table buttonContainer = new Table();
        buttonContainer.defaults().pad(5);
        buttonContainer.add(cookButton).width(200);
        buttonContainer.add(closeButton).width(200);

        // Main container for recipes and button
        Table container = new Table();
        container.add(recipesTable).row();
        container.add(buttonContainer).padTop(15).padBottom(10);

        // Calculate window size to fit all recipes and button
        float width = (SLOT_SIZE + 16) * (COLS) + 160;
        float height = (SLOT_SIZE + 16) * (ROWS) + 200; // Adjusted height for natural button size

        window.add(container).pad(15);
        window.pack();
        window.setSize(width, height);
        center();
        stage.addActor(window);
    }

    private void refreshRecipesTable()
    {
        recipesTable.clear();
        int count = 0;

        // Create background for tooltips
        Drawable tooltipBg = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.8f));

        for (KitchenRecipe recipe : KitchenRecipe.values())
        {
            Stack slotContainer = new Stack();
            slotContainer.setSize(SLOT_SIZE, SLOT_SIZE);

            // Recipe texture
            Texture texture = recipe.getType().getTexture();
            Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
            image.setSize(SLOT_SIZE, SLOT_SIZE);

            Player player = App.getCurrentGame().getCurrentPlayer();
            ArrayList<KitchenRecipe> recipes = player.getCookingRecipes();

            if (!recipes.contains(recipe))
            {
                // Apply gray tint directly to the texture (better than overlay)
                image.setColor(LOCKED_COLOR);
            }
            else
            {
                // Reset to normal color for unlocked recipes
                image.setColor(Color.WHITE);
            }

            // Add click listener only for all recipes
            slotContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedRecipe = recipe;
                    refreshRecipesTable();
                    cookButton.setDisabled(false);
                }
            });

            slotContainer.addActor(image);

            // Add selection border if this recipe is selected
            if (recipe == selectedRecipe) {
                Image borderImage = new Image(selectionBorderDrawable);
                slotContainer.addActor(borderImage);
            }

            BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);
            Label.LabelStyle labelStyle = new Label.LabelStyle(tooltipFont, Color.WHITE);
            Label tooltipLabel = new Label(recipe.getInfo(), labelStyle);

            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(5);

            slotContainer.addListener(tooltip);

            recipesTable.add(slotContainer).size(SLOT_SIZE, SLOT_SIZE).pad(8);

            if (++count % COLS == 0)
            {
                recipesTable.row();
            }
        }
    }

    private void center()
    {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }

    public void toggleVisibility()
    {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible) {
            refreshRecipesTable();
            // Reset selection when window is opened
            selectedRecipe = null;
            cookButton.setDisabled(true);
        }
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public void dispose()
    {
        window.remove();
    }

    private void processCooking(KitchenRecipe recipe)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();
        ArrayList<KitchenRecipe> recipes = player.getCookingRecipes();

        if (!recipes.contains(recipe))
        {
            UIRenderer.showTextBox("You do not have access to this recipe.");
            return;
        }

        if (!player.inventoryHasCapacity())
        {
            UIRenderer.showTextBox("You don't have any capacity left in your backpack.");
            return;
        }

        ArrayList<GameObject> refrigerator = player.getRefrigerator();
        HashMap<GameObjectType, Integer> ingredients = recipe.getIngredients();
        boolean canAfford = true;

        for (GameObjectType a : ingredients.keySet())
        {
            int amount = player.howManyInInventory(a) + player.howManyInRefrigerator(a);
            if (amount < ingredients.get(a))
            {
                canAfford = false;
            }
        }

        if (!canAfford)
        {
            UIRenderer.showTextBox("You don't have enough ingredients for this recipe :(");
            return;
        }

        for (GameObjectType a : ingredients.keySet())
        {
            int removedFromInventory = Math.min(player.howManyInInventory(a), ingredients.get(a));
            if (removedFromInventory > 0)
            {
                player.removeAmountFromInventory(a, removedFromInventory);
            }

            int removedFromRefrigerator = ingredients.get(a) - removedFromInventory;
            if (removedFromRefrigerator > 0)
            {
                player.removeAmountFromRefrigerator(a, removedFromRefrigerator);
            }
        }

        player.increaseEnergy(-3);

        GameObject food = new GameObject(recipe.getType(), 1);
        player.addToInventory(food);

        UIRenderer.showTextBox("Did Gordon Ramsay teach you how to cook, or is he taking notes now?\n" +
            "You just cooked one " + food.getObjectType() + ".");
    }

    public Window getWindow() {
        return window;
    }
}
