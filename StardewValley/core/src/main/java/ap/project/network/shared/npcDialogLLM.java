package ap.project.network.shared;

import ap.project.model.game.NPC;
import ap.project.util.NpcContextGenerator;
import com.badlogic.gdx.Gdx;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.function.Consumer;

public class npcDialogLLM
{
    private static final String MODEL = "mistralai/mistral-7b-instruct";
    private static final String API_KEY = loadApiKey();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static String loadApiKey()
    {
        try (InputStream input = npcDialogLLM.class
            .getResourceAsStream("/secrets.properties"))
        {

            if (input == null)
            {
                throw new RuntimeException("secrets.properties not found in classpath");
            }

            Properties prop = new Properties();
            prop.load(input);
            String apiKey = prop.getProperty("OPENROUTER_API_KEY");

            if (apiKey == null || apiKey.isBlank())
            {
                throw new RuntimeException("OPENROUTER_API_KEY not found in secrets.properties");
            }

            return apiKey;
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to load API key: " + e.getMessage(), e);
        }
    }

    public static void generateDynamicDialogue(NPC npc, Consumer<String> callback)
    {
        // Precompute fallback on main thread to avoid threading issues
        final String fallback = fallbackDialogue(npc);

        String context;
        try {
            context = NpcContextGenerator.generateContext(npc);
        } catch (Exception e) {
            callback.accept(fallback); // Use fallback if context fails
            return;
        }

        // Start async HTTP request
        getNPCDialogueAsync(context, fallback, callback);
    }

    private static void getNPCDialogueAsync(String context, String fallback, Consumer<String> callback)
    {
        // Prepare the conversation history
        String jsonBody = String.format(
            "{\"model\":\"%s\",\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"%s\"}," +
                "{\"role\":\"user\",\"content\":\"What would you say to the player in 10 words or less?\"}" +
                "],\"max_tokens\":50}",
            MODEL,
            escapeJson(context)
        );

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
            .header("Authorization", "Bearer " + API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        // Async request with callback handling
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApplyAsync(response -> {
                try {
                    return extractResponseContent(response.body());
                } catch (Exception e) {
                    return fallback; // Use fallback on parsing error
                }
            })
            .exceptionally(e -> fallback) // Use fallback on network error
            .thenAccept(result -> {
                // Post result to LibGDX main thread
                Gdx.app.postRunnable(() -> callback.accept(result));
            });
    }

    private static String escapeJson(String content)
    {
        return content.replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    private static String extractResponseContent(String jsonResponse)
    {
        // Simple JSON parser for the response
        int contentStart = jsonResponse.indexOf("\"content\":\"") + 11;
        if (contentStart < 11) throw new RuntimeException("No content found in response");

        int contentEnd = jsonResponse.indexOf("\"", contentStart);
        String content = jsonResponse.substring(contentStart, contentEnd);

        // Unescape special characters
        return content.replace("\\\"", "\"")
            .replace("\\n", "\n")
            .replace("\\'", "'");
    }

    private static String fallbackDialogue(NPC npc)
    {
        // Fallback to existing dialogue system
        return npc.talk();
    }
}
