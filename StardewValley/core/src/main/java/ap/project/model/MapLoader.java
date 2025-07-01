package ap.project.model;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLoader {
    public static Map<String, List<Point>> loadMap(String filePath, int[] dimensionsOut) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // ✅ Use LibGDX's file system to read the asset
            InputStream inputStream = Gdx.files.internal(filePath).read();

            Map<String, Object> raw = mapper.readValue(inputStream, new TypeReference<>() {});

            int width = (int) raw.getOrDefault("width", 70);
            int height = (int) raw.getOrDefault("height", 70);

            dimensionsOut[0] = width;
            dimensionsOut[1] = height;

            @SuppressWarnings("unchecked")
            Map<String, List<Map<String, Integer>>> rawTiles =
                (Map<String, List<Map<String, Integer>>>) raw.get("tiles");

            Map<String, List<Point>> finalMap = new HashMap<>();

            for (Map.Entry<String, List<Map<String, Integer>>> entry : rawTiles.entrySet()) {
                List<Point> points = entry.getValue().stream()
                    .map(p -> new Point(p.get("x"), p.get("y")))
                    .toList();
                finalMap.put(entry.getKey(), points);
            }

            return finalMap;

        } catch (Exception e) {
            System.err.println("Failed to load map: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
}
