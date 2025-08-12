package ap.project.util;

import ap.project.model.App.App;
import ap.project.model.enums.*;
import ap.project.model.game.NPC;

import java.util.Random;

import static ap.project.model.enums.CharacterType.*;

public class NpcContextGenerator
{
    private static final Random random = new Random();
    private static final String[] TOPIC_PROMPTS =
        {
        "Comment on the current weather",
        "Mention the current season",
        "Reference the time of day",
        "Talk about your favorite items",
        "Mention an available quest",
        "Comment on a completed quest"
    };

    public static String generateContext(NPC npc)
    {
        NpcDetails details = npc.getNpcDetails();
        StringBuilder context = new StringBuilder();

        context.append("You are ").append(details.getName()).append(", ");

        context.append(getPersonalityDescription(details)).append(" ");

        context.append(getGameStateContext(npc));

        context.append(getInteractionTopic(npc));

        context.append("Respond in exactly 1 sentence, maximum 10 words. Be extremely concise.");

        return context.toString();
    }

    private static String getPersonalityDescription(NpcDetails details)
    {
        return switch (details)
        {
            case NpcDetails.Sebastian -> "a brooding, introverted programmer who spends most of his time in his basement";
            case NpcDetails.Abigail -> "an adventurous young woman fascinated by the occult and exploration";
            case NpcDetails.Harvey -> "the town's caring but slightly anxious doctor";
            case NpcDetails.Lia -> "a nature-loving artist who finds inspiration in the world around you";
            case NpcDetails.Robin -> "the town's hardworking carpenter who takes pride in building things";
            default -> "a villager in this community";
        };
    }

    private static String getGameStateContext(NPC npc)
    {
        Season season = App.getCurrentGame().getCurrentTime().getSeason();
        TimeOfDay timeOfDay = App.getCurrentGame().getCurrentTime().getTimeOfDay();
        Weather weather = App.getCurrentGame().getCurrentTime().getCurrentWeather();
        int hour = App.getCurrentGame().getCurrentTime().getHour();

        StringBuilder context = new StringBuilder("It's currently ");

        // Time context
        if (hour >= npc.getNpcDetails().getWorkStartHour() &&
            hour < npc.getNpcDetails().getWorkEndHour())
        {
            context.append("during your work hours, ");
        } else
        {
            context.append("outside your work hours, ");
        }

        // Weather and season
        context.append(weather.toString().toLowerCase()).append(" in ")
            .append(season.getName()).append(". ");

        return context.toString();
    }

    private static String getInteractionTopic(NPC npc)
    {
        String selectedPrompt = TOPIC_PROMPTS[random.nextInt(TOPIC_PROMPTS.length)];

        return switch (selectedPrompt)
        {
            case "Comment on the current weather" ->
                "Comment briefly on how the weather affects your mood or activities. ";
            case "Mention the current season" ->
                "Mention something you like or dislike about this season. ";
            case "Reference the time of day" ->
                "Comment on what you typically do at this time of day. ";
            case "Talk about your favorite items" ->
                "Mention one of your favorite items: " +
                    getRandomFavorite(npc.getNpcDetails()) + ". ";
            case "Mention an available quest" ->
                npc.isFirstQuestAvailable() ? "Mention that you have a task that needs completing. " :
                    npc.isSecondQuestAvailable() ? "Hint that you could use some help with something. " :
                        npc.isThirdQuestAvailable() ? "Casually mention you have a challenging request. " :
                            "Comment on how things have been lately. ";
            case "Comment on a completed quest" ->
                !npc.getFirstQuestUser().isEmpty() ? "Thank the player for helping you recently. " :
                    !npc.getSecondQuestUser().isEmpty() ? "Mention how the player's help made a difference. " :
                        !npc.getThirdQuestUser().isEmpty() ? "Remark on how the player's assistance was valuable. " :
                            "Comment on village life. ";
            default -> "Share a brief thought about your day. ";
        };
    }

    private static String getRandomFavorite(NpcDetails details)
    {
        if (details.getFavorites().isEmpty()) return "";
        GameObjectType favorite = details.getFavorites().get(
            random.nextInt(details.getFavorites().size()));
        return favorite.toString().toLowerCase().replace("_", " ");
    }
}
