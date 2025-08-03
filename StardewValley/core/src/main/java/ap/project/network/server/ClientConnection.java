package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.network.shared.messages.Message;
import ap.project.network.shared.messages.UserProfileMessage;
import ap.project.network.shared.messages.GameConfigMessage;
import com.esotericsoftware.kryonet.Connection;

public class ClientConnection
{
    private final Connection connection;
    private GameConfigMessage.PlayerConfig playerConfig;
    private boolean isReady = false;
    private int userId;

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

    public GameConfigMessage.PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public boolean isReady() {
        return isReady;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setPlayerConfig(GameConfigMessage.PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
