package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.building.CraftingItem;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.enums.building_enums.ArtisanGoodsType;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class CraftingItemWindow
{
    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private boolean isVisible = false;
    private CraftingItem currentItem;

    // Grid components for PERIODIC state
    private Table craftingGrid;
    private Table inventoryGrid;
    private static final int CRAFTING_ROWS = 1;
    private static final int CRAFTING_COLS = 6;
    private static final int INVENTORY_ROWS = 3;
    private static final int INVENTORY_COLS = 12;
    private static final int SLOT_SIZE = 48;
    private static final int GRID_SPACING = 8;
    private Drawable selectionBorderDrawable;
    private Drawable slotBackground;

    // Selection tracking
    private GameObject selectedItem;
    private boolean isCraftingSource;
    private int selectedIndex = -1;

    // UI components
    private TextButton activateButton;
    private TextButton startButton;
    private TextButton cancelButton;
    private TextButton getProductButton;
    private TextButton getInstantlyButton;
    private Label statusLabel;
    private Dialog confirmationDialog;

    private ArrayList<GameObject> craftingIngredients = new ArrayList<>();

    public CraftingItemWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        window = new Window("Artisan Station", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(10);

        // Create selection border and slot background
        createSelectionBorder();
        slotBackground = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(SLOT_SIZE, SLOT_SIZE, new Color(0.3f, 0.3f, 0.3f, 0.7f));

        // Create confirmation dialog
        createConfirmationDialog();

        stage.addActor(window);
    }

    private void createSelectionBorder()
    {
        Pixmap borderPixmap = new Pixmap(SLOT_SIZE, SLOT_SIZE, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(new Color(101/255f, 67/255f, 33/255f, 1f));
        borderPixmap.fillRectangle(0, 0, SLOT_SIZE, 4); // Top border
        borderPixmap.fillRectangle(0, SLOT_SIZE - 4, SLOT_SIZE, 4); // Bottom border
        borderPixmap.fillRectangle(0, 0, 4, SLOT_SIZE); // Left border
        borderPixmap.fillRectangle(SLOT_SIZE - 4, 0, 4, SLOT_SIZE); // Right border
        selectionBorderDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();
    }

    private void createConfirmationDialog()
    {
        confirmationDialog = new Dialog("Confirmation", skin)
        {
            @Override
            protected void result(Object object)
            {
                if (object.equals(true))
                {
                    // Handle instant completion
                    UIRenderer.showTextBox("Completed instantly!");
                    currentItem.reset();
                    refresh();
                }
                confirmationDialog.hide();
            }
        };

        confirmationDialog.text("Are you sure you want to complete instantly?");
        confirmationDialog.button("Yes", true);
        confirmationDialog.button("No", false);
        confirmationDialog.setVisible(false);
        stage.addActor(confirmationDialog);
    }

    public void setItem(CraftingItem item)
    {
        this.currentItem = item;
        refresh();
    }

    public void toggleVisibility()
    {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible) center();
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    private void refresh()
    {
        window.clear();

        if (currentItem == null) return;

        switch (currentItem.getCraftingType().getItemType())
        {
            case ONE_TIME:
                buildOneTimeUI();
                break;
            case PERIODIC:
                if (currentItem.isWorking())
                {
                    if (currentItem.isDone())
                    {
                        buildPeriodicDoneUI();
                    } else {
                        buildPeriodicWorkingUI();
                    }
                } else
                {
                    buildPeriodicSetupUI();
                }
                break;
            case PERMANENT:
                buildPermanentUI();
                break;
        }

        window.pack();
        center();
    }

    private void buildOneTimeUI()
    {
        Table content = new Table();
        content.add(new Label("One-Time Crafting Device", skin)).padBottom(20).row();

        activateButton = new TextButton("Activate", skin);
        activateButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // Activate logic
                UIRenderer.showTextBox("Device activated!");
                toggleVisibility();
            }
        });

        content.add(activateButton).size(150, 50).row();
        window.add(content);
    }

    private void buildPeriodicSetupUI()
    {
        Table mainLayout = new Table();
        mainLayout.defaults().pad(5);

        // Crafting grid header
        mainLayout.add(new Label("Input Slots", skin)).colspan(INVENTORY_COLS).padBottom(10).row();

        // Center the crafting grid in a wrapper table
        Table centerWrapper = new Table();
        craftingGrid = new Table();
        buildGrid(craftingGrid, craftingIngredients, true, CRAFTING_COLS);
        centerWrapper.add(craftingGrid);
        mainLayout.add(centerWrapper).colspan(INVENTORY_COLS).padBottom(20).center().row();

        // Options button
        TextButton optionsButton = new TextButton("Recipes", skin);
        optionsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showRecipesDialog();
            }
        });
        mainLayout.add(optionsButton).padTop(10).padBottom(20).colspan(INVENTORY_COLS).row();

        // Separator
        mainLayout.add(createSeparator()).colspan(INVENTORY_COLS).fillX().padBottom(20).row();

        // Inventory grid
        mainLayout.add(new Label("Inventory", skin)).colspan(INVENTORY_COLS).padBottom(10).row();
        inventoryGrid = new Table();
        Player player = App.getCurrentGame().getCurrentPlayer();
        buildGrid(inventoryGrid, player.getInventory(), false, INVENTORY_COLS);
        mainLayout.add(inventoryGrid).colspan(INVENTORY_COLS).padBottom(20).row();

        // Buttons
        Table buttonTable = new Table();
        buttonTable.defaults().minWidth(120).pad(10);

        startButton = new TextButton("Start Crafting", skin);
        startButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // Start crafting logic
                ArtisanGoodsType goodsType = determineArtisanGoodsType();
                if (goodsType != null)
                {
                    currentItem.start(goodsType);
                    UIRenderer.showTextBox("Crafting started!");
                    refresh();
                } else
                {
                    UIRenderer.showTextBox("Invalid ingredients!");
                }
            }
        });

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toggleVisibility();
            }
        });

        buttonTable.add(startButton).padRight(20);
        buttonTable.add(closeButton);
        mainLayout.add(buttonTable).colspan(INVENTORY_COLS).padTop(10);

        window.add(mainLayout);
    }

    private void buildPeriodicWorkingUI()
    {
        Table content = new Table();
        content.defaults().pad(15);

        statusLabel = new Label("Crafting in progress...", skin);
        content.add(statusLabel).padBottom(30).row();

        // Progress bar
        ProgressBar progressBar = new ProgressBar(0, 1, 0.01f, false, skin);
        progressBar.setValue(currentItem.getHowMuchDone());
        progressBar.setAnimateDuration(0.5f);
        content.add(progressBar).width(300).height(30).padBottom(20).row();

        // Buttons
        Table buttonTable = new Table();
        buttonTable.defaults().minWidth(160).pad(10);

        cancelButton = new TextButton("Cancel Crafting", skin);
        cancelButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                currentItem.reset();
                UIRenderer.showTextBox("Crafting canceled!");
                toggleVisibility();
            }
        });

        getInstantlyButton = new TextButton("Complete Instantly", skin);
        getInstantlyButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                confirmationDialog.show(stage);
            }
        });

        buttonTable.add(cancelButton).padRight(20);
        buttonTable.add(getInstantlyButton);
        content.add(buttonTable).padTop(10);

        window.add(content);
    }

    private void buildPeriodicDoneUI()
    {
        Table content = new Table();
        content.defaults().pad(20);

        statusLabel = new Label("Crafting complete! Collect your item.", skin);
        content.add(statusLabel).padBottom(30).row();

        getProductButton = new TextButton("Collect Product", skin);
        getProductButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // Collect product logic
                GameObject product = currentItem.getProduct();
                if (product != null)
                {
                    App.getCurrentGame().getCurrentPlayer().addToInventory(product);
                    UIRenderer.showTextBox("Collected: " + product.getObjectType());
                }
                currentItem.reset();
                toggleVisibility();
            }
        });

        content.add(getProductButton).minWidth(200).minHeight(60).row();
        window.add(content);
    }

    private void buildPermanentUI()
    {
        Table content = new Table();
        content.defaults().pad(20);

        content.add(new Label("Permanent Device", skin)).padBottom(20).row();
        content.add(new Label("This device works automatically", skin)).padBottom(30).row();

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toggleVisibility();
            }
        });

        content.add(closeButton).minWidth(150).minHeight(50);
        window.add(content);
    }

    private Image createSeparator()
    {
        Pixmap separatorPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        separatorPixmap.setColor(Color.BROWN);
        separatorPixmap.fill();
        Image separator = new Image(new Texture(separatorPixmap));
        separator.setSize(600, 2);
        separatorPixmap.dispose();
        return separator;
    }

    private void buildGrid(Table grid, List<GameObject> items, boolean isCraftingGrid, int cols)
    {
        grid.defaults().size(SLOT_SIZE).pad(GRID_SPACING);
        grid.clear();

        BitmapFont quantityFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 16, Color.WHITE);

        int slots = isCraftingGrid ? CRAFTING_ROWS * CRAFTING_COLS : INVENTORY_ROWS * INVENTORY_COLS;

        for (int i = 0; i < slots; i++)
        {
            final int index = i;
            final boolean isCrafting = isCraftingGrid;

            Stack slotStack = new Stack();
            slotStack.setSize(SLOT_SIZE, SLOT_SIZE);

            Image bgImage = new Image(slotBackground);
            slotStack.add(bgImage);

            slotStack.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    handleSlotClick(index, isCrafting, items);
                }
            });

            if (i < items.size() && items.get(i) != null)
            {
                GameObject item = items.get(i);
                Texture texture = item.getObjectType().getTexture();
                Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
                image.setSize(SLOT_SIZE, SLOT_SIZE);
                slotStack.addActor(image);

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

            grid.add(slotStack);
            if ((i + 1) % cols == 0) grid.row();
        }
    }

    private void handleSlotClick(int index, boolean isCraftingSource, List<GameObject> items)
    {
        GameObject clickedItem = (index < items.size()) ? items.get(index) : null;

        if (clickedItem != null)
        {
            selectedItem = clickedItem;
            selectedIndex = index;
            this.isCraftingSource = isCraftingSource;
            refresh();
        } else if (selectedItem != null)
        {
            if (isCraftingSource == this.isCraftingSource)
            {
                UIRenderer.showTextBox("Cannot move within the same grid");

                selectedItem = null;
                selectedIndex = -1;
                refresh();
                return;
            } else
            {
                // Handle transfer between grids
                if (this.isCraftingSource)
                {
                    // Moving from crafting grid to inventory
                    transferFromCraftTableToInventory();
                } else
                {
                    // Moving from inventory to crafting grid
                    transferFromInventoryToCraftTable();
                }
            }

            selectedItem = null;
            selectedIndex = -1;
            refresh();
        }
    }

    private void transferFromInventoryToCraftTable()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (selectedItem == null)
        {
            UIRenderer.showTextBox("please first select an item from inventory");
            return;
        }

        if (craftingIngredients.size() >= CRAFTING_COLS)
        {
            UIRenderer.showTextBox("the craft grid is full");
            return;
        }

        for (GameObject item : craftingIngredients)
        {
            if (item.getObjectType() == selectedItem.getObjectType())
            {
                item.setNumber(item.getNumber() + 1);
                player.removeAmountFromInventory(selectedItem.getObjectType(), 1);
                return;
            }
        }

        craftingIngredients.add(new GameObject(selectedItem.getObjectType(), 1));
        player.removeAmountFromInventory(selectedItem.getObjectType(), 1);
    }

    private void transferFromCraftTableToInventory()
    {
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (!player.getCurrentBackPack().hasEmptySlot())
        {
            UIRenderer.showTextBox("Inventory is full!");
            return;
        }

        if (selectedItem != null)
        {
            player.addToInventory(selectedItem);
            craftingIngredients.remove(selectedItem);
        }
    }

    private ArtisanGoodsType determineArtisanGoodsType()
    {
        // Placeholder - to be implemented later
        return null;
    }

    private void showRecipesDialog()
    {
        Dialog recipesDialog = new Dialog("Available Recipes", skin);
        recipesDialog.setModal(true);
        recipesDialog.setMovable(true);
        recipesDialog.setResizable(true);

        ScrollPane scrollPane = new ScrollPane(new Label("Recipe list will be shown here", skin));
        scrollPane.setFadeScrollBars(false);
        recipesDialog.getContentTable().add(scrollPane).width(400).height(300);

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                recipesDialog.hide();
            }
        });
        recipesDialog.getButtonTable().add(closeButton);

        recipesDialog.show(stage);
        recipesDialog.setSize(500, 400);
        recipesDialog.setPosition(
            (stage.getWidth() - recipesDialog.getWidth()) / 2,
            (stage.getHeight() - recipesDialog.getHeight()) / 2
        );
    }

    private void center()
    {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }
}
