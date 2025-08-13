package ap.project.network.shared;

import ap.project.model.game.NPC;
import ap.project.util.NpcContextGenerator;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

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

    public static String generateDynamicDialogue(NPC npc)
    {
        try
        {
            String context = NpcContextGenerator.generateContext(npc);
            return getNPCDialogue(context);
        } catch (Exception e)
        {
            // Fallback to pre-written dialogue
            return fallbackDialogue(npc);
        }
    }

    private static String getNPCDialogue(String context) throws Exception
    {
        // Prepare the conversation history
        String jsonBody = String.format(
            "{\"model\":\"%s\",\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"%s\"}," +
                "{\"role\":\"user\",\"content\":\"What would you say to the player?\"}" +
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

        // Send the request and get response
        HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        // Parse the response
        return extractResponseContent(response.body());
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
