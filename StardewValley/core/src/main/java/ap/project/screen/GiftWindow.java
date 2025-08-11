package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.enums.GameAnimationType;
import ap.project.model.enums.GameObjectType;
import ap.project.model.game.GameObject;
import ap.project.model.game.NPC;
import ap.project.model.game.NPCCharacter;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipWithNpcData;
import ap.project.model.tools.Tool;
import ap.project.visual.MapVisual;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.List;

public class GiftWindow
{
    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private boolean isVisible = false;
    private NPC currentNpc;

    // Grid components
    private Table giftSlotGrid;
    private Table inventoryGrid;
    private static final int GIFT_SLOT_SIZE = 48;
    private static final int INVENTORY_ROWS = 3;
    private static final int INVENTORY_COLS = 12;
    private static final int GRID_SPACING = 8;
    private Drawable selectionBorderDrawable;
    private Drawable slotBackground;

    // Selection tracking
    private GameObject selectedItem;
    private boolean isGiftSource;
    private int selectedIndex = -1;

    // UI components
    private TextButton giveButton;
    private TextButton closeButton;
    private TextButton favoritesButton;

    // Tooltip management
    private final TooltipManager tooltipManager = TooltipManager.getInstance();
    private Drawable tooltipBackground;

    // Gift item
    private GameObject giftItem;

