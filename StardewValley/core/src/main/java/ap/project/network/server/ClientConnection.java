package ap.project.network.server;

import ap.project.network.shared.messages.Message;
import ap.project.network.shared.messages.UserProfileMessage;
import ap.project.network.shared.messages.GameConfigMessage;
import com.esotericsoftware.kryonet.Connection;

public class ClientConnection
{
    private final Connection connection;
    private String playerId;
    private UserProfileMessage userProfile;
    private GameConfigMessage.PlayerConfig playerConfig;
    private boolean isReady = false;

    public ClientConnection(Connection connection) {
        this.connection = connection;
    }

    public void send(Message message) {
        connection.sendTCP(message);
    }

    // Getters
    public Connection getConnection() {
        return connection;
    }

    public String getPlayerId() {
        return playerId;
    }

    public UserProfileMessage getUserProfile() {
        return userProfile;
    }

    public GameConfigMessage.PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public boolean isReady() {
        return isReady;
    }

    // Setters
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setUserProfile(UserProfileMessage userProfile) {
        this.userProfile = userProfile;
    }

    public void setPlayerConfig(GameConfigMessage.PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
