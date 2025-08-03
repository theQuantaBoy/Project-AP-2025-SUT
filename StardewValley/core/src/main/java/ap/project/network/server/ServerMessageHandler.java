package ap.project.network.server;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.network.shared.messages.*;

public class ServerMessageHandler
{
    public static void handle(ClientConnection client, Message message)
    {
        switch (message.getType())
        {
            case TEST:
                handleTestMessage((TestMessage) message);
                break;
            case USER_PROFILE:
                handleUserProfile(client, (UserProfileMessage) message);
                break;
        }
    }

    private static void handleTestMessage(TestMessage msg)
    {
        System.out.println("Test Received: " + msg.getText());
    }

    private static void handleUserProfile(ClientConnection connection, UserProfileMessage msg)
    {
        String username = msg.username;
        String nickname = msg.nickname;
        Gender gender = Gender.getGender(msg.gender);
        int id = msg.userId;

        User user = new User(username, nickname, gender, id);

        System.out.println("Received profile: " + user.getUsername());

        // Check if user already exists in server's connection list
        GameServer server = GameServer.getInstance();
        boolean userExists = server.getConnectedClients().stream()
            .anyMatch(c -> c.getUserId() == user.getHashId());

        if (userExists) {
            System.out.println("User already connected: " + user.getUsername());
            connection.send(new ConnectionFailedMessage("User already connected"));
        } else {
            // Register user
            connection.setUserId(user);
            System.out.println("Registered user: " + user.getUsername());
            connection.send(new ConnectionConfirmedMessage());

            // Add to server's user list
            server.addUser(user);
        }
    }
}