    public GiftWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        window = new Window("Gift", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(10);

        // Create selection border and slot background
        createSelectionBorder();
        slotBackground = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(GIFT_SLOT_SIZE, GIFT_SLOT_SIZE, new Color(0.3f, 0.3f, 0.3f, 0.7f));

        // Create tooltip background
        tooltipBackground = GameAssetsManager.getGameAssetsManager()
            .createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.8f));

        stage.addActor(window);
    }

    private void createSelectionBorder()
    {
        Pixmap borderPixmap = new Pixmap(GIFT_SLOT_SIZE, GIFT_SLOT_SIZE, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(new Color(101/255f, 67/255f, 33/255f, 1f));
        borderPixmap.fillRectangle(0, 0, GIFT_SLOT_SIZE, 4); // Top border
        borderPixmap.fillRectangle(0, GIFT_SLOT_SIZE - 4, GIFT_SLOT_SIZE, 4); // Bottom border
        borderPixmap.fillRectangle(0, 0, 4, GIFT_SLOT_SIZE); // Left border
        borderPixmap.fillRectangle(GIFT_SLOT_SIZE - 4, 0, 4, GIFT_SLOT_SIZE); // Right border
        selectionBorderDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(borderPixmap)));
        borderPixmap.dispose();
    }

    private void showFavoritesDialog()
    {
        Dialog favoritesDialog = new Dialog("Available Recipes", skin);
        favoritesDialog.setModal(true);
        favoritesDialog.setMovable(true);
        favoritesDialog.setResizable(true);

        String favorites = currentNpc.getNpcDetails().getFavoritesText();
        ScrollPane scrollPane = new ScrollPane(new Label(favorites, skin));
        scrollPane.setFadeScrollBars(false);
        favoritesDialog.getContentTable().add(scrollPane).width(500).height(350).pad(20);

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                favoritesDialog.hide();
            }
        });
        favoritesDialog.getButtonTable().add(closeButton);

        favoritesDialog.show(stage);
        favoritesDialog.setSize(800, 550);
        favoritesDialog.setPosition(
            (stage.getWidth() - favoritesDialog.getWidth()) / 2,
            (stage.getHeight() - favoritesDialog.getHeight()) / 2
        );
    }

    public void setNpc(NPC npc)
    {
        this.currentNpc = npc;
        giftItem = null;
        refresh();
    }

    public void toggleVisibility()
    {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible)
        {
            refresh();
            center();
        } else
        {
            selectedItem = null;
            selectedIndex = -1;
        }
    }

    private void refresh()
    {
        window.clear();
        if (currentNpc == null) return;

        Table mainLayout = new Table();
        mainLayout.defaults().pad(5);

        // Create a centered container for the top section
        Table topSection = new Table();
        topSection.defaults().pad(5);

        // Gift slot header - centered
        topSection.add(new Label("Gift Slot", skin)).center().padBottom(10).row();

        // Gift slot grid (single slot) - centered
        giftSlotGrid = new Table();
        buildGiftSlotGrid();
        topSection.add(giftSlotGrid).center().row();

        // Favorites button - centered
        favoritesButton = new TextButton("Show Favorites", skin);
        favoritesButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showFavoritesDialog();
            }
        });
        topSection.add(favoritesButton).padTop(10).padBottom(20).center().row();

        // Add top section to main layout
        mainLayout.add(topSection).center().padBottom(20).row();

        // Separator
        mainLayout.add(createSeparator()).fillX().padBottom(20).row();

        // Inventory grid
        mainLayout.add(new Label("Inventory", skin)).colspan(INVENTORY_COLS).padBottom(10).row();
        inventoryGrid = new Table();
        Player player = App.getCurrentGame().getCurrentPlayer();
        buildInventoryGrid(inventoryGrid, player.getInventory());
        mainLayout.add(inventoryGrid).colspan(INVENTORY_COLS).padBottom(20).row();

        // Buttons
        Table buttonTable = new Table();
        buttonTable.defaults().minWidth(120).pad(10);

        giveButton = new TextButton("Give Gift", skin);
        giveButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                giveGift();
            }
        });

        closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toggleVisibility();
            }
        });

        buttonTable.add(giveButton).padRight(20);
        buttonTable.add(closeButton);
        mainLayout.add(buttonTable).padTop(10);

        window.add(mainLayout);
        window.pack();
    }

    private void buildGiftSlotGrid()
    {
        giftSlotGrid.defaults().size(GIFT_SLOT_SIZE).pad(GRID_SPACING);
        giftSlotGrid.clear();

        BitmapFont quantityFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 16, Color.WHITE);
        BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);

        Stack slotStack = new Stack();
        slotStack.setSize(GIFT_SLOT_SIZE, GIFT_SLOT_SIZE);

        Image bgImage = new Image(slotBackground);
        slotStack.add(bgImage);

        slotStack.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                handleGiftSlotClick();
            }
        });

        if (giftItem != null)
        {
            Texture texture = giftItem.getObjectType().getTexture();
            Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
            image.setSize(GIFT_SLOT_SIZE, GIFT_SLOT_SIZE);
            slotStack.addActor(image);

            if (giftItem.getNumber() > 1)
            {
                Table quantityTable = new Table();
                quantityTable.setFillParent(true);
                quantityTable.bottom().right();

                Label quantityLabel = new Label(String.valueOf(giftItem.getNumber()),
                    new Label.LabelStyle(quantityFont, Color.WHITE));
                quantityTable.add(quantityLabel).pad(2);

                slotStack.add(quantityTable);
            }

            if (giftItem == selectedItem && isGiftSource)
            {
                Image borderImage = new Image(selectionBorderDrawable);
                slotStack.addActor(borderImage);
            }

            // Add tooltip
            Label tooltipLabel = new Label(giftItem.getObjectType().toString(),
                new Label.LabelStyle(tooltipFont, Color.WHITE));
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().setBackground(tooltipBackground);
            tooltip.getContainer().pad(5);
            slotStack.addListener(tooltip);
        }

        giftSlotGrid.add(slotStack);
    }

    private void buildInventoryGrid(Table grid, List<GameObject> items)
    {
        grid.defaults().size(GIFT_SLOT_SIZE).pad(GRID_SPACING);
        grid.clear();

        BitmapFont quantityFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 16, Color.WHITE);
        BitmapFont tooltipFont = GameAssetsManager.generateFont("fonts/Roboto-Regular.ttf", 20, Color.WHITE);

        int slots = INVENTORY_ROWS * INVENTORY_COLS;

        for (int i = 0; i < slots; i++)
        {
            final int index = i;
            Stack slotStack = new Stack();
            slotStack.setSize(GIFT_SLOT_SIZE, GIFT_SLOT_SIZE);

            Image bgImage = new Image(slotBackground);
            slotStack.add(bgImage);

            slotStack.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    handleInventorySlotClick(index, items);
                }
            });

            if (i < items.size() && items.get(i) != null)
            {
                GameObject item = items.get(i);
                Texture texture = item.getObjectType().getTexture();
                Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
                image.setSize(GIFT_SLOT_SIZE, GIFT_SLOT_SIZE);
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

                if (item == selectedItem && !isGiftSource)
                {
                    Image borderImage = new Image(selectionBorderDrawable);
                    slotStack.addActor(borderImage);
                }

                // Add tooltip
                Label tooltipLabel = new Label(item.getObjectType().toString(),
                    new Label.LabelStyle(tooltipFont, Color.WHITE));
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBackground);
                tooltip.getContainer().pad(5);
                slotStack.addListener(tooltip);
            }

            grid.add(slotStack);
            if ((i + 1) % INVENTORY_COLS == 0) grid.row();
        }
    }

    private void handleGiftSlotClick()
    {
        if (giftItem != null)
        {
            // Select the gift item
            selectedItem = giftItem;
            isGiftSource = true;
            refresh();
        } else if (selectedItem != null && !isGiftSource)
        {
            // Transfer from inventory to gift slot
            giftItem = new GameObject(selectedItem.getObjectType(), 1);
            Player player = App.getCurrentGame().getCurrentPlayer();
            player.removeAmountFromInventory(selectedItem.getObjectType(), 1);
            selectedItem = null;
            refresh();
        }
    }

    private void handleInventorySlotClick(int index, List<GameObject> items)
    {
        GameObject clickedItem = (index < items.size()) ? items.get(index) : null;

        if (clickedItem != null)
        {
            selectedItem = clickedItem;
            selectedIndex = index;
            isGiftSource = false;
            refresh();
        } else if (selectedItem != null && isGiftSource)
        {
            // Transfer from gift slot to inventory
            Player player = App.getCurrentGame().getCurrentPlayer();
            if (player.getCurrentBackPack().hasEmptySlot())
            {
                player.addToInventory(selectedItem);
                giftItem = null;
                selectedItem = null;
                refresh();
            } else
            {
                UIRenderer.showTextBox("Inventory full!");
            }
        }
    }

    private void giveGift()
    {
        if (giftItem == null)
        {
            UIRenderer.showTextBox("Please select a gift first!");
            return;
        }

        // Check if NPC likes the gift
        boolean isFavorite = currentNpc.isFavorite(giftItem.getObjectType());
        String message = isFavorite ?
            currentNpc.getName() + " loved your gift!" :
            currentNpc.getName() + " accepted your gift.";

        Player player = App.getCurrentGame().getCurrentPlayer();

        FriendshipWithNpcData friendship = player.getNpcFriendship(currentNpc);

        if (isTool(giftItem.getObjectType()))
        {
            UIRenderer.showTextBox("You can't gift a tool!");
            return;
        }

        player.removeAmountFromInventory(giftItem.getObjectType(), 1);

        if (!friendship.isHasGifted())
        {
            if (isFavorite)
            {
                UIRenderer.showTextBox("You received 200 xp");
            } else
            {
                UIRenderer.showTextBox("You received 50 xp");
            }
        }

        friendship.gift(isFavorite);

        // Clear gift slot
        giftItem = null;
        selectedItem = null;

        UIRenderer.showTextBox(message);
        refresh();

        toggleVisibility();

        NPCCharacter character = currentNpc.getCharacter();
        Vector2 loc = new Vector2(character.getPosition().x - 9, character.getPosition().y - 4);
        MapVisual.playAnimationAt(GameAnimationType.GIFT, loc);
    }

    private boolean isTool(GameObjectType objectType)
    {
        if ((objectType == GameObjectType.HOE) || (objectType == GameObjectType.PICKAXE) ||
            (objectType == GameObjectType.AXE) || (objectType == GameObjectType.WATERING_CAN) ||
            (objectType == GameObjectType.FISHING_POLE) || (objectType == GameObjectType.SCYTHE) ||
            (objectType == GameObjectType.MILK_PAIL) || (objectType == GameObjectType.SHEAR) ||
            (objectType == GameObjectType.BackPack) || (objectType == GameObjectType.TRASH_CAN))
        {
            return true;
        }

        return false;
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

    private void center()
    {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public Window getWindow()
    {
        return window;
    }
}
