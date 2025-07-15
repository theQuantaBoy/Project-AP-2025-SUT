package ap.project.model.enums;

import java.util.List;

public enum Season
{
    Spring(0, "spring", List.of(Weather.Sunny, Weather.Rain, Weather.Storm)),
    Summer(1, "summer", List.of(Weather.Sunny, Weather.Rain, Weather.Storm)),
    Fall(2, "fall", List.of(Weather.Sunny, Weather.Rain, Weather.Storm)),
    Winter(3, "winter", List.of(Weather.Sunny, Weather.Snow));

    private final int id;
    private final String name;
    private final List<Weather> weatherTypes;

    Season(int id, String name, List<Weather> weatherTypes)
    {
        this.id = id;
        this.name = name;
        this.weatherTypes = weatherTypes;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public Season update(int num)
    {
        return values()[(this.id + num) % values().length];
    }

    public List<Weather> getWeatherTypes()
    {
        return weatherTypes;
    }

    public String getName()
    {
        return name;
    }
}
