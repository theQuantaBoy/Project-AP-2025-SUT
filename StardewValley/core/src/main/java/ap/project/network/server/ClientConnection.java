package ap.project.network.server;

import ap.project.network.shared.messages.Message;
import com.esotericsoftware.kryonet.Connection;

public class ClientConnection
{
    private final Connection connection;
    private String playerId;

    public ClientConnection(Connection connection)
    {
        this.connection = connection;
    }

    public void send(Message message)
    {
        connection.sendTCP(message);
    }

    // Getters/setters...
}
