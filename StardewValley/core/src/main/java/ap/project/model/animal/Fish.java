package ap.project.model.animal;

import ap.project.model.App.App;
import ap.project.model.enums.animal_enums.FishQuality;
import ap.project.model.game.GameObject;
import ap.project.model.enums.Weather;
import ap.project.model.enums.animal_enums.FishType;
import ap.project.model.enums.tool_enums.FishingPoleLevel;

import java.util.Random;

public class Fish extends GameObject
{
    private FishType type;
    private double quality = 0.0;
    private FishQuality fishQuality;

    public Fish(FishType type)
    {
        super.ObjectType = type.getType();
        this.type = type;

        if (Math.random() < 0.5)
        {
            this.fishQuality = FishQuality.SILVER;
        } else
        {
            this.fishQuality = FishQuality.GOLD;
        }
    }

    public void increaseQuality()
    {
        if (fishQuality == FishQuality.SILVER)
        {
            fishQuality = FishQuality.GOLD;
        } else if (fishQuality == FishQuality.GOLD)
        {
            fishQuality = FishQuality.IRIDIUM;
        }
    }

    public double getQuality()
    {
        return quality;
    }

    public void calculateQuality(FishingPoleLevel level)
    {
        Random random = new Random();
        int R = random.nextInt(2);
        int skill = App.getCurrentGame().getCurrentPlayer().getFishingSkill().getLevel();
        double M;
        double pole = 0;
        switch (App.getCurrentGame().getCurrentTime().getCurrentWeather())
        {
            case Weather.Sunny -> M = 1.5;
            case Weather.Rain -> M = 1.2;
            case Weather.Storm -> M = 0.5;
            default -> M = 1;
        }
        switch (level)
        {
            case FishingPoleLevel.Training -> pole = 0.1;
            case FishingPoleLevel.Bamboo -> pole = 0.5;
            case FishingPoleLevel.FiberGlass -> pole = 0.9;
            case FishingPoleLevel.Iridium -> pole = 1.2;
        }

        this.quality = (R * (skill + 2) * pole) / (7 - M);
    }

    public FishType getType()
    {
        return type;
    }
}
