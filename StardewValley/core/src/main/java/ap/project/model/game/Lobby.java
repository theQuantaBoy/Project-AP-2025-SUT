package ap.project.model.game;

import ap.project.model.App.User;
import ap.project.util.StringToNumber;

import java.util.ArrayList;

public class Lobby
{
    private ArrayList<User> users = new ArrayList<>();
    private final String name;
    private final boolean isPrivate;
    private String password;
    private final String id;
    private final boolean isVisible;

    private final long createdAt;

    public Lobby(String name, String password, User user, boolean isVisible)
    {
        this.name = name;
        this.password = password;
        this.isPrivate = true;
        users.add(user);
        this.id = StringToNumber.generateId(6);
        this.isVisible = isVisible;
        this.createdAt = System.currentTimeMillis();
    }

    public Lobby(String name, User user, boolean isVisible)
    {
        this.name = name;
        this.isPrivate = false;
        users.add(user);
        this.id = StringToNumber.generateId(6);
        this.isVisible = isVisible;
        this.createdAt = System.currentTimeMillis();
    }

    public User getAdmin()
    {
        if (!users.isEmpty())
        {
            return users.get(0);
        }

        return null;
    }

    public ArrayList<User> getUsers()
    {
        return users;
    }

    public String getName()
    {
        return name;
    }

    public boolean isPrivate()
    {
        return isPrivate;
    }

    public String getPassword()
    {
        return password;
    }

    public String getId()
    {
        return id;
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public long getCreatedAt()
    {
        return createdAt;
    }
}
