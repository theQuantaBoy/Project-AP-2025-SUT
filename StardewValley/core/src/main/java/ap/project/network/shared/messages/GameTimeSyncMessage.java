package ap.project.network.shared.messages;

import ap.project.network.shared.enums.MessageType;

public class GameTimeSyncMessage extends Message
{
    public int minute;
    public int hour;
    public int day;
    public int totalHours;
    public int totalDays;
    public String currentWeather;
    public String tomorrowWeather;

    public GameTimeSyncMessage() {}

    public GameTimeSyncMessage(int minute, int hour, int day, int totalHours, int totalDays, String currentWeather, String tomorrowWeather)
    {
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.totalHours = totalHours;
        this.totalDays = totalDays;
        this.currentWeather = currentWeather;
        this.tomorrowWeather = tomorrowWeather;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.GAME_TIME_SYNC;
    }
}
