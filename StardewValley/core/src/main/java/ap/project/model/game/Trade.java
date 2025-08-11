package ap.project.model.game;

import java.util.List;

public class Trade {
    private final Player request;
    private final Player response;
    private final List<GameObject> requestedItems;
    private final List<GameObject> offeredItems;
    private final int tradeID;
    private static int lastAssignedTradeID = 0;

    public Trade(Player request, Player response,  List<GameObject> requestedItems, List<GameObject> offeredItems) {
        this.request = request;
        this.response = response;
        this.requestedItems = requestedItems;
        this.offeredItems = offeredItems;
        this.tradeID = ++lastAssignedTradeID;
    }

    public Player getRequest() {
        return request;
    }

    public Player getResponse() {
        return response;
    }

    public List<GameObject> getRequestedItems() {
        return requestedItems;
    }

    public List<GameObject> getOfferedItems() {
        return offeredItems;
    }

    public int getTradeID() {
        return tradeID;
    }
}
