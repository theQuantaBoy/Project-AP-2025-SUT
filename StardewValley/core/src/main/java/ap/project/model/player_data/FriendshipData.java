package ap.project.model.player_data;

import ap.project.model.game.Player;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.UpdateFriendshipMessage;

import java.util.ArrayList;

public class FriendshipData {
    private int level;
    private int xp;
    private boolean newLevel = false;
    private boolean isIntrcatedToday;
    private boolean bouquetBought = false;
    private boolean isMarried = false;
    private ArrayList<String> messageHistory = new ArrayList<>();

    public FriendshipData() {
    }

    public FriendshipData(int level, int xp, boolean isIntrcatedToday) {
        this.level = level;
        this.xp = xp;
        this.isIntrcatedToday = isIntrcatedToday;
    }

    public int getLevel() {
        return level;
    }

    public void addLevel() {
        this.level ++;
    }

    public void setLevel(int level) {
        this.level = level;
        //TODO: add network
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        //TODO: add network
    }

    public boolean isNewLevel() {
        return newLevel;
    }

    public void setNewLevel(boolean newLevel) {
        this.newLevel = newLevel;
        //TODO: add network
    }

    public boolean isIntrcatedToday() {
        return isIntrcatedToday;
    }

    public void setIntrcatedToday(boolean intrcatedToday) {
        isIntrcatedToday = intrcatedToday;
        //TODO: add network
    }

    public boolean isBouquetBought() {
        return bouquetBought;
    }

    public void setBouquetBought(boolean bouquetBought) {
        this.bouquetBought = bouquetBought;
        //TODO: add network
    }

    public boolean isMarried() {
        return isMarried;
    }

    public void setMarried(boolean married) {
        isMarried = married;
        //TODO: add network
    }

    public ArrayList<String> getMessageHistory() {
        return messageHistory;
    }

    public void setMessageHistory(ArrayList<String> messageHistory) {
        this.messageHistory = messageHistory;
    }

    // Add a message to the chat history
    public void addMessage(String message) {
        messageHistory.add(message);
    }

    // Clear all chat messages
    public void clearMessageHistory() {
        messageHistory.clear();
    }

    // Get the last N messages
    public ArrayList<String> getLastMessages(int count) {
        ArrayList<String> lastMessages = new ArrayList<>();
        int start = Math.max(0, messageHistory.size() - count);

        for (int i = start; i < messageHistory.size(); i++) {
            lastMessages.add(messageHistory.get(i));
        }

        return lastMessages;
    }

    // Check if there are any messages
    public boolean hasMessages() {
        return !messageHistory.isEmpty();
    }

    // Get message count
    public int getMessageCount() {
        return messageHistory.size();
    }

    public void changeXp(int delta, Player owner, Player friend) {
        this.xp += delta;

        if (delta >= 0) handleLevelUp(owner, friend);
        else handleLevelDown(owner, friend);

    }

    private void handleLevelUp(Player owner, Player friend) {
        while (true) {
            switch (this.level) {
                case 0:
                    if (xp >= 100) levelUp(100, owner, friend);
                    else return;
                    break;
                case 1:
                    if (xp >= 200) levelUp(200, owner, friend);
                    else return;
                    break;
                case 2:
                    if (xp >= 300 && bouquetBought) levelUp(300, owner, friend);
                    else return;
                    break;
                case 3:
                    if (xp >= 400 && isMarried) levelUp(xp, owner, friend);
                    else return;
                    break;
                default:
                    return;
            }
        }
    }

    private void handleLevelDown(Player owner, Player friend) {
        while (this.level > 0) {
            int threshold = getThresholdForLevel(this.level - 1);
            if (this.xp < 0) {
                this.level--;
                this.xp += threshold;
                newLevel = true;
            } else {
                break;
            }
        }
        GameClient.getInstance().send(new UpdateFriendshipMessage(owner.getUser().getHashId(), friend.getUser().getHashId(), this));
    }

    private void levelUp(int threshold, Player owner, Player friend) {
        this.level++;
        this.xp -= threshold;
        newLevel = true;
        GameClient.getInstance().send(new UpdateFriendshipMessage(owner.getUser().getHashId(), friend.getUser().getHashId(), this));
    }

    public int getThresholdForLevel(int level) {
        return switch (level) {
            case 0 -> 100;
            case 1 -> 200;
            case 2 -> 300;
            default -> Integer.MAX_VALUE;
        };
    }
}
