package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.model.game.Lobby;
import ap.project.network.shared.messages.Message;
import com.esotericsoftware.kryonet.Connection;

public class ClientConnection
{
    private final Connection connection;
    private int userId;

    private boolean isOnline = false;
    private boolean isInLobby = false;

    public float lastOnlineCheckTime = 0f;
    public float lastLobbyCheckTime = 0f;

    public int characterChoice = 0;
    public int mapChoice = 0;

    public Lobby lobby = null;

    public ClientConnection(Connection connection)
    {
        this.connection = connection;
    }

    public void send(Message message) {
        connection.sendTCP(message);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setUserId(User user)
    {
        this.userId = user.getHashId();
    }

    public int getUserId()
    {
        return userId;
    }

    public void setOnline(boolean online)
    {
        isOnline = online;
    }

    public void setInLobby(boolean inLobby)
    {
        isInLobby = inLobby;
    }

    public boolean isOnline()
    {
        return isOnline;
    }

    public boolean isInLobby()
    {
        return isInLobby;
    }
}
