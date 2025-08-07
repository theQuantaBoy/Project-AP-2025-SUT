package ap.project.model.game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DummyGame
{
    private final String gameId;
    private final ArrayList<Integer> playerIds;
    private final int totalDays;
    private final int totalHours;
    private final long lastSaved;

    public DummyGame(String gameId, ArrayList<Integer> playerIds, int totalDays, int totalHours, long lastSaved)
    {
        this.gameId = gameId;
        this.playerIds = playerIds;
        this.totalDays = totalDays;
        this.totalHours = totalHours;
        this.lastSaved = lastSaved;
    }

    // Getters
    public String getGameId() { return gameId; }
    public ArrayList<Integer> getPlayerIds() { return playerIds; }
    public int getTotalDays() { return totalDays; }
    public int getTotalHours() { return totalHours; }
    public long getLastSaved() { return lastSaved; }

    public String getLastSavedString()
    {
        return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date(lastSaved));
    }

    @Override
    public String toString()
    {
        return "Game: " + gameId +
            "\nPlayers: " + playerIds.size() +
            "\nDuration: " + totalDays + " days, " + totalHours + " hours";
    }
}
