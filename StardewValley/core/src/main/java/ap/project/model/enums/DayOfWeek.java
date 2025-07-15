package ap.project.model.enums;

public enum DayOfWeek
{
    Monday(0, "Monday", "Mon"),
    Tuesday(1, "Tuesday", "Tue"),
    Wednesday(2, "Wednesday", "Wed"),
    Thursday(3, "Thursday", "Thu"),
    Friday(4, "Friday", "Fri"),
    Saturday(5, "Saturday", "Sat"),
    Sunday(6, "Sunday", "Sun");

    private final int id;
    private final String name;
    private final String shortName;

    DayOfWeek(int id, String name, String shortName)
    {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }

    public static String getDayOfWeek(int id)
    {
        return DayOfWeek.values()[id].name();
    }

    public static String getShortDayOfWeek(int id)
    {
        return DayOfWeek.values()[id].shortName;
    }
}
