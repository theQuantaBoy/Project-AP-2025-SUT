package ap.project.util;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;
import ap.project.model.enums.Weather;
import ap.project.network.shared.DTO.PlayerDTO;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import ap.project.network.shared.KryoRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteUtil
{
    private static final String BASE_DIR = "saves" + File.separator + "games" + File.separator;
    private static final ObjectMapper jsonMapper = JsonFileUtil.mapper;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }

    public static void savePlayerState(String gameId, String playerId, PlayerDTO dto) throws Exception {
        Path gameDir = Paths.get(BASE_DIR, gameId);
        Files.createDirectories(gameDir);

        String dbPath = gameDir.resolve(playerId + ".db").toAbsolutePath().toString();
        String jdbcUrl = "jdbc:sqlite:" + dbPath.replace("\\", "/");

        // Serialize to JSON
        String json = jsonMapper.writeValueAsString(dto);

        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS player_state (id INTEGER PRIMARY KEY, data TEXT)");

            try (PreparedStatement upsert = conn.prepareStatement(
                "INSERT OR REPLACE INTO player_state (id, data) VALUES (1, ?)")) {
                upsert.setString(1, json);
                upsert.executeUpdate();
            }
        }
    }

    public static PlayerDTO loadPlayerState(String gameId, String playerId) throws Exception {
        Path dbPath = Paths.get(BASE_DIR, gameId, playerId + ".db");
        if (!Files.exists(dbPath)) return null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT data FROM player_state WHERE id = 1")) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String json = rs.getString("data");
                return jsonMapper.readValue(json, PlayerDTO.class);
            }
        }
        return null;
    }

    public static void saveGameData(String gameId, String dataType, Serializable data) throws Exception
    {
        Path gameDir = Paths.get(BASE_DIR, gameId);
        Files.createDirectories(gameDir);

        String dbPath = gameDir.resolve("game_data.db").toString();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(
                 "CREATE TABLE IF NOT EXISTS game_data (data_type TEXT PRIMARY KEY, data BLOB)"))
        {
            pstmt.executeUpdate();

            try (PreparedStatement upsert = conn.prepareStatement(
                "INSERT OR REPLACE INTO game_data (data_type, data) VALUES (?, ?)"))
            {
                upsert.setString(1, dataType);
                upsert.setBytes(2, serializeObject(data));
                upsert.executeUpdate();
            }
        }
    }

    public static <T extends Serializable> T loadGameData(String gameId, String dataType, Class<T> type) throws Exception
    {
        Path dbPath = Paths.get(BASE_DIR, gameId, "game_data.db");
        if (!Files.exists(dbPath)) return null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT data FROM game_data WHERE data_type = ?"))
        {

            pstmt.setString(1, dataType);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                byte[] data = rs.getBytes("data");
                return deserializeObject(data, type);
            }
        }
        return null;
    }

    private static byte[] serializeDTO(PlayerDTO dto) throws IOException
    {
        Kryo kryo = new Kryo();
        KryoRegistry.registerClasses(kryo);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos))
        {
            kryo.writeObject(output, dto);
            output.flush();
            return baos.toByteArray();
        }
    }

    private static PlayerDTO deserializeDTO(byte[] data)
    {
        Kryo kryo = new Kryo();
        KryoRegistry.registerClasses(kryo);

        try (Input input = new Input(new ByteArrayInputStream(data)))
        {
            return kryo.readObject(input, PlayerDTO.class);
        }
    }

    private static byte[] serializeObject(Serializable obj) throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos))
        {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T deserializeObject(byte[] data, Class<T> type) throws IOException, ClassNotFoundException
    {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data)))
        {
            return (T) ois.readObject();
        }
    }

    public static void saveUserList(String dbPath, List<User> users)
    {
        createDirectoryIfNeeded(dbPath);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement())
        {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "hashId INTEGER PRIMARY KEY, " +
                "username TEXT, " +
                "password TEXT, " +
                "nickname TEXT, " +
                "email TEXT, " +
                "gender TEXT, " +
                "question TEXT, " +
                "answer TEXT, " +
                "numberOfGames INTEGER, " +
                "characterChoice INTEGER, " +
                "mapChoice INTEGER)");

            // Clear existing data
            stmt.execute("DELETE FROM users");

            // Insert current users
            try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))
            {

                for (User user : users)
                {
                    pstmt.setInt(1, user.getHashId());
                    pstmt.setString(2, user.getUsername());
                    pstmt.setString(3, user.getPassword());
                    pstmt.setString(4, user.getNickname());
                    pstmt.setString(5, user.getEmail());
                    pstmt.setString(6, user.getGender() != null ? user.getGender().name() : null);
                    pstmt.setString(7, user.getQuestion());
                    pstmt.setString(8, user.getAnswer());
                    pstmt.setInt(9, user.getNumberOfGames());
                    pstmt.setInt(10, user.getCharacterChoice());
                    pstmt.setInt(11, user.getMapChoice());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<User> loadUserList(String dbPath)
    {
        ArrayList<User> users = new ArrayList<>();
        if (!new File(dbPath).exists()) return users;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users"))
        {

            while (rs.next())
            {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nickname"),
                    rs.getString("email"),
                    Gender.valueOf(rs.getString("gender")),
                    rs.getString("question"),
                    rs.getString("answer"),
                    rs.getInt("numberOfGames"),
                    rs.getInt("hashId"),
                    rs.getInt("characterChoice"),
                    rs.getInt("mapChoice")
                );
                users.add(user);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return users;
    }

    // Logged-in user operations
    public static void saveLoggedInUser(String dbPath, int hashId)
    {
        createDirectoryIfNeeded(dbPath);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement())
        {

            stmt.execute("CREATE TABLE IF NOT EXISTS logged_in_user (" +
                "id INTEGER PRIMARY KEY CHECK(id = 1), " +
                "hashId INTEGER)");

            stmt.execute("DELETE FROM logged_in_user");

            try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO logged_in_user VALUES (1, ?)"))
            {
                pstmt.setInt(1, hashId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static int loadLoggedInUser(String dbPath)
    {
        if (!new File(dbPath).exists()) return -1;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT hashId FROM logged_in_user"))
        {

            if (rs.next())
            {
                return rs.getInt("hashId");
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return -1;
    }

    // Game time operations
    public static void saveGameTime(String gameId, ap.project.model.game.Time time)
    {
        String dbPath = "saves/games/" + gameId + "/game_data.db";
        createDirectoryIfNeeded(dbPath);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement())
        {

            stmt.execute("CREATE TABLE IF NOT EXISTS game_time (" +
                "minute INTEGER, hour INTEGER, day INTEGER, " +
                "totalDaysPassed INTEGER, totalHoursPassed INTEGER, " +
                "currentWeather TEXT, tomorrowWeather TEXT)");

            stmt.execute("DELETE FROM game_time");

            try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO game_time VALUES (?, ?, ?, ?, ?, ?, ?)"))
            {
                pstmt.setInt(1, time.getMinute());
                pstmt.setInt(2, time.getHour());
                pstmt.setInt(3, time.getDay());
                pstmt.setInt(4, time.getTotalDaysPassed());
                pstmt.setInt(5, time.getTotalHoursPassed());
                pstmt.setString(6, time.getCurrentWeather().name());
                pstmt.setString(7, time.getTomorrowWeather().name());
                pstmt.executeUpdate();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static ap.project.model.game.Time loadGameTime(String gameId)
    {
        String dbPath = "saves/games/" + gameId + "/game_data.db";
        if (!new File(dbPath).exists()) return null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM game_time"))
        {

            if (rs.next())
            {
                ap.project.model.game.Time time = new ap.project.model.game.Time();
                time.setMinute(rs.getInt("minute"));
                time.setHour(rs.getInt("hour"));
                time.setDay(rs.getInt("day"));
                time.setTotalDaysPassed(rs.getInt("totalDaysPassed"));
                time.setTotalHoursPassed(rs.getInt("totalHoursPassed"));
                time.setCurrentWeather(Weather.valueOf(rs.getString("currentWeather")));
                time.setTomorrowWeather(Weather.valueOf(rs.getString("tomorrowWeather")));
                return time;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static void createDirectoryIfNeeded(String path)
    {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists())
        {
            parentDir.mkdirs();
        }
    }

    public static ArrayList<String> getSavedGameIds()
    {
        ArrayList<String> gameIds = new ArrayList<>();
        File gamesDir = new File("saves/games");

        if (gamesDir.exists() && gamesDir.isDirectory())
        {
            for (File file : gamesDir.listFiles())
            {
                if (file.isDirectory())
                {
                    gameIds.add(file.getName());
                }
            }
        }
        return gameIds;
    }

    public static List<String> getGameIdsForPlayer(int playerHashId)
    {
        List<String> gameIds = new ArrayList<>();
        File gamesDir = new File("saves/games");

        if (gamesDir.exists() && gamesDir.isDirectory())
        {
            for (File gameDir : gamesDir.listFiles())
            {
                if (gameDir.isDirectory())
                {
                    File playerFile = new File(gameDir, playerHashId + ".db");
                    if (playerFile.exists())
                    {
                        gameIds.add(gameDir.getName());
                    }
                }
            }
        }
        return gameIds;
    }
}
