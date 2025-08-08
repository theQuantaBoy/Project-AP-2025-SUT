package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class GamePresenceMessage extends Message
{
    public String gameID;
    public int userID;

    public float x;
    public float y;
    public byte direction;
    public boolean isMoving;

    public boolean isInFarm;
    public boolean isInCity;
    public boolean isInGreenHouse;
    public boolean isInHome;
    public boolean isInZeidiesFarm;
    public boolean isInZeidiesHome;
    public boolean isInShop;

    public String currentShop;

    public GamePresenceMessage() {}

    public GamePresenceMessage(String gameID, int userID, float x, float y, byte direction, boolean isMoving, boolean isInFarm, boolean isInCity, boolean isInGreenHouse, boolean isInHome, boolean isInZeidiesFarm, boolean isInZeidiesHome, boolean isInShop, String currentShop)
    {
        this.gameID = gameID;
        this.userID = userID;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.isMoving = isMoving;
        this.isInFarm = isInFarm;
        this.isInCity = isInCity;
        this.isInGreenHouse = isInGreenHouse;
        this.isInHome = isInHome;
        this.isInZeidiesFarm = isInZeidiesFarm;
        this.isInZeidiesHome = isInZeidiesHome;
        this.isInShop = isInShop;
        this.currentShop = currentShop;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_PRESENCE;
    }
}
