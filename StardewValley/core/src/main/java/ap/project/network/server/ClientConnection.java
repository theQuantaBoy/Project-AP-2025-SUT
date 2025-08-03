package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.network.shared.messages.Message;
import com.esotericsoftware.kryonet.Connection;

public class ClientConnection
{
    private final Connection connection;
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

    public int getUserId()
    {
        return userId;
    }
}
