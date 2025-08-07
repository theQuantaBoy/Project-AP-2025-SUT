package ap.project.model.tools;

import ap.project.model.game.GameObject;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.tool_enums.BackPackLevel;
import ap.project.model.enums.tool_enums.ToolType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BackPack extends Tool {
    private BackPackLevel level;
    private List<GameObject> slots; // Represents all slots (filled and empty)
    private List<Tool> tools = new LinkedList<>();
    private ArrayList<GameObject> hotbarSlots = new ArrayList<>(Collections.nCopies(8, null));
    private int itemCount; // Count of actual items

    public BackPack() {
        super.ObjectType = GameObjectType.BackPack;
        super.toolType = ToolType.BackPack;
        super.name = toolType.getName();
        this.level = BackPackLevel.base;
        initializeSlots();
    }

    public BackPack(BackPackLevel level) {
        super.ObjectType = GameObjectType.BackPack;
        super.toolType = ToolType.BackPack;
        super.name = toolType.getName();
        this.level = level;
        initializeSlots();
    }

    private void initializeSlots() {
        int capacity = level.getCapacity();
        if (capacity >= 0) {
            // fixed-size: placeholder nulls
            slots = new ArrayList<>(capacity);
            for (int i = 0; i < capacity; i++) {
                slots.add(null);
            }
        } else {
            // unlimited: start empty, will grow
            slots = new ArrayList<>();
        }
        itemCount = 0;
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
        if (level.getCapacity() < 0) {
            // unlimited: add to end
            slots.add(item);
            itemCount++;
            return true;
        } else {
            // fixed: fill first null
            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i) == null) {
                    slots.set(i, item);
                    itemCount++;
                    return true;
                }
            }
            return false; // full
        }
    }

    public boolean removeItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.size()) return false;
        if (slots.get(slotIndex) == null) return false;
        if (level.getCapacity() < 0) {
            // unlimited: remove and shift
            slots.remove(slotIndex);
        } else {
            // fixed: just clear
            slots.remove(slotIndex);
        }
        itemCount--;
        return true;
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

    public ArrayList<GameObject> getHotbarSlots() {
        return hotbarSlots;
    }

    public boolean addToHotbar(GameObject item, int slot) {
        if (slot >= 0 && slot < hotbarSlots.size()) {
            hotbarSlots.set(slot, item);
            return true;
        }
        return false;
    }

    public void removeFromHotbar(int slot) {
        if (slot >= 0 && slot < hotbarSlots.size()) {
            hotbarSlots.set(slot, null);
        }
    }

    public void setItemCount(int itemCount)
    {
        this.itemCount = itemCount;
    }
}
