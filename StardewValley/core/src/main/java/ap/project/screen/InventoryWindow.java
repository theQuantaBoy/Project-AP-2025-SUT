package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.game.Player;
import ap.project.model.player_data.Skill;
import ap.project.model.tools.Tool;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.tools.BackPack;
import ap.project.model.game.GameObject;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class InventoryWindow {
    private final Window popup;
    private final Stage stage;
    private final Table inventoryTable;
    private final ScrollPane inventoryScrollPane;
    private final Table skillsTable;
    private final Table socialTable;
    private final Table mapTable;
    private final Table toolsTable;
    private final Stack contentStack;
    private BackPack backpack;
    private final Skin skin;
    private boolean isVisible = false;
    private static final int COLS = 8;
    private static final int SLOTS_SIZE = 64;
    private Drawable slotBackground;
    private Drawable slotHighlight;
    private Player player;
    private TooltipManager tooltipManager = TooltipManager.getInstance();
    private Drawable tooltipBg;
    private TextButton toolsTab;
    private TextButton mapTab;
    public enum TabType { INVENTORY, SKILL, SOCIAL, MAP, TOOLS }

    private TabType lastTabOpenedByTabKey = TabType.INVENTORY;
    private ImageButton trashButton;

    public InventoryWindow(Stage stage) {
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        this.stage = stage;
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
                    player.removeAmountFromInventory(selected.getObjectType(), 1);
                    // reset selection
                    if (backpack.getSlots().get(selectedInventorySlot) == null) selectedInventorySlot = -1;
                    // rebuild UI
                    refreshInventoryTable();
                }
            }
        });

        TextButton invTab = new TextButton("Inventory", skin);
        TextButton skillsTab = new TextButton("Skills", skin);
        TextButton socialTab = new TextButton("Social", skin);
        mapTab = new TextButton("Map", skin);
        toolsTab = new TextButton("Tools", skin);

        this.slotBackground = GameAssetsManager.getGameAssetsManager().createColoredDrawable(SLOTS_SIZE, SLOTS_SIZE, new Color(0.3f, 0.3f, 0.3f, 0.7f));
        this.slotHighlight = GameAssetsManager.getGameAssetsManager().createColoredDrawable(SLOTS_SIZE, SLOTS_SIZE, new Color(0.5f, 0.5f, 0.5f, 0.9f));

        tooltipManager.initialTime = 0.5f;
        tooltipManager.subsequentTime = 0.1f;
        tooltipBg = GameAssetsManager.getGameAssetsManager().createColoredDrawable(1, 1, new Color(0f, 0f, 0f, 0.7f));

        inventoryTable = new Table(skin);
        inventoryScrollPane = new ScrollPane(inventoryTable, skin);
        inventoryScrollPane.setFadeScrollBars(false);
        inventoryScrollPane.setScrollingDisabled(true, false); // ✅ Only vertical scrolling

        skillsTable = buildSkillsTable();
        socialTable = buildSocialTable();
        mapTable = buildMapTable();
        toolsTable = new Table(skin);

        contentStack = new Stack(inventoryScrollPane, skillsTable, socialTable, mapTable, toolsTable);
        skillsTable.setVisible(false);
        socialTable.setVisible(false);
        mapTable.setVisible(false);
        toolsTable.setVisible(false);

        invTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshInventoryTable();
                inventoryScrollPane.setVisible(true);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(false);
                toolsTable.setVisible(false);
                popup.pack();
                center(stage);
                lastTabOpenedByTabKey = TabType.INVENTORY;
            }
        });

        skillsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inventoryScrollPane.setVisible(false);
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
                inventoryScrollPane.setVisible(false);
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
                inventoryScrollPane.setVisible(false);
                skillsTable.setVisible(false);
                socialTable.setVisible(false);
                mapTable.setVisible(true);
                toolsTable.setVisible(false);
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
                refreshToolTable();
                popup.pack();
                center(stage);
                lastTabOpenedByTabKey = TabType.TOOLS;
            }
        });

        popup.row();
        popup.add(invTab).expandX().fillX();
        popup.add(skillsTab).expandX().fillX();
        popup.add(socialTab).expandX().fillX();
        popup.add(mapTab).expandX().fillX();
        popup.add(toolsTab).expandX().fillX();
        popup.add(trashButton).width(32).height(32).padLeft(10);
        popup.row();
        popup.add(contentStack).colspan(4).expand().fill(); // Important: use .fill
        popup.row();
        popup.pack();
        center(stage);
        stage.addActor(popup);
    }

    private int selectedInventorySlot = -1;
    private int selectedToolSlot = -1;

    private void refreshInventoryTable() {
        inventoryTable.clear();
        Table content;
        /*if (backpack.getCapacity() == -1)*/ content = buildLimitedInventoryTable();
//        else content = buildLimitedInventoryTable();

        inventoryTable.add(content).expand().center();
    }

    public GameObject getSelectedInventoryObject() {
        int index = selectedInventorySlot;
        if (index >= 0 && index < backpack.getSlots().size()) {
            return backpack.getSlots().get(index);
        }
        return null;
    }

