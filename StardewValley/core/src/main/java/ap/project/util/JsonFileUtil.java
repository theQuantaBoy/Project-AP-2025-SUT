package ap.project.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.File;
import java.io.IOException;

public class JsonFileUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // For handling polymorphic DTOs like GameObjectDTO
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
    }

    public static void saveToFile(Object dto, String filePath) throws IOException {
        File file = new File(filePath);

        // Ensure parent directories exist
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs(); // recursively create missing dirs
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, dto);
    }

    public static <T> T loadFromFile(String filePath, Class<T> type) throws IOException {
        return mapper.readValue(new File(filePath), type);
    }
}
