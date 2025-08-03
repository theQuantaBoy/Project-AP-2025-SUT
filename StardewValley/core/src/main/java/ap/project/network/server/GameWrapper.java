package ap.project.network.server;

import ap.project.model.game.Game;

public class GameWrapper
{
    private final Game game;
    private boolean isActive = false;

    private long lastTimeSync;
    private static final long SYNC_INTERVAL = 500;
    private long lastMinuteAdvance;

    private boolean gameStarted = false;

    public GameWrapper(Game game)
    {
        this.game = game;

        lastTimeSync = System.currentTimeMillis();
        lastMinuteAdvance = 0;

        gameStarted = true;
    }

    public void start()
    {
        isActive = true;
    }

    public void pause()
    {
        isActive = false;
    }

    public boolean isActive()
    {
        return isActive;
    }
}
