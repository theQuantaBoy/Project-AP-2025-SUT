package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.enums.*;
import ap.project.model.enums.building_enums.CraftingRecipeEnums;
import ap.project.model.enums.building_enums.KitchenRecipe;
import ap.project.model.building.CraftingItem;
import ap.project.model.game.*;
import ap.project.model.player_data.Skill;
import ap.project.model.shops.Shop;
import ap.project.model.tools.Tool;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.tools.BackPack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryWindow {
    private final Window popup;
    private final Stage stage;
    private final Table inventoryTable;
    private final Table skillsTable;
    private final Table socialTable;
    private final Table mapTable;
    private final Table toolsTable;
    private final Table settingsTable;
    private final Stack contentStack;
    private BackPack backpack;
    private final Skin skin;
    private boolean isVisible = false;
    private static final int COLS = 8;
    private static final int SLOTS_SIZE = 64;
    private Drawable slotBackground; // For inventory slot backgrounds
    private Drawable slotHighlight;
    private Player player;
    private TooltipManager tooltipManager = TooltipManager.getInstance();
    private Drawable tooltipBg;
    private TextButton toolsTab;
    private TextButton mapTab;
    private TextButton settingsTab;
    public enum TabType { INVENTORY, SKILL, SOCIAL, MAP, TOOLS, SETTINGS}

    private TabType lastTabOpenedByTabKey = TabType.INVENTORY;
    private WorldScreen worldScreen;

    private TextButton craftingTab;
    private Table craftingTable;
    private CraftingRecipeEnums selectedCraftingRecipe = null;
    private TextButton buildButton;
    private Drawable craftingSelectionBorderDrawable;
    private static final int CRAFTING_ROWS = 6;
    private static final int CRAFTING_COLS = 6;
    private static final int CRAFTING_SLOT_WIDTH = 48;
    private static final int CRAFTING_SLOT_HEIGHT = 96;
    private static final int CRAFTING_SLOT_PADDING = 8;

    private Table hotbarTable;
    private int selectedHotbarSlot = -1;
    private static final int HOTBAR_SLOTS = 8;

    private final ScrollPane inventoryScrollPane;
    private ImageButton trashButton;

    private static final int MAP_PADDING = 4;
    private static final int CITY_WIDTH = 406;
    private static final int FARM_WIDTH = 209;
    private static final int FARM_HEIGHT = 170;
    private static final int CITY_HEIGHT = FARM_HEIGHT * 2 + MAP_PADDING;
    private static final int AVATAR_SIZE = 48;

    // New field to track which inventory item is selected for hotbar assignment
    private GameObject selectedInventoryItemForHotbar = null;

    public InventoryWindow(Stage stage, WorldScreen worldScreen) {
        this.player = App.getCurrentGame().getCurrentPlayer();
        this.backpack = player.getCurrentBackPack();
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        this.stage = stage;
        this.worldScreen = worldScreen;
        // Create popup Window
        popup = new Window("Menu", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);

        Drawable trashDrawable = getIconForGameObject(new GameObject(GameObjectType.TRASH_CAN, 1));
        trashButton = new ImageButton(trashDrawable);
        trashButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedInventorySlot >= 0) {
                    // remove from backpack
                    GameObject selected = backpack.getSlots().get(selectedInventorySlot);
                    GameObjectType type = (selected != null) ? selected.getObjectType() : null;

                    player.removeAmountFromInventory(type, 1);
                    // reset selection
                    if (backpack.getSlots().get(selectedInventorySlot) == null) selectedInventorySlot = -1;

                    if (player.getCurrentObject() != null && player.getCurrentObject().getObjectType() == type)
                    {
                        player.setCurrentObject(null);
                    }

                    if (player.getCurrentTool() != null && player.getCurrentTool().getObjectType() == type)
                    {
                        player.setCurrentTool(null);
                    }

                    for (int i = 0; i < backpack.getHotbarSlots().size(); i++) {
                        GameObject temp = backpack.getHotbarSlots().get(i);
                        if (temp != null && temp.getObjectType() == type)
                        {
                            backpack.removeFromHotbar(i);
                        }
                    }

                    // rebuild UI
                    refreshInventoryTable();
                }
            }
        });

        // Create tab buttons
        TextButton invTab = new TextButton("Inventory", skin);
        TextButton skillsTab = new TextButton("Skills", skin);
        TextButton socialTab = new TextButton("Social", skin);
        mapTab = new TextButton("Map", skin);
        toolsTab = new TextButton("Tools", skin);
        this.slotBackground = GameAssetsManager.getGameAssetsManager().createColoredDrawable(SLOTS_SIZE, SLOTS_SIZE, new Color(0.3f, 0.3f, 0.3f, 0.7f));
        this.slotHighlight = GameAssetsManager.getGameAssetsManager().createColoredDrawable(SLOTS_SIZE, SLOTS_SIZE, new Color(0.5f, 0.5f, 0.5f, 0.9f));

        tooltipManager.initialTime = 0.5f; // Delay before tooltip shows
        tooltipManager.subsequentTime = 0.1f;
        tooltipBg = GameAssetsManager.getGameAssetsManager().createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        // Create content tables
        inventoryTable = new Table(skin);
        inventoryScrollPane = new ScrollPane(inventoryTable, skin);
        inventoryScrollPane.setFadeScrollBars(false);
        inventoryScrollPane.setScrollingDisabled(true, false); // ✅ Only vertical scrolling

        skillsTable    = buildSkillsTable();
        socialTable    = buildSocialTable();

        mapTable       = buildMapTable();
        refreshMapTable();

        toolsTable     = new Table(skin);
        settingsTable = buildSettingsTable();

        hotbarTable = new Table(skin);
        hotbarTable.defaults().size(SLOTS_SIZE).pad(2);
        hotbarTable.center();

        settingsTab = new TextButton("Settings", skin);
        craftingTab = new TextButton("Crafting", skin);
        craftingTable = new Table(skin);
        craftingTable.setVisible(false);

        // Create selection border drawable
        Pixmap borderPixmap = new Pixmap(CRAFTING_SLOT_WIDTH, CRAFTING_SLOT_HEIGHT, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(new Color(101/255f, 67/255f, 33/255f, 1f));
        borderPixmap.fillRectangle(0, 0, CRAFTING_SLOT_WIDTH, 4); // Top
        borderPixmap.fillRectangle(0, CRAFTING_SLOT_HEIGHT - 4, CRAFTING_SLOT_WIDTH, 4); // Bottom
        borderPixmap.fillRectangle(0, 0, 4, CRAFTING_SLOT_HEIGHT); // Left
        borderPixmap.fillRectangle(CRAFTING_SLOT_WIDTH - 4, 0, 4, CRAFTING_SLOT_HEIGHT); // Right
        craftingSelectionBorderDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();

        // Stack panels and hide non-inventory
        contentStack = new Stack(inventoryScrollPane, skillsTable, socialTable, mapTable, toolsTable, craftingTable, settingsTable);
        skillsTable.setVisible(false);
        socialTable.setVisible(false);
        mapTable.setVisible(false);
        toolsTable.setVisible(false);
        craftingTable.setVisible(false);
        settingsTable.setVisible(false);

        // Tab switching with inventory refresh
        invTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshInventoryTable();
                inventoryScrollPane.setVisible(true);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                craftingTable.setVisible(false);
                settingsTable.setVisible(false);
                popup.pack();
                center(stage);
                lastTabOpenedByTabKey = TabType.INVENTORY;
            }
        });
        skillsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshSkillsTable();
                inventoryScrollPane.setVisible(false);
                skillsTable.setVisible(true);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                craftingTable.setVisible(false);
                settingsTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        socialTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryScrollPane.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(true);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                craftingTable.setVisible(false);
                settingsTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        mapTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryScrollPane.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(true);
                toolsTable.setVisible(false);
                craftingTable.setVisible(false);
                settingsTable.setVisible(false);
                refreshMapTable();
                popup.pack();
                center(stage);
                lastTabOpenedByTabKey = TabType.MAP;
            }
        });
        toolsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryScrollPane.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(true);
                craftingTable.setVisible(false);
                settingsTable.setVisible(false);
                refreshToolTable();
                popup.pack();
                center(stage);

                lastTabOpenedByTabKey = TabType.TOOLS;
            }
        });

        craftingTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryScrollPane.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                craftingTable.setVisible(true);
                settingsTable.setVisible(false);
                refreshCraftingTable();
                popup.pack();
                center(stage);
            }
        });

        settingsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryScrollPane.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                craftingTable.setVisible(false);
                settingsTable.setVisible(true);
                popup.pack();
                lastTabOpenedByTabKey = TabType.SETTINGS;
            }
        });

        buildButton = new TextButton("Build", skin);
        buildButton.setDisabled(true);
        buildButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedCraftingRecipe != null) {
                    craftSelectedRecipe();
                }
            }
        });

        popup.row();
        popup.add(invTab).expandX().fillX();
        popup.add(skillsTab).expandX().fillX();
        popup.add(socialTab).expandX().fillX();
        popup.add(mapTab).expandX().fillX();
        popup.add(toolsTab).expandX().fillX();
        popup.add(craftingTab).expandX().fillX();
        popup.add(settingsTab).expandX().fillX();
        popup.row();
        popup.add(contentStack).colspan(6).expand().fill().center().row();
        popup.pack();
        center(stage);
        stage.addActor(popup);

        refreshHotbar();
    }

    /**
     * NEW METHOD: Handle number key presses for hotbar assignment
     * Call this method from your main input handler when number keys 1-8 are pressed
     */
    public void handleHotbarAssignment(int keycode) {
        if (!isVisible || selectedInventoryItemForHotbar == null) {
            return; // Only work when inventory is open and item is selected
        }

        int hotbarSlot = -1;

        // Map keycodes to hotbar slots (1-8 keys map to slots 0-7)
        switch (keycode) {
            case Input.Keys.NUM_1: hotbarSlot = 0; break;
            case Input.Keys.NUM_2: hotbarSlot = 1; break;
            case Input.Keys.NUM_3: hotbarSlot = 2; break;
            case Input.Keys.NUM_4: hotbarSlot = 3; break;
            case Input.Keys.NUM_5: hotbarSlot = 4; break;
            case Input.Keys.NUM_6: hotbarSlot = 5; break;
            case Input.Keys.NUM_7: hotbarSlot = 6; break;
            case Input.Keys.NUM_8: hotbarSlot = 7; break;
            default: return; // Invalid key
        }

        // Assign the selected item to the hotbar slot
        assignItemToHotbar(selectedInventoryItemForHotbar, hotbarSlot);

        // Show feedback to player
        UIRenderer.showTextBox(selectedInventoryItemForHotbar.getObjectType().toString() +
            " assigned to hotbar slot " + (hotbarSlot + 1));

        // Refresh the hotbar to show the change
        refreshHotbar();
    }

    /**
     * NEW METHOD: Assign an item to a specific hotbar slot
     */
    private void assignItemToHotbar(GameObject item, int hotbarSlot) {
        if (item == null || hotbarSlot < 0 || hotbarSlot >= HOTBAR_SLOTS) {
            return;
        }

        // Get the current hotbar slots
        java.util.List<GameObject> hotbarSlots = backpack.getHotbarSlots();

        // Ensure the hotbar list is large enough
        while (hotbarSlots.size() <= hotbarSlot) {
            hotbarSlots.add(null);
        }

        // Create a copy of the item for the hotbar (item stays in inventory)
        if (hotbarSlots.contains(item)) {
            hotbarSlots.remove(item);
        }
        GameObject hotbarItem = item;
        hotbarSlots.set(hotbarSlot, hotbarItem);

        // ADDED: Notify WorldScreen to refresh its hotbar
        if (worldScreen != null) {
            worldScreen.refreshHotbarUI();
        }
    }

    private void refreshHotbar() {
        hotbarTable.clear();
        updatePlayer();
        java.util.List<GameObject> hotbarSlots = backpack.getHotbarSlots();

        for (int slot = 0; slot < HOTBAR_SLOTS; slot++) {
            final int slotIndex = slot;
            GameObject obj = slot < hotbarSlots.size() ? hotbarSlots.get(slot) : null;

            Table slotContainer = new Table();
            slotContainer.setBackground(slotIndex == selectedHotbarSlot ? slotHighlight : slotBackground);
            slotContainer.setSize(SLOTS_SIZE, SLOTS_SIZE);

            if (obj != null) {
                Drawable icon = getSafeIcon(obj);
                Stack itemStack = new Stack();
                itemStack.add(new Image(icon));

                if (obj.getNumber() > 1) {
                    Label countLabel = new Label(String.valueOf(obj.getNumber()), skin);
                    countLabel.setFontScale(0.8f); // Smaller font for hotbar
                    countLabel.setAlignment(Align.bottomRight);

                    // Create container for padding
                    Table countContainer = new Table();
                    itemStack.setFillParent(true);
                    countContainer.bottom().right();
                    countContainer.add(countLabel).pad(0, 0, 4, 4); // Bottom and right padding

                    slotContainer.add(itemStack).expand().fill();
                    slotContainer.add(countContainer);
                } else {
                    slotContainer.add(itemStack).expand().fill();
                }

                // Tooltip
                String tooltipText = obj.getObjectType().toString();
                Label tooltipLabel = new Label(tooltipText, skin);
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBg);
                tooltip.getContainer().pad(8);
                slotContainer.addListener(tooltip);
                //stage.addActor(tooltip.getContainer());
            }

            // Add slot number label for better UX
            Label slotNumberLabel = new Label(String.valueOf(slot + 1), skin);
            slotNumberLabel.setFontScale(0.6f);
            slotNumberLabel.setColor(Color.GRAY);
            Table slotNumberContainer = new Table();
            slotNumberContainer.setFillParent(true);
            slotNumberContainer.top().left();
            slotNumberContainer.add(slotNumberLabel).pad(2, 2, 0, 0);

            slotContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameObject clickedObject = slotIndex < hotbarSlots.size() ?
                        hotbarSlots.get(slotIndex) : null;

                    if (event.getButton() == Input.Buttons.LEFT)
                    {
                        // Clear other selections
                        selectedInventorySlot = -1;
                        selectedToolSlot = -1;

                        // Toggle hotbar selection
                        if (selectedHotbarSlot == slotIndex) {
                            selectedHotbarSlot = -1;
                            player.setCurrentObject(null);
                        } else {
                            selectedHotbarSlot = slotIndex;
                            player.setCurrentObject(clickedObject);
                        }
                        refreshInventoryTable();
                        refreshToolTable();
                        refreshHotbar();
                    }
                }
            });

            // Add both the main content and the slot number
            Stack slotStack = new Stack();
            slotStack.add(slotContainer);
            slotStack.add(slotNumberContainer);

            hotbarTable.add(slotStack).size(SLOTS_SIZE, SLOTS_SIZE).pad(2);
        }

        if (worldScreen != null) {
            worldScreen.refreshHotbarUI();
        }
    }

    private void refreshInventoryTable() {
        inventoryTable.clear();
        Table content = buildLimitedInventoryTable();
        inventoryTable.add(content).expand().fill().center();
        refreshHotbar();
    }

    private void refreshCraftingTable()
    {
        craftingTable.clear();
        craftingTable.defaults().pad(CRAFTING_SLOT_PADDING).center();

        // Create main grid
        Table gridTable = new Table();
        gridTable.defaults().size(CRAFTING_SLOT_WIDTH, CRAFTING_SLOT_HEIGHT).pad(8);

        BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);
        Label.LabelStyle tooltipLabelStyle = new Label.LabelStyle(tooltipFont, Color.WHITE);

        int count = 0;
        for (CraftingRecipeEnums recipe : CraftingRecipeEnums.values())
        {
            Stack slotContainer = new Stack();
            slotContainer.setSize(CRAFTING_SLOT_WIDTH, CRAFTING_SLOT_HEIGHT);

            // Recipe texture
            Texture texture = recipe.getProduct().getTexture();
            Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
            image.setScaling(Scaling.fit);

            // Handle different texture sizes
            if (recipe.isTall())
            {
                image.setSize(CRAFTING_SLOT_WIDTH, CRAFTING_SLOT_HEIGHT);
            } else
            {
                image.setSize(CRAFTING_SLOT_WIDTH, CRAFTING_SLOT_WIDTH);
            }

            // Check if recipe is unlocked
            Player player = App.getCurrentGame().getCurrentPlayer();
            ArrayList<CraftingRecipeEnums> recipes = player.getCraftingRecipes();

            if (!recipes.contains(recipe))
            {
                image.setColor(new Color(0.6f, 0.6f, 0.6f, 0.5f));
            } else
            {
                image.setColor(Color.WHITE);
            }

            slotContainer.addActor(image);

            // Center square textures vertically
            if (!recipe.isTall())
            {
                image.setY((CRAFTING_SLOT_HEIGHT - CRAFTING_SLOT_WIDTH) / 2);
            }

            // Add click listener
            slotContainer.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    selectedCraftingRecipe = recipe;
                    refreshCraftingTable();
                    buildButton.setDisabled(false);
                }
            });

            if (recipe == selectedCraftingRecipe)
            {
                Image borderImage = new Image(craftingSelectionBorderDrawable);
                slotContainer.addActor(borderImage);
            }


            // Add tooltip
            TooltipLabel tooltipLabel = new TooltipLabel(recipe.getInfo(), tooltipLabelStyle);
            Tooltip<TooltipLabel> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(5);
            slotContainer.addListener(tooltip);

            gridTable.add(slotContainer);

            if (++count % CRAFTING_COLS == 0)
            {
                gridTable.row();
            }
        }

        // Add grid and build button to crafting table
        craftingTable.add(gridTable).center().row();
        craftingTable.add(buildButton).padTop(15);
    }

    private static class TooltipLabel extends Label
    {
        public TooltipLabel(CharSequence text, LabelStyle style)
        {
            super(text, style);
            setWrap(true);
        }

        @Override
        public float getPrefWidth()
        {
            return 400;
        }
    }

    private void craftSelectedRecipe()
    {
        if (selectedCraftingRecipe == null) return;

        Player player = App.getCurrentGame().getCurrentPlayer();

        ArrayList<CraftingRecipeEnums> recipes = player.getCraftingRecipes();
        if (!recipes.contains(selectedCraftingRecipe))
        {
            UIRenderer.showTextBox("You don't currently have access to this recipe.");
            return;
        }

        HashMap<GameObjectType, Integer> ingredients = selectedCraftingRecipe.getIngredients();

        for (GameObjectType type : ingredients.keySet())
        {
            if (!player.hasEnoughInInventory(type, ingredients.get(type)))
            {
                UIRenderer.showTextBox("You don't have enough of " + type + " in your inventory.");
                return;
            }
        }

        if (!player.inventoryHasCapacity())
        {
            UIRenderer.showTextBox("You don't have any capacity left in your backpack :(");
            return;
        }

        for (GameObjectType type : ingredients.keySet())
        {
            player.removeAmountFromInventory(type, ingredients.get(type));
        }

        player.increaseEnergy(-2);
        CraftingItem craftingItem = new CraftingItem(selectedCraftingRecipe, null);
        player.addToInventory(craftingItem);

        UIRenderer.showTextBox(selectedCraftingRecipe.getProduct() + " was added to your inventory.");

        refreshInventoryTable();
        refreshCraftingTable();
    }

    private Drawable createColoredDrawable(int width, int height, Color color)
    {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private void updatePlayer()
    {
        player = App.getCurrentGame().getCurrentPlayer();
        backpack = player.getCurrentBackPack();
    }

    private int selectedObjectSlot = -1;

    public Table buildLimitedInventoryTable() {
        // Create main container table (vertical layout)
        Table mainTable = new Table(skin);
        mainTable.defaults().pad(2);
        mainTable.center();

        // Add hotbar at the top
        mainTable.add(hotbarTable).colspan(COLS).padBottom(15).row();

        // Create inventory grid
        Table inventoryGrid = new Table(skin);
        inventoryGrid.defaults().size(SLOTS_SIZE).pad(2);
        inventoryGrid.center();

        updatePlayer();
        java.util.List<GameObject> slots = backpack.getNonEmptyItems();
        int capacity = backpack.getCapacity();
        int counter = 0;

        for (int slot = 0; slot < capacity; slot++) {
            final int slotIndex = slot;
            GameObject obj = slot < backpack.getItemCount() ? slots.get(counter++) : null;

            Table slotContainer = new Table();
            slotContainer.setBackground(slotIndex == selectedInventorySlot ? slotHighlight : slotBackground);
            slotContainer.setSize(SLOTS_SIZE, SLOTS_SIZE);

            if (obj != null) {
                Drawable icon = getSafeIcon(obj);
                Stack itemStack = new Stack();
                itemStack.add(new Image(icon));

                if (obj.getNumber() > 1) {
                    Label countLabel = new Label(String.valueOf(obj.getNumber()), skin);

                    // Create container for padding
                    Table countContainer = new Table();
                    itemStack.setFillParent(true);
                    countContainer.bottom().right();
                    countContainer.add(countLabel);// Bottom and right padding

                    slotContainer.add(itemStack).expand().fill();
                    slotContainer.add(countContainer);
                } else {
                    slotContainer.add(itemStack).expand().fill();
                }

                String tooltipText;
                if (obj.getNumber() < 2) tooltipText = obj.getObjectType().toString();
                else tooltipText = obj.getObjectType().toString() + " x" +  obj.getNumber();
                Label tooltipLabel = new Label(tooltipText, skin);
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBg);
                tooltip.getContainer().pad(8);
                slotContainer.addListener(tooltip);
                //stage.addActor(tooltip.getContainer());
            }

            slotContainer.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    GameObject clickedObject = slotIndex < slots.size() ? slots.get(slotIndex) : null;

                    if (event.getButton() == Input.Buttons.RIGHT) {
                        if (clickedObject != null) {
                            GameObjectType type = clickedObject.getObjectType();

                            if (!KitchenRecipe.isEdible(type)) {
                                UIRenderer.showTextBox("This item is not edible :(");
                                return true;
                            }

                            int count = player.howManyInInventory(type);
                            if (count == 0) {
                                UIRenderer.showTextBox("You don't currently have this food in your backpack.");
                                return true;
                            }

                            KitchenRecipe food = KitchenRecipe.getKitchenRecipe(type.toString());
                            if (food == null) {
                                UIRenderer.showTextBox("This item is not edible :(");
                                return true;
                            }

                            player.removeAmountFromInventory(type, 1);
                            player.increaseEnergy(food.getEnergy());

                            UIRenderer.showTextBox("Yum Yum, you just ate " + food.getType());
                            refreshInventoryTable();
                        }
                    } else {
                        // Left-click handling
                        if (player.getCurrentObject() != null && player.getCurrentObject().equals(clickedObject)) {
                            player.setCurrentObject(null);
                            selectedInventorySlot = -1; // Deselect if same object clicked
                            selectedInventoryItemForHotbar = null; // Clear hotbar assignment selection
                        } else {
//                            if (clickedObject != null && clickedObject instanceof Tool)
//                            {
//                                player.setCurrentTool(null);
//                            } else if (clickedObject != null)
//                            {
//                                player.setCurrentObject(clickedObject);
//                            }
                            selectedInventorySlot = clickedObject != null ? slotIndex : -1;
                            selectedInventoryItemForHotbar = clickedObject; // Set for hotbar assignment
                        }

                        // Clear hotbar selection
                        selectedHotbarSlot = -1;
                        refreshHotbar();
                        refreshInventoryTable();

                        // Show instruction for hotbar assignment
                        if (selectedInventoryItemForHotbar != null) {
                            UIRenderer.showTextBox("Press 1-8 to assign " +
                                selectedInventoryItemForHotbar.getObjectType().toString() + " to hotbar");
                        }
                    }

                    return true;
                }
            });

            inventoryGrid.add(slotContainer).size(SLOTS_SIZE, SLOTS_SIZE).pad(2);

            if ((slot + 1) % COLS == 0) {
                inventoryGrid.row();
            }
        }

        // Add inventory grid to main container
        mainTable.add(inventoryGrid).colspan(COLS).row();

        // Add trash button below the inventory grid
        Table trashContainer = new Table();
        trashContainer.add(trashButton).size(SLOTS_SIZE, SLOTS_SIZE).padTop(20);
        mainTable.add(trashContainer).colspan(COLS);

        return mainTable;
    }

    public GameObject getSelectedInventoryObject() {
        int index = selectedInventorySlot;
        if (index >= 0 && index < backpack.getSlots().size()) {
            return backpack.getSlots().get(index);
        }
        return null;
    }


    private int selectedToolSlot = -1;
    private int selectedInventorySlot = -1;


    private void refreshToolTable() {
        toolsTable.clear();
        toolsTable.defaults().size(SLOTS_SIZE).pad(4);
        toolsTable.center();
        updatePlayer();
        java.util.List<Tool> tools = backpack.getTools();

        for (int i = 0; i < COLS; i++) {
            Tool tool = i < tools.size() ? tools.get(i) : null;
            Table slotContainer = new Table();

            slotContainer.setBackground(i == selectedToolSlot ? slotHighlight : slotBackground);
            slotContainer.setSize(SLOTS_SIZE, SLOTS_SIZE);

            if (tool != null) {
                Drawable icon = getSafeIcon((GameObject) tool);
                Image iconImage = new Image(icon);
                slotContainer.add(iconImage).expand().fill();

                // Tooltip for the tool
                String tooltipText = tool.getObjectType().toString();
                Label tooltipLabel = new Label(tooltipText, skin);
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBg);
                tooltip.getContainer().pad(8);
                slotContainer.addListener(tooltip);
                //stage.addActor(tooltip.getContainer());
            }

            final int slotIndex = i;
            slotContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Tool clickedTool = slotIndex < tools.size() ? tools.get(slotIndex) : null;

//                    if (player.getCurrentTool() != null && player.getCurrentTool().equals(clickedTool)) {
//                        player.setCurrentTool(null);
//                        selectedToolSlot = -1; // Deselect if same tool clicked
//                    } else {
//                        player.setCurrentTool(clickedTool);
//                        selectedToolSlot = clickedTool != null ? slotIndex : -1;
//                    }

                    // Clear hotbar selection when selecting a tool
                    selectedHotbarSlot = -1;
                    refreshHotbar();

                    refreshToolTable();
                }
            });

            toolsTable.add(slotContainer)
                .size(SLOTS_SIZE, SLOTS_SIZE)
                .pad(4);

            if ((i + 1) % COLS == 0) {
                toolsTable.row();
            }
        }
    }

    public Drawable getSafeIcon(GameObject obj)
    {
        try
        {
            return getIconForGameObject(obj);
        } catch (Exception e) {
            return new Image(new Texture(Gdx.files.internal("game_objects/crops/Rice.png"))).getDrawable();
        }
    }

    private Drawable getIconForGameObject(GameObject obj)
    {
        // Replace with your actual icon loading logic
        String path = obj.getObjectType().getPath();
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }

    private Table buildSkillsTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        updatePlayer();

        for (Skill s : player.getSkills()) {
            Label skillLabel = new Label(s.getName(), skin);
            float max = s.getThresholdForLevel(s.getLevel());
            ProgressBar bar = new ProgressBar(0, max, 1, false, skin);
            bar.setValue((float) s.getUnit());

            if (player.getBuff() != null)
            {
                BuffType type = player.getBuff().getType();

                switch (type)
                {
                    case FORAGING_5:
                    case FORAGING_11:
                        if (s.getType() == SkillType.Foraging) bar.setValue(max);
                        break;

                    case FISHING_5:
                    case FISHING_10:
                        if (s.getType() == SkillType.Fishing) bar.setValue(max);
                        break;

                    case FARMING:
                        if (s.getType() == SkillType.Farming) bar.setValue(max);
                        break;

                    case MINING:
                        if (s.getType() == SkillType.Mining) bar.setValue(max);
                        break;
                }
            }

            // Tooltip content (can be customized)
            String tooltipText = s.getName() + " (Level " + s.getLevel() + ")\n" +
                "XP: " + s.getUnit();

            Label tooltipLabel = new Label(tooltipText, skin);
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);

            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(8);            // add some padding around the text

            // Add tooltip listener to label and progress bar
            skillLabel.addListener(tooltip);
            bar.addListener(tooltip);

            // Ensure tooltips work properly
            //stage.addActor(tooltip.getContainer());

            table.add(skillLabel);
            table.add(bar).width(120);
            table.row();
        }
        return table;
    }

    public void refreshSkillsTable()
    {
        skillsTable.clear();
        Table updatedTable = buildSkillsTable();
        skillsTable.add(updatedTable).expand().fill();
    }

    private Table buildSocialTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        for (int i = 1; i <= 10; i++) {
            table.add(new Label("Item #" + i, skin));
            table.add(new Label("x" + (int) (Math.random() * 50), skin));
            table.row();
        }
        return table;
    }

    // Helper method to get map display name
    private String getMapDisplayName(MapTypes mapType)
    {
        switch (mapType)
        {
            case MINING: return "Mining Farm";
            case FISHING: return "Fishing Farm";
            case FORAGING: return "Foraging Farm";
            case COMBAT: return "Combat Farm";
            case TOWN: return "Town Center";
            default: return mapType.getName();
        }
    }

    private Table buildMapTable()
    {
        Table mainTable = new Table(skin);
        mainTable.defaults().pad(10);
        return mainTable;
    }

    private void refreshMapTable() {
        mapTable.clear();

        // Get current season for map textures
        Season season = App.getCurrentGame().getCurrentTime().getSeason();
        ArrayList<Player> players = App.getCurrentGame().getPlayers();

        // Create tooltip style
        BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);
        Label.LabelStyle tooltipLabelStyle = new Label.LabelStyle(tooltipFont, Color.WHITE);

        // Left column (farms 0 and 2)
        Table leftColumn = new Table();
        leftColumn.defaults().padBottom(MAP_PADDING); // Add vertical padding between farms

        if (players.size() > 0) {
            MapContainer farm0 = createFarmMapContainer(players.get(0), season, tooltipLabelStyle);
            leftColumn.add(farm0).size(FARM_WIDTH, FARM_HEIGHT).row();
        }

        if (players.size() > 2) {
            MapContainer farm2 = createFarmMapContainer(players.get(2), season, tooltipLabelStyle);
            leftColumn.add(farm2).size(FARM_WIDTH, FARM_HEIGHT);
        }

        // Center column (city)
        MapContainer cityContainer = createCityMapContainer(season, tooltipLabelStyle);

        // Right column (farms 1 and 3)
        Table rightColumn = new Table();
        rightColumn.defaults().padBottom(MAP_PADDING); // Add vertical padding between farms

        if (players.size() > 1) {
            MapContainer farm1 = createFarmMapContainer(players.get(1), season, tooltipLabelStyle);
            rightColumn.add(farm1).size(FARM_WIDTH, FARM_HEIGHT).row();
        }

        if (players.size() > 3) {
            MapContainer farm3 = createFarmMapContainer(players.get(3), season, tooltipLabelStyle);
            rightColumn.add(farm3).size(FARM_WIDTH, FARM_HEIGHT);
        }

        // Add columns to outer grid
        Table outerGrid = new Table();
        outerGrid.defaults().pad(MAP_PADDING).center();
        outerGrid.add(leftColumn);
        outerGrid.add(cityContainer).size(CITY_WIDTH, CITY_HEIGHT).pad(MAP_PADDING);
        outerGrid.add(rightColumn);

        mapTable.add(outerGrid);
    }

    private MapContainer createFarmMapContainer(Player player, Season season, Label.LabelStyle tooltipStyle) {
        Farm farm = player.getFarm();
        MapContainer container = new MapContainer(farm, FARM_WIDTH, FARM_HEIGHT, season, tooltipStyle);
        return container;
    }

    private MapContainer createCityMapContainer(Season season, Label.LabelStyle tooltipStyle) {
        City city = App.getCurrentGame().getCity();
        MapContainer container = new MapContainer(city, CITY_WIDTH, CITY_HEIGHT, season, tooltipStyle);
        return container;
    }

    private Table buildSettingsTable()
    {
        Table table = new Table(skin);
        table.defaults().pad(20).width(350).height(120); // Bigger buttons

        TextButton exitButton = new TextButton("Exit Game", skin);
        exitButton.getLabel().setFontScale(1.2f); // Larger text
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if (WorldScreen.getInstance().ONLINE_MODE)
                {
                    WorldScreen.getInstance().saveAndExitOnline();
                } else
                {
                    WorldScreen.getInstance().saveAndExitOffline();
                }
            }
        });

        TextButton removeButton = new TextButton("Remove Player", skin);
        removeButton.getLabel().setFontScale(1.2f); // Larger text
        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Remove Player clicked");
            }
        });

        table.add(exitButton).row();
        table.add(removeButton);

        return table;
    }

    private void center(Stage stage) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        popup.setPosition((w - popup.getWidth()) / 2f, (h - popup.getHeight()) / 2f);
    }

    /**
     * Toggles the popup visibility.
     */
    public void toggleVisibility() {
        isVisible = !isVisible;
        popup.setVisible(isVisible);

        if (isVisible)
        {
            refreshInventoryTable();
            refreshHotbar();
            refreshMapTable();
            refreshSkillsTable();
        }
    }

    public void clearSelection() {
        selectedInventorySlot = -1;
        selectedHotbarSlot = -1;
        selectedInventoryItemForHotbar = null; // Clear hotbar assignment selection
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        popup.remove();
    }

    public TextButton getToolsTab() {
        return toolsTab;
    }

    public TextButton getMapTab() {
        return mapTab;
    }

    public TabType getLastTabOpenedByTabKey() {
        return lastTabOpenedByTabKey;
    }

    private static class MapContainer extends Group
    {
        private final Texture mapTexture;
        private final Map map;
        private final java.util.List<Player> players;
        private final int width;
        private final int height;
        private final Season season;
        private final TooltipManager tooltipManager;

        private final BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);
        private final Label.LabelStyle tooltipLabelStyle = new Label.LabelStyle(tooltipFont, Color.WHITE);

        public MapContainer(Map map, int width, int height, Season season, Label.LabelStyle tooltipStyle)
        {
            this.map = map;
            this.width = width;
            this.height = height;
            this.season = season;
            this.mapTexture = MapTypes.getMiniMapTexture(map.getMapType(), season);
            this.tooltipManager = TooltipManager.getInstance();

            // Add players based on map type
            if (map instanceof Farm)
            {
                this.players = new ArrayList<>();
                for (Player p : App.getCurrentGame().getPlayers())
                {
                    if ((p.isInFarm() || p.isInHome() || p.isInGreenHouse()) && p.getFarm() == map)
                    {
                        players.add(p);
                    }
                }
            } else
            {
                this.players = new ArrayList<>();
                for (Player p : App.getCurrentGame().getPlayers())
                {
                    if (p.isInCity() || p.isInShop())
                    {
                        players.add(p);
                    }
                }
            }

            setSize(width, height);
        }

        @Override
        public void draw(Batch batch, float parentAlpha)
        {
            // Draw map texture
            batch.draw(mapTexture, getX(), getY(), width, height);

            // Draw player avatars
            for (Player player : players)
            {
                drawPlayerAvatar(batch, player);
            }
        }

        private void drawPlayerAvatar(Batch batch, Player player)
        {
            // Get avatar texture
            Texture avatarTexture = player.getCharacter().getAvatar();

            // Calculate position
            Point playerLoc = getPlayerLocation(player);
            float xRatio = (float) playerLoc.getX() / map.getWidth();
            float yRatio = (float) playerLoc.getY() / map.getHeight();
            float flipYRatio = 1 - yRatio;

            float avatarX = getX() + xRatio * width - AVATAR_SIZE / 2f;
            float avatarY = getY() + flipYRatio * height - AVATAR_SIZE / 2f;

            // Draw downscaled avatar - REMOVED THE UNNECESSARY FLIP
            batch.draw(avatarTexture,
                avatarX, avatarY,
                AVATAR_SIZE, AVATAR_SIZE,
                0, 0,
                avatarTexture.getWidth(), avatarTexture.getHeight(),
                false, false); // Changed flipY from true to false
        }

        private Point getPlayerLocation(Player player)
        {
            if (player.isInHome())
            {
                return player.getFarm().getHomePoint();
            }

            if (player.isInGreenHouse())
            {
                return player.getFarm().getGreenhousePoint();
            }

            if (player.isInShop())
            {
                City city = App.getCurrentGame().getCity();
                for (java.util.Map.Entry<Point, Shop> entry : city.getShopDoors().entrySet())
                {
                    if (player.getCurrentMap() == entry.getValue())
                    {
                        return entry.getKey();
                    }
                }
            }

            return player.getLocation();
        }
    }

    public Window getPopup() {
        return popup;
    }
}
