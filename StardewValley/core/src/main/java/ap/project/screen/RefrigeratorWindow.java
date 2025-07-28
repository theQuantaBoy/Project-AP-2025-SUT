package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.App.Result;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.building_enums.KitchenRecipe;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.model.tools.BackPack;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class RefrigeratorWindow
{
    private final Window window;
    private final Stage stage;
    private final Table refrigeratorGrid;
    private final Table inventoryGrid;
    private final Skin skin;
    private boolean isVisible = false;
    private static final int ROWS = 3;
    private static final int COLS = 12;
    private static final int SLOT_SIZE = 48;
    private static final int GRID_SPACING = 8;
    private final TooltipManager tooltipManager = TooltipManager.getInstance();
    private Drawable selectionBorderDrawable;
    private Drawable slotBackground;
    private final Player player;

    private static final int BORDER_THICKNESS = 4; // Customizable border thickness
    private static final Color BORDER_COLOR = new Color(101/255f, 67/255f, 33/255f, 1f); // Dark brown

    // Selection tracking
    private GameObject selectedItem;
    private boolean isRefrigeratorSource;
    private int selectedIndex = -1;

    public RefrigeratorWindow(Stage stage)
    {
        this.stage = stage;
        this.player = App.getCurrentGame().getCurrentPlayer();
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        window = new Window("Refrigerator", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(10);

        // Create selection border and slot background
        createSelectionBorder();
        slotBackground = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(SLOT_SIZE, SLOT_SIZE, new Color(0.3f, 0.3f, 0.3f, 0.7f));

        // Create grids
        refrigeratorGrid = new Table();
        inventoryGrid = new Table();

        // Build UI structure
        Table mainLayout = new Table();
        mainLayout.add(new Label("Refrigerator", skin)).colspan(COLS).padBottom(10).row();
        mainLayout.add(refrigeratorGrid).colspan(COLS).padBottom(20).row();

        // Create custom separator
        Pixmap separatorPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        separatorPixmap.setColor(Color.BROWN);
        separatorPixmap.fill();
        Image separator = new Image(new Texture(separatorPixmap));
        separator.setSize(600, 2); // Width matches window
        separatorPixmap.dispose();

        mainLayout.add(separator).colspan(COLS).padBottom(20).fillX().row();

        mainLayout.add(new Label("Inventory", skin)).colspan(COLS).padBottom(10).row();
        mainLayout.add(inventoryGrid).colspan(COLS).padBottom(20).row();

        // OK button
        TextButton okButton = new TextButton("OK", skin);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleVisibility();
            }
        });
        mainLayout.add(okButton).colspan(COLS).padTop(20);

        float width = (SLOT_SIZE + 16) * (COLS) + 320;
        float height = (SLOT_SIZE + 16) * (2 * ROWS) + 360;

        window.add(mainLayout);
        window.pack();
        window.setSize(width, height);
        center();
        stage.addActor(window);
    }

    private void createSelectionBorder()
    {
        Pixmap borderPixmap = new Pixmap(SLOT_SIZE, SLOT_SIZE, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(BORDER_COLOR);
        borderPixmap.fillRectangle(0, 0, SLOT_SIZE, BORDER_THICKNESS); // Top border
        borderPixmap.fillRectangle(0, SLOT_SIZE - BORDER_THICKNESS, SLOT_SIZE, BORDER_THICKNESS); // Bottom border
        borderPixmap.fillRectangle(0, 0, BORDER_THICKNESS, SLOT_SIZE); // Left border
        borderPixmap.fillRectangle(SLOT_SIZE - BORDER_THICKNESS, 0, BORDER_THICKNESS, SLOT_SIZE); // Right border
        selectionBorderDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();
    }

    private void refresh()
    {
        refrigeratorGrid.clear();
        inventoryGrid.clear();

        // Build refrigerator grid
        ArrayList<GameObject> refrigerator = player.getRefrigerator();
        buildGrid(refrigeratorGrid, player.getRefrigerator(), true);

        // Build inventory grid
        ArrayList<GameObject> inventory = player.getInventory();
        buildGrid(inventoryGrid, inventory, false);
    }

    private void buildGrid(Table grid, ArrayList<GameObject> items, boolean isRefrigerator)
    {
        grid.defaults().size(SLOT_SIZE).pad(GRID_SPACING);

        Drawable tooltipBg = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.8f));
        BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);
        BitmapFont quantityFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 16, Color.WHITE);

        for (int i = 0; i < ROWS * COLS; i++)
        {
            final int index = i;
            final boolean isSourceRefrigerator = isRefrigerator;

            // Use Stack instead of Container for better layering
            Stack slotStack = new Stack();
            slotStack.setSize(SLOT_SIZE, SLOT_SIZE);

            // Add slot background first
            Image bgImage = new Image(slotBackground);
            slotStack.add(bgImage);

            // Add click listener
            slotStack.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    handleSlotClick(index, isSourceRefrigerator, items);
                }
            });

            // Add item if present
            if (i < items.size() && items.get(i) != null)
            {
                GameObject item = items.get(i);
                Texture texture = item.getObjectType().getTexture();

                // Create container for item to ensure proper scaling
                Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
                image.setSize(SLOT_SIZE, SLOT_SIZE);

                slotStack.addActor(image);

                // Add quantity text in a separate table at bottom right
                if (item.getNumber() > 1)
                {
                    Table quantityTable = new Table();
                    quantityTable.setFillParent(true);
                    quantityTable.bottom().right();

                    Label quantityLabel = new Label(String.valueOf(item.getNumber()),
                        new Label.LabelStyle(quantityFont, Color.WHITE));
                    quantityTable.add(quantityLabel).pad(2);

                    slotStack.add(quantityTable);
                }

                if (item == selectedItem)
                {
                    Image borderImage = new Image(selectionBorderDrawable);
                    slotStack.addActor(borderImage);
                }
            }

            // Add tooltip
            if (i < items.size() && items.get(i) != null)
            {
                GameObject item = items.get(i);
                Label tooltipLabel = new Label(item.getObjectType().toString(),
                    new Label.LabelStyle(tooltipFont, Color.WHITE));
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBg);
                tooltip.getContainer().pad(5);
                slotStack.addListener(tooltip);
            }

            grid.add(slotStack).size(SLOT_SIZE, SLOT_SIZE);
            if ((i + 1) % COLS == 0) grid.row();
        }
    }

    private void handleSlotClick(int index, boolean isRefrigerator, List<GameObject> items)
    {
        // Get item at clicked position
        GameObject clickedItem = (index < items.size()) ? items.get(index) : null;

        // First click: select item
        if (clickedItem != null)
        {
            selectedItem = clickedItem;
            selectedIndex = index;
            isRefrigeratorSource = isRefrigerator;
            refresh();

            UIRenderer.showTextBox("selected " + selectedItem.getObjectType());
        }

        // Second click: transfer items
        else if ((selectedItem != null && clickedItem == null))
        {
            // Prevent transferring to same grid
            if (isRefrigeratorSource == isRefrigerator)
            {
                UIRenderer.showTextBox("Cannot move within the same grid");

                selectedItem = null;
                selectedIndex = -1;
                refresh();
                return;
            }

            // Transfer items
            if (isRefrigeratorSource)
            {
                transferFromRefrigeratorToInventory();
            } else
            {
                transferFromInventoryToRefrigerator();
            }

            selectedItem = null;
            selectedIndex = -1;
            refresh();
        }
    }

    // Placeholder for food restriction logic
    private boolean canBeStoredInRefrigerator(GameObjectType type)
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (type == null)
        {
            return false;
        }

        if (!KitchenRecipe.isEdible(type))
        {
            return false;
        }

        if (player.getRefrigerator().size() == 36)
        {
            return false;
        }

        return true;
    }

    private void transferFromRefrigeratorToInventory()
    {
        // Placeholder for capacity check
        if (!player.getCurrentBackPack().hasEmptySlot())
        {
            UIRenderer.showTextBox("Inventory is full!");
            return;
        }

        if (!canBeStoredInRefrigerator(selectedItem.getObjectType()))
        {
            UIRenderer.showTextBox("This can't be stored in refrigerator!");
            return;
        }

        // Perform transfer
        player.getRefrigerator().remove(selectedItem);
        player.addToInventory(selectedItem);

        UIRenderer.showTextBox("You successfully picked " + selectedItem.getObjectType() + " from the refrigerator.");
    }

    private void transferFromInventoryToRefrigerator()
    {
        // Placeholder for capacity check
        if (player.getRefrigerator().size() >= 36)
        {
            UIRenderer.showTextBox("Refrigerator is full!");
            return;
        }

        if (!canBeStoredInRefrigerator(selectedItem.getObjectType()))
        {
            UIRenderer.showTextBox("This can't be stored in refrigerator!");
            return;
        }

        player.removeFromInventory(selectedItem);
        player.getRefrigerator().add(selectedItem);

        UIRenderer.showTextBox("You successfully put " + selectedItem.getObjectType() + " into your inventory.");
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
        if (isVisible) refresh();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        window.remove();
    }
}
