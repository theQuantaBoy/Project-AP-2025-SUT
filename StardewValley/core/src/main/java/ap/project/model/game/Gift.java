package ap.project.model.game;

import ap.project.model.enums.GameObjectType;

public class Gift {
    private final GameObjectType gameObject;
    private final Player giver;
    private final Player taker;
    private final int amount;
    private int rate;
    private final int id;
    private static int lastAssigned = 0;
    private boolean isRated = false;

    public Gift(GameObjectType gameObject, Player giver, Player taker, int amount) {
        this.gameObject = gameObject;
        this.giver = giver;
        this.taker = taker;
        this.amount = amount;
        this.id = ++lastAssigned;
    }

    public GameObjectType getGameObject() {
        return gameObject;
    }

    public Player getGiver() {
        return giver;
    }

    public Player getTaker() {
        return taker;
    }

    public int getAmount() {
        return amount;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
        isRated = true;
    }

    public int getId() {
        return id;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    @Override
    public String toString() {
        return gameObject.toString();
    }
}