//    public Table buildInventoryTable() {
//        Table table = new Table(skin);
//        table.defaults().size(SLOTS_SIZE).pad(2);
//        table.top().left();
//        updatePlayer();
//
//        // Loop only existing items
//        for (int i = 0; i < backpack.getSlots().size(); i++) {
//            GameObject obj = backpack.getSlots().get(i);
//            table.add(createSlotCell(i, obj))
//                .size(SLOTS_SIZE, SLOTS_SIZE).pad(2);
//
//            if ((i + 1) % COLS == 0) {
//                table.row();
//            }
//        }
//        return table;
//    }
//
//    /**
//     * Creates a single slot cell at index i, highlighting if selected.
//     */
//    private Table createSlotCell(int index, GameObject obj) {
//        Table slot = new Table(skin);
//        slot.setBackground(index == selectedInventorySlot ? slotHighlight : slotBackground);
//        slot.setSize(SLOTS_SIZE, SLOTS_SIZE);
//
//        if (obj != null) {
//            Drawable icon = getIconForGameObject(obj);
//            Stack stack = new Stack(new Image(icon));
//            if (obj.getNumber() > 1) {
//                stack.add(new Label(String.valueOf(obj.getNumber()), skin));
//            }
//            slot.add(stack).expand().fill();
//
//            Tooltip<Label> tip = new Tooltip<>(
//                new Label(obj.getObjectType().toString(), skin),
//                tooltipManager
//            );
//            tip.getContainer().setBackground(tooltipBg);
//            slot.addListener(tip);
//            stage.addActor(tip.getContainer());
//        }
//
//        slot.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                selectedInventorySlot = (obj != null) ? index : -1;
//                refreshInventoryTable();  // re-build & re-display
//            }
//        });
//
//        return slot;
//    }


    public Table buildLimitedInventoryTable() {
        Table table = new Table(skin);
        table.defaults().size(SLOTS_SIZE).pad(2);
        table.center();
        updatePlayer();
        java.util.List<GameObject> slots = backpack.getSlots();
        int capacity = backpack.getCapacity();

        for (int slot = 0; slot < capacity; slot++) {
            final int slotIndex = slot;
            GameObject obj = slots.get(slot);
            Table slotContainer = new Table();
            slotContainer.setBackground(slotIndex == selectedInventorySlot ? slotHighlight : slotBackground);
            slotContainer.setSize(SLOTS_SIZE, SLOTS_SIZE);

            if (obj != null) {
                Drawable icon = getSafeIcon(obj);
                Stack itemStack = new Stack();
                itemStack.add(new Image(icon));

                if (obj.getNumber() > 1) {
                    Label countLabel = new Label(String.valueOf(obj.getNumber()), skin);
                    itemStack.add(countLabel);
                }

                slotContainer.add(itemStack).expand().fill();

                Label tooltipLabel = new Label(obj.getObjectType().toString(), skin);
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBg);
                tooltip.getContainer().pad(8);
                slotContainer.addListener(tooltip);
                stage.addActor(tooltip.getContainer());
            }

            slotContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameObject clickedObject = slotIndex < slots.size() ? slots.get(slotIndex) : null;
                    selectedInventorySlot = clickedObject != null ? slotIndex : -1;
                    refreshInventoryTable();
                    if (clickedObject != null)
                        System.out.println("Selected Inventory Item: " + clickedObject.getObjectType());
                }
            });

            table.add(slotContainer).size(SLOTS_SIZE, SLOTS_SIZE).pad(2);

            if ((slot + 1) % COLS == 0)
                table.row();
        }
        return table;
    }

    private Drawable getSafeIcon(GameObject obj) {
        try {
            return getIconForGameObject(obj);
        } catch (Exception e) {
            return new Image(new Texture(Gdx.files.internal("game_objects/crops/Rice.png"))).getDrawable();
        }
    }

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
                slotContainer.add(new Image(icon)).expand().fill();

                Label tooltipLabel = new Label(tool.getObjectType().toString(), skin);
                Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
                tooltip.getContainer().setBackground(tooltipBg);
                tooltip.getContainer().pad(8);
                slotContainer.addListener(tooltip);
                stage.addActor(tooltip.getContainer());
            }

            final int slotIndex = i;
            slotContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Tool clickedTool = slotIndex < tools.size() ? tools.get(slotIndex) : null;
                    player.setCurrentTool(clickedTool);
                    selectedToolSlot = clickedTool != null ? slotIndex : -1;
                    refreshToolTable();
                    if (player.getCurrentTool() != null)
                        System.out.println(player.getCurrentTool().getToolType().getName());
                }
            });

            toolsTable.add(slotContainer).size(SLOTS_SIZE, SLOTS_SIZE).pad(4);

            if ((i + 1) % COLS == 0)
                toolsTable.row();
        }
    }

    private Drawable getIconForGameObject(GameObject obj) {
        String path = obj.getObjectType().getPath();
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }

    private Table buildSkillsTable() {
        Table table = new Table(skin);
        table.defaults().pad(4);
        updatePlayer();
        for (Skill s : player.getSkills()) {
            Label skillLabel = new Label(s.getName(), skin);
            ProgressBar bar = new ProgressBar(0, s.getThresholdForLevel(s.getLevel()), 1, false, skin);
            bar.setValue((float) s.getUnit());

            String tooltipText = s.getName() + " (Level " + s.getLevel() + ")\nXP: " +
                s.getUnit() + "/" + s.getThresholdForLevel(s.getLevel());

            Label tooltipLabel = new Label(tooltipText, skin);
            Tooltip<Label> tooltip = new Tooltip<>(tooltipLabel, tooltipManager);
            tooltip.getContainer().setBackground(tooltipBg);
            tooltip.getContainer().pad(8);

            skillLabel.addListener(tooltip);
            bar.addListener(tooltip);
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

    public TextButton getToolsTab() {
        return toolsTab;
    }

    public TextButton getMapTab() {
        return mapTab;
    }

    public TabType getLastTabOpenedByTabKey() {
        return lastTabOpenedByTabKey;
    }

    private void updatePlayer() {
        player = App.getCurrentGame().getCurrentPlayer();
        backpack = player.getCurrentBackPack();
    }
    public void clearSelection() {
        selectedInventorySlot = -1;
    }

}
