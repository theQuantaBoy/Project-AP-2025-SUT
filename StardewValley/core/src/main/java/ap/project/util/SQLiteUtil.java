package ap.project.util;

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
}
