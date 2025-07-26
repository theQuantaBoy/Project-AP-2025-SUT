package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.enums.GameObjectType;
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

import java.util.List;

public class RefrigeratorWindow {
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

    // Selection tracking
    private GameObject selectedItem;
    private boolean isRefrigeratorSource;
    private int selectedIndex = -1;

    public RefrigeratorWindow(Stage stage) {
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

        window.add(mainLayout);
        window.pack();
        window.setSize(800, 600);
        center();
        stage.addActor(window);
    }

    private void createSelectionBorder() {
        Pixmap borderPixmap = new Pixmap(SLOT_SIZE, SLOT_SIZE, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(new Color(101/255f, 67/255f, 33/255f, 1f));
        borderPixmap.fillRectangle(0, 0, SLOT_SIZE, 4);
        borderPixmap.fillRectangle(0, SLOT_SIZE - 4, SLOT_SIZE, 4);
        borderPixmap.fillRectangle(0, 0, 4, SLOT_SIZE);
        borderPixmap.fillRectangle(SLOT_SIZE - 4, 0, 4, SLOT_SIZE);
        selectionBorderDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();
    }

    private void refresh() {
        refrigeratorGrid.clear();
        inventoryGrid.clear();
        selectedItem = null;
        selectedIndex = -1;

        // Build refrigerator grid
        buildGrid(refrigeratorGrid, player.getRefrigerator(), true);

        // Build inventory grid
        List<GameObject> inventory = player.getCurrentBackPack().getSlots();
        buildGrid(inventoryGrid, inventory, false);
    }

    private void buildGrid(Table grid, List<GameObject> items, boolean isRefrigerator) {
        grid.defaults().size(SLOT_SIZE).pad(GRID_SPACING);

        Drawable tooltipBg = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.8f));
        BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);
        BitmapFont quantityFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 16, Color.WHITE);

        for (int i = 0; i < ROWS * COLS; i++) {
            final int index = i;
            final boolean isSourceRefrigerator = isRefrigerator;

            // Use Container instead of Stack for background support
            Container<Table> slotContainer = new Container<>();
            slotContainer.setSize(SLOT_SIZE, SLOT_SIZE);
            slotContainer.setBackground(slotBackground); // Set slot background

            Table content = new Table();
            slotContainer.setActor(content);

            // Check if this slot is selected
            boolean isSelected = (selectedIndex == index) &&
                (isRefrigeratorSource == isRefrigerator);

            // Add selection border if selected
            if (isSelected) {
                content.add(new Image(selectionBorderDrawable)).size(SLOT_SIZE, SLOT_SIZE);
            }

            // Add item if present
            if (i < items.size() && items.get(i) != null) {
                GameObject item = items.get(i);
                Texture texture = item.getObjectType().getTexture();
                Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
                image.setSize(SLOT_SIZE, SLOT_SIZE);
                content.add(image).size(SLOT_SIZE, SLOT_SIZE);

                // Add quantity text
                if (item.getNumber() > 1) {
                    Label quantityLabel = new Label(String.valueOf(item.getNumber()),
                        new Label.LabelStyle(quantityFont, Color.WHITE));
                    quantityLabel.setAlignment(Align.bottomRight);
                    content.add(quantityLabel).size(SLOT_SIZE, SLOT_SIZE);
                }

                // Add tooltip
                Label tooltipLabel = new Label(item.getObjectType().toString(),
                    new Label.LabelStyle(tooltipFont, Color.WHITE));
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBg);
                tooltip.getContainer().pad(5);
                slotContainer.addListener(tooltip);
            }

            // Add click listener
            slotContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleSlotClick(index, isSourceRefrigerator, items);
                }
            });

            grid.add(slotContainer);
            if ((i + 1) % COLS == 0) grid.row();
        }
    }

    private void handleSlotClick(int index, boolean isRefrigerator, List<GameObject> items) {
        // Get item at clicked position
        GameObject clickedItem = (index < items.size()) ? items.get(index) : null;

        // First click: select item
        if (selectedItem == null) {
            if (clickedItem != null) {
                selectedItem = clickedItem;
                selectedIndex = index;
                isRefrigeratorSource = isRefrigerator;
                refresh();
            }
        }
        // Second click: transfer items
        else {
            // Prevent transferring to same grid
            if (isRefrigeratorSource == isRefrigerator) {
                selectedItem = null;
                selectedIndex = -1;
                refresh();
                return;
            }

            // Transfer items
            if (isRefrigeratorSource) {
                transferFromRefrigeratorToInventory(index, clickedItem);
            } else {
                transferFromInventoryToRefrigerator(index, clickedItem);
            }

            selectedItem = null;
            selectedIndex = -1;
            refresh();
        }
    }

    // Placeholder for food restriction logic
    private boolean canBeStoredInRefrigerator(GameObjectType type) {
        // TODO: Implement food-only logic
        return true;
    }

    private void transferFromRefrigeratorToInventory(int targetIndex, GameObject targetItem) {
        // Placeholder for capacity check
        if (!player.getCurrentBackPack().hasEmptySlot()) {
            UIRenderer.showTextBox("Inventory is full!");
            return;
        }

        if (!canBeStoredInRefrigerator(selectedItem.getObjectType())) {
            UIRenderer.showTextBox("This can't be stored in refrigerator!");
            return;
        }

        // Perform transfer
        player.getRefrigerator().remove(selectedIndex);
        player.addToInventory(selectedItem);
    }

    private void transferFromInventoryToRefrigerator(int targetIndex, GameObject targetItem) {
        // Placeholder for capacity check
        if (player.getRefrigerator().size() >= 36) {
            UIRenderer.showTextBox("Refrigerator is full!");
            return;
        }

        if (!canBeStoredInRefrigerator(selectedItem.getObjectType())) {
            UIRenderer.showTextBox("This can't be stored in refrigerator!");
            return;
        }

        // Perform transfer
        player.getCurrentBackPack().removeItem(selectedItem);
        player.getRefrigerator().add(selectedItem);
    }

    private void center() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }

    public void toggleVisibility() {
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
