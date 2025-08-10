package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.enums.Season;
import ap.project.model.enums.TimeOfDay;
import ap.project.model.enums.Weather;
import ap.project.model.player_data.FriendshipData;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Random;

public class Time
{
    private int minute = 0;
    private int hour = 9;
    private int day = 1;

    private int totalDaysPassed = 0;
    private int totalHoursPassed = 0;

    private Season season = Season.Spring;
    private TimeOfDay timeOfDay = TimeOfDay.MORNING;

    private Weather currentWeather = Weather.Sunny;
    private Weather tomorrowWeather = Weather.Sunny;

    public void updateMinute(int minutes)
    {
        minute += minutes;
        if (minute > 59)
        {
            minute = 0;
            updateHour(1);
        }
    }

    private void updateTimeOfDay()
    {
        if (hour >= 9 && hour < 12) timeOfDay = TimeOfDay.MORNING;
        else if (hour >= 12 && hour < 17) timeOfDay = TimeOfDay.AFTERNOON;
        else if (hour >= 17 && hour < 23) timeOfDay = TimeOfDay.EVENING;
    }

    public void updateHour(int hourNum)
    {
        hour += hourNum;
        totalHoursPassed += hourNum;

        if (hour > 22)
        {
            int dayNum = ((hour - 23) / 14) + 1;
            hour = ((hour - 23) % 14) + 9;
            updateDay(dayNum, false);
        }

        App.getCurrentGame().updatePlayerBuffs();

        if(hour > 9 && hour <= 12) timeOfDay = TimeOfDay.MORNING;
        if(hour > 12 && hour <= 17) timeOfDay = TimeOfDay.AFTERNOON;
        if(hour > 17 && hour <= 23) timeOfDay = TimeOfDay.EVENING;
    }

    public void updateDay(int dayNum, boolean hasCheated)
    {
        // setting energies to maximum
        for (Player player : App.getCurrentGame().getPlayers())
        {
            player.setEnergy(player.getMaxEnergy());
        }

        day += dayNum;
        totalDaysPassed += dayNum;

        if (hasCheated)
        {
            totalHoursPassed += (14 * dayNum);
        }

        if (day > 28)
        {
            int seasonNum = ((day - 29) / 28) + 1;
            day = ((day - 29) % 28) + 1;
            season = season.update(seasonNum);
        }

        updateWeather();

        for (int i = 0; i < dayNum; i++)
        {
            if (hasCheated)
            {
                if (Gdx.app.getType() != Application.ApplicationType.HeadlessDesktop)
                {
                    App.getCurrentGame().waterAllFarmPlants();
                }
            }

            if (Gdx.app.getType() != Application.ApplicationType.HeadlessDesktop)
            {
                App.getCurrentGame().endDay();
            }
        }

//        friendshipUpdate();

        Game game = App.getCurrentGame();
        game.resetPlayerBuffs();
    }

    public void setFromNetwork(int day, int hour, int minute, Season season, Weather weather, Weather tomorrowWeather)
    {
        System.out.println("TIME SET FROM SERVER");
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.season = season;
        this.currentWeather = weather;
        this.tomorrowWeather = tomorrowWeather;
        updateTimeOfDay();
    }

    public int getHour()
    {
        return hour;
    }

    public int getDay()
    {
        return day;
    }

    public Season getSeason()
    {
        return season;
    }

    public Weather getCurrentWeather()
    {
        return currentWeather;
    }

    public Weather getTomorrowWeather()
    {
        return tomorrowWeather;
    }

    public void setTomorrowWeather(Weather tomorrowWeather)
    {
        this.tomorrowWeather = tomorrowWeather;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    private void updateWeather()
    {
        currentWeather = tomorrowWeather;

        Random random = new Random();
        List<Weather> list = season.getWeatherTypes();
        tomorrowWeather = list.get(random.nextInt(list.size()));
    }

    public void friendshipUpdate() {
        for (Player player1 : App.getCurrentGame().getPlayers()) {
            for (Player player2 : App.getCurrentGame().getPlayers()) {
                if (player1 != player2) {
                    FriendshipData data1 = player1.getFriendships().get(player2);
                    FriendshipData data2 = player2.getFriendships().get(player1);
                    data1.setIntrcatedToday(false);
                    data2.setIntrcatedToday(false);
                }
            }
        }
    }

    public int getTotalDaysPassed()
    {
        return totalDaysPassed;
    }

    public int getTotalHoursPassed()
    {
        return totalHoursPassed;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public void setTotalDaysPassed(int totalDaysPassed)
    {
        this.totalDaysPassed = totalDaysPassed;
    }

    public void setTotalHoursPassed(int totalHoursPassed)
    {
        this.totalHoursPassed = totalHoursPassed;
    }

    public void setCurrentWeather(Weather currentWeather)
    {
        this.currentWeather = currentWeather;
    }
}
