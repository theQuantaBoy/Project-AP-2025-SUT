package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.enums.building_enums.KitchenRecipe;
import ap.project.model.game.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ap.project.model.App.GameAssetsManager;

import java.util.ArrayList;

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

    public CookBookWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        window = new Window("Cook Book", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(20); // Increased padding for larger window

        recipesTable = new Table();
        recipesTable.defaults().size(SLOT_SIZE).pad(8); // Increased padding between slots
        refreshRecipesTable();

        // Calculate window size to fit all recipes without scrolling
        float width = (SLOT_SIZE + 16) * (COLS) + 80;   // 16 padding per slot + extra margin
        float height = (SLOT_SIZE + 16) * (ROWS) + 120;  // 16 padding + more top/bottom margin


        // Add recipes table directly to window (no scroll pane)
        window.add(recipesTable).pad(15);
        window.pack();
        window.setSize(width, height); // Set fixed size
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
            Table slot = new Table();

            // Recipe texture
            Texture texture = recipe.getType().getTexture();
            Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
            slot.add(image).size(SLOT_SIZE - 10); // Slightly smaller than slot

            Player player = App.getCurrentGame().getCurrentPlayer();
            ArrayList<KitchenRecipe> recipes = player.getCookingRecipes();

            // Gray overlay for locked recipes
            if (!recipes.contains(recipe))
            {
                Drawable grayOverlay = GameAssetsManager.getGameAssetsManager()
                    .createColoredDrawable(SLOT_SIZE, SLOT_SIZE, new Color(0.5f, 0.5f, 0.5f, 0.7f));
                Image overlay = new Image(grayOverlay);
                slot.addActor(overlay);
            }

            BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);
            Label.LabelStyle labelStyle = new Label.LabelStyle(tooltipFont, Color.WHITE);
            Label tooltipLabel = new Label(recipe.getInfo(), labelStyle);

            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(5);

            slot.addListener(tooltip);

            recipesTable.add(slot).pad(8); // Increased padding between slots

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
        if (isVisible) refreshRecipesTable();
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public void dispose()
    {
        window.remove();
    }
}
