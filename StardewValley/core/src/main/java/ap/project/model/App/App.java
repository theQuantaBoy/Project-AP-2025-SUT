package ap.project.model.App;

import ap.project.model.game.DummyGame;
import ap.project.model.game.Game;
import ap.project.model.enums.Menu;
import ap.project.model.game.Player;
import ap.project.model.game.Time;
import ap.project.network.shared.DTO.PlayerDTO;
import ap.project.network.shared.Mapper.Mapper;
import ap.project.util.SQLiteUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ap.project.util.SQLiteUtil.loadPlayerState;

public class App
{
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Game> games = new ArrayList<>();

    private static ArrayList<DummyGame> savedGames = new ArrayList<>();

    private static Game currentGame = null;
    private static User currentUser = null;
    private static Menu currentMenu = Menu.RegisterMenu;
    private static User loggedInUser = null;

    public static void initialize()
    {
        // Load users
        users = SQLiteUtil.loadUserList("saves/app/users_client.db");

        // Load logged in user
        int loggedInId = SQLiteUtil.loadLoggedInUser("saves/app/logged_in_user.db");
        if (loggedInId != -1)
        {
            for (User user : users)
            {
                if (user.getHashId() == loggedInId)
                {
                    loggedInUser = user;
                    break;
                }
            }
        }

        // load games
        savedGames = loadSavedGames();
    }

    public static void shutdown()
    {
        // Save users
        SQLiteUtil.saveUserList("saves/app/users_client.db", users);

        // Save logged in user
        if (loggedInUser != null)
        {
            SQLiteUtil.saveLoggedInUser("saves/app/logged_in_user.db", loggedInUser.getHashId());
        }
    }

    public static List<DummyGame> getSavedGames()
    {
        return savedGames;
    }

    public static ArrayList<DummyGame> getGamesForPlayer(int playerHashId)
    {
        ArrayList<DummyGame> playerGames = new ArrayList<>();
        for (DummyGame game : savedGames)
        {
            if (game.getPlayerIds().contains(playerHashId))
            {
                playerGames.add(game);
            }
        }
        return playerGames;
    }

    public static Game loadGame(String gameId)
    {
        // 1. Create game instance
        Game game = new Game(gameId);

        // 2. Load game time
        Time time = SQLiteUtil.loadGameTime(gameId);
        if (time != null)
        {
            game.setCurrentTime(time);
        }

        // 3. Load players
        ArrayList<Player> players = new ArrayList<>();
        File gameDir = new File("saves/games/" + gameId);

        for (File playerFile : gameDir.listFiles())
        {
            String fileName = playerFile.getName();
            if (playerFile.isFile() && fileName.endsWith(".db") &&
                !fileName.equals("game_data.db"))
            {
                try
                {
                    int playerHashId = Integer.parseInt(fileName.replace(".db", ""));
                    PlayerDTO dto = SQLiteUtil.loadPlayerState(gameId, String.valueOf(playerHashId));

                    if (dto != null)
                    {
                        Player player = Mapper.fromDTO(dto);
                        players.add(player);
                    }
                } catch (Exception e)
                {
                    // Skip invalid files
                }
            }
        }

        game.setPlayers(players);
        setCurrentGame(game);
        return game;
    }

    private static ArrayList<DummyGame> loadSavedGames()
    {
        ArrayList<DummyGame> games = new ArrayList<>();
        File gamesDir = new File("saves/games");

        if (!gamesDir.exists() || !gamesDir.isDirectory())
        {
            return games;
        }

        for (File gameDir : gamesDir.listFiles())
        {
            if (gameDir.isDirectory())
            {
                String gameId = gameDir.getName();
                ArrayList<Integer> playerIds = new ArrayList<>();

                // Get player IDs from .db files
                for (File playerFile : gameDir.listFiles())
                {
                    if (playerFile.isFile() && playerFile.getName().endsWith(".db"))
                    {
                        try
                        {
                            String fileName = playerFile.getName();
                            if (!fileName.equals("game_data.db"))
                            {
                                int playerId = Integer.parseInt(fileName.replace(".db", ""));
                                playerIds.add(playerId);
                            }
                        } catch (NumberFormatException e)
                        {
                            // Skip invalid files
                        }
                    }
                }

                // Get game duration from time data
                Time time = SQLiteUtil.loadGameTime(gameId);
                if (time != null)
                {
                    games.add(new DummyGame(
                        gameId,
                        playerIds,
                        time.getTotalDaysPassed(),
                        time.getTotalHoursPassed()
                    ));
                }
            }
        }

        return games;
    }

    public static User getUserByHashId(int hashId)
    {
        for (User user : App.getUsers())
        {
            if (user.getHashId() == hashId)
            {
                return user;
            }
        }
        return null;
    }

    public static void createUser(User newUser)
    {
        users.add(newUser);
        SQLiteUtil.saveUserList("saves/app/users_client.db", users);
    }

    public static void updateUser(User updatedUser)
    {
        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).getHashId() == updatedUser.getHashId())
            {
                users.set(i, updatedUser);
                break;
            }
        }
        SQLiteUtil.saveUserList("saves/app/users_client.db", users);
    }

    // Add this method for user sync
    public static void syncUsersWithServer(List<User> serverUsers)
    {
        Map<Integer, User> clientUserMap = new HashMap<>();
        for (User user : users)
        {
            clientUserMap.put(user.getHashId(), user);
        }

        // Add new users from server
        for (User serverUser : serverUsers)
        {
            if (!clientUserMap.containsKey(serverUser.getHashId()))
            {
                users.add(serverUser);
            }
        }

        // Save updated list
        SQLiteUtil.saveUserList("saves/app/users_client.db", users);
    }

    public static void saveCurrentGame()
    {
        if (currentGame != null)
        {
            // Save game time
            SQLiteUtil.saveGameTime(currentGame.getId(), currentGame.getCurrentTime());

            // Save all players
            for (Player player : currentGame.getPlayers())
            {
                PlayerDTO dto = new PlayerDTO(player);
                try
                {
                    SQLiteUtil.savePlayerState(
                        currentGame.getId(),
                        String.valueOf(player.getUser().getHashId()),
                        dto
                    );
                } catch (Exception e)
                {
                    System.err.println("Error saving player state: " + e.getMessage());
                }
            }

            // Update saved games list
            refreshSavedGames();
        }
    }

    private static void refreshSavedGames()
    {
        savedGames = loadSavedGames();
    }

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static Game getCurrentGame() {
        return currentGame;
    }

    public static void setCurrentGame(Game currentGame) {
        App.currentGame = currentGame;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        App.currentUser = currentUser;
    }

    public static Menu getCurrentMenu()
    {
        return currentMenu;
    }

    public static void setCurrentMenu(Menu currentMenu)
    {
        App.currentMenu = currentMenu;
    }

    public static void addGame(Game game)
    {
        games.add(game);
    }

    public static User getPlayerByUsername(String username) {
        for (User user : App.getUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static User getLoggedInUser()
    {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser)
    {
        App.loggedInUser = loggedInUser;
        SQLiteUtil.saveLoggedInUser("saves/app/logged_in_user.db", loggedInUser.getHashId());
    }

    public static void createOrUpdateUser(User user)
    {
        boolean found = false;
        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).getHashId() == user.getHashId())
            {
                users.set(i, user);
                found = true;
                break;
            }
        }

        if (!found)
        {
            users.add(user);
        }

        SQLiteUtil.saveUserList("saves/app/users_server.db", new ArrayList<>(users));
    }
}
