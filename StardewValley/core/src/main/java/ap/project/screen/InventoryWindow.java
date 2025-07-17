package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.game.Player;
import ap.project.model.player_data.Skill;
import ap.project.model.tools.Tool;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.tools.BackPack;
import ap.project.model.game.GameObject;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * A popup inventory window with tabbed panels that automatically shows
 * the player's current backpack contents in a grid.
 */
public class InventoryWindow {
    private final Window popup;
    private final Stage stage;
    private final Table inventoryTable;
    private final Table skillsTable;
    private final Table socialTable;
    private final Table mapTable;
    private final Table toolsTable;
    private final Stack contentStack;
    private final BackPack backpack;
    private final Skin skin;
    private boolean isVisible = false;
    private static final int COLS = 8;
    private Drawable slotBackground; // For inventory slot backgrounds
    private Drawable slotHighlight;
    private Player player;

    /**
     * @param stage    the Stage to which this window will be added
     */
    public InventoryWindow(Stage stage) {
        this.player = App.getCurrentGame().getCurrentPlayer();
        this.backpack = player.getCurrentBackPack();
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        this.stage = stage;
        // Create popup Window
        popup = new Window("Menu", skin);
        popup.setVisible(false);
        popup.setMovable(true);
        popup.defaults().pad(5);

        // Create tab buttons
        TextButton invTab = new TextButton("Inventory", skin);
        TextButton skillsTab = new TextButton("Skills", skin);
        TextButton socialTab = new TextButton("Social", skin);
        TextButton mapTab = new TextButton("Map", skin);
        TextButton toolsTab = new TextButton("Tools", skin);
        this.slotBackground = createColoredDrawable(32, 32, new Color(0.3f, 0.3f, 0.3f, 0.7f));
        this.slotHighlight = createColoredDrawable(32, 32, new Color(0.5f, 0.5f, 0.5f, 0.9f));

        // Create content tables
        inventoryTable = new Table(skin);
        skillsTable    = buildSkillsTable();
        socialTable    = buildSocialTable();
        mapTable       = buildMapTable();
        toolsTable     = new Table(skin);

        // Stack panels and hide non-inventory
        contentStack = new Stack(inventoryTable, skillsTable, socialTable, mapTable, toolsTable);
        skillsTable.setVisible(false);
        socialTable.setVisible(false);
        mapTable.setVisible(false);
        toolsTable.setVisible(false);

        // Tab switching with inventory refresh
        invTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshInventoryTable();
                inventoryTable.setVisible(true);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        skillsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(true);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        socialTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(true);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        mapTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(true);
                toolsTable.setVisible(false);
                popup.pack();
                center(stage);
            }
        });
        toolsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryTable.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(true);
                refreshToolTable();
                popup.pack();
                center(stage);
            }
        });

        // Assemble popup layout
        popup.row();
        popup.add(invTab).expandX().fillX();
        popup.add(skillsTab).expandX().fillX();
        popup.add(socialTab).expandX().fillX();
        popup.add(mapTab).expandX().fillX();
        popup.add(toolsTab).expandX().fillX();
        popup.row();
        popup.add(contentStack).colspan(4).expand().fill();
        popup.pack();
        center(stage);
        stage.addActor(popup);
    }

    private Drawable createColoredDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    /**
     * Refreshes the inventory grid to match the backpack contents.
     */
    private void refreshInventoryTable() {
        inventoryTable.clear();
        inventoryTable.defaults().size(32).pad(2);

        // Get all slots from backpack
        java.util.List<GameObject> slots = backpack.getSlots();
        int capacity = backpack.getCapacity();

        for (int slot = 0; slot < capacity; slot++) {
            GameObject obj = slots.get(slot);

            // Create slot container with background
            Table slotContainer = new Table();
            slotContainer.setBackground(slotBackground);
            slotContainer.setSize(32, 32);

            if (obj != null) {
                // Get item icon
                Drawable icon = null;
                try {
                    icon = getIconForGameObject(obj);
                } catch (Exception e) {
                    icon = new Image(new Texture(Gdx.files.internal("game_objects/crops/Rice.png"))).getDrawable();
                }

                // Create stack for item and count
                Stack itemStack = new Stack();
                itemStack.add(new Image(icon));

                // Add count label if more than 1
                if (obj.getNumber() > 1) {
                    Label countLabel = new Label(String.valueOf(obj.getNumber()), skin);
                    itemStack.add(countLabel);
                }

                slotContainer.add(itemStack).expand().fill();
            }

            inventoryTable.add(slotContainer).size(32, 32).pad(2);

            // Start new row after every COLS items
            if ((slot + 1) % COLS == 0) {
                inventoryTable.row();
            }
        }
    }

    private void refreshToolTable() {
        toolsTable.clear();
        toolsTable.defaults().size(32).pad(2);

        // Get all slots from backpack
        java.util.List<Tool> slots = backpack.getTools();
        int capacity = slots.size();

        for (int slot = 0; slot < capacity; slot++) {
            GameObject obj = slots.get(slot);

            // Create slot container with background
            Table slotContainer = new Table();
            slotContainer.setBackground(slotBackground);
            slotContainer.setSize(32, 32);

            if (obj != null) {
                // Get item icon
                Drawable icon = null;
                try {
                    icon = getIconForGameObject(obj);
                } catch (Exception e) {
                    icon = new Image(new Texture(Gdx.files.internal("game_objects/crops/Rice.png"))).getDrawable();
                }

                // Create stack for item and count
                Stack itemStack = new Stack();
                itemStack.add(new Image(icon));

                // Add count label if more than 1
                if (obj.getNumber() > 1) {
                    Label countLabel = new Label(String.valueOf(obj.getNumber()), skin);
                    itemStack.add(countLabel);
                }

                slotContainer.add(itemStack).expand().fill();
            }

            toolsTable.add(slotContainer).size(32, 32).pad(2);
        }
    }

    /**
     * Gets the icon for a game object
     */
    private Drawable getIconForGameObject(GameObject obj) {
        // Replace with your actual icon loading logic
        String path = obj.getObjectType().getPath();
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }

    private Table buildSkillsTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        TooltipManager tooltipManager = TooltipManager.getInstance();
        tooltipManager.initialTime = 0.5f; // Delay before tooltip shows
        tooltipManager.subsequentTime = 0.1f;
        Drawable tooltipBg = createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        for (Skill s : player.getSkills()) {
            Label skillLabel = new Label(s.getName(), skin);
            ProgressBar bar = new ProgressBar(0, s.getThresholdForLevel(s.getLevel()), 1, false, skin);
            bar.setValue((float) s.getUnit());

            // Tooltip content (can be customized)
            String tooltipText = s.getName() + " (Level " + s.getLevel() + ")\n" +
                "XP: " + s.getUnit() + "/" + s.getThresholdForLevel(s.getLevel());

            Label tooltipLabel = new Label(tooltipText, skin);
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);

            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(8);            // add some padding around the text

            // Add tooltip listener to label and progress bar
            skillLabel.addListener(tooltip);
            bar.addListener(tooltip);

            // Ensure tooltips work properly
            stage.addActor(tooltip.getContainer());

            table.add(skillLabel);
            table.add(bar).width(120);
            table.row();
        }
        return table;
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

    private Table buildMapTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        for (int i = 1; i <= 10; i++) {
            table.add(new Label("Map #" + i, skin));
            table.add(new Label("x" + (int) (Math.random() * 50), skin));
            table.row();
        }
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
        if (isVisible) refreshInventoryTable();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        popup.remove();
    }
}
