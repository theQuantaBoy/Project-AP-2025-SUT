package ap.project.network.shared.messages;

import ap.project.model.enums.Season;
import ap.project.model.enums.Weather;
import ap.project.network.shared.enums.MessageType;

public class GameTimeSyncMessage extends Message
{
    public int day;
    public int hour;
    public int minute;
    public int seasonOrdinal;
    public int weatherOrdinal;
    public int tomorrowWeatherOrdinal;
    public long serverTimestamp; // Unix timestamp in milliseconds
    public int totalHours;
    public int totalDays;

    public GameTimeSyncMessage() {} // Kryo requires this

    public GameTimeSyncMessage(int day, int hour, int minute, Season season, Weather weather, Weather tomorrowWeather,
                               int totalHours, int totalDays)
    {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.seasonOrdinal = season.ordinal();
        this.weatherOrdinal = weather.ordinal();
        this.tomorrowWeatherOrdinal = tomorrowWeather.ordinal();
        this.serverTimestamp = System.currentTimeMillis();
        this.totalHours = totalHours;
        this.totalDays = totalDays;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.TIME_SYNC;
    }
}
