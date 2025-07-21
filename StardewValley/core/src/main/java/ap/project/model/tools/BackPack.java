package ap.project.model.tools;

import ap.project.model.game.GameObject;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.tool_enums.BackPackLevel;
import ap.project.model.enums.tool_enums.ToolType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BackPack extends Tool {
    private BackPackLevel level;
    private List<GameObject> slots; // Represents all slots (filled and empty)
    private List<Tool> tools = new LinkedList<>();
    private int itemCount; // Count of actual items

    public BackPack() {
        super.ObjectType = GameObjectType.BackPack;
        super.toolType = ToolType.BackPack;
        super.name = toolType.getName();
        this.level = BackPackLevel.base;
        initializeSlots();
    }

    public BackPack(BackPackLevel level) {
        super.toolType = ToolType.BackPack;
        super.name = toolType.getName();
        this.level = level;
        initializeSlots();
    }

    private void initializeSlots() {
        int capacity = level.getCapacity();
        this.slots = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            slots.add(null); // Initialize all slots as empty
        }
        this.itemCount = 0;
    }

    public BackPackLevel getLevel() {
        return level;
    }

    public void setLevel(BackPackLevel level) {
        this.level = level;
        initializeSlots(); // Reinitialize slots when level changes
    }

    public List<GameObject> getSlots() {
        return slots;
    }

    public int getCapacity() {
        return level.getCapacity();
    }

    public int getItemCount() {
        return itemCount;
    }

    public boolean addItem(GameObject item) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == null) {
                slots.set(i, item);
                itemCount++;
                return true;
            }
        }
        return false; // No space available
    }

    public GameObject removeItem(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slots.size()) {
            GameObject item = slots.get(slotIndex);
            slots.set(slotIndex, null);
            if (item != null) itemCount--;
            return item;
        }
        return null;
    }

    public boolean removeItem(GameObject item) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == item) {
                slots.set(i, null);
                itemCount--;
                return true;
            }
        }
        return false;
    }

    public List<GameObject> getNonEmptyItems() {
        List<GameObject> items = new ArrayList<>();
        for (GameObject obj : slots) {
            if (obj != null) {
                items.add(obj);
            }
        }
        return items;
    }

    public boolean hasEmptySlot() {
        return itemCount < slots.size();
    }

    public int getEmptySlotCount() {
        return slots.size() - itemCount;
    }

    public List<Tool> getTools() {
        return tools;
    }
}
