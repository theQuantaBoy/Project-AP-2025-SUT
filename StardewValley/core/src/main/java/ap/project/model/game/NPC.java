package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.enums.CharacterType;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.NpcDetails;
import ap.project.model.player_data.FriendshipWithNpcData;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class NPC {
    private Point location;
    private final NpcDetails npcDetails;
    private final String name;
    private final ArrayList<String> dialogues = new ArrayList<>();
    private final List<GameObjectType> favorites;
    private final List<GameObject> requests;
    private final List<GameObject> rewards;
    private final String appearance;

    private boolean firstQuestDone = false;
    private boolean secondQuestDone = false;
    private boolean thirdQuestDone = false;

    private String firstQuestUser = "";
    private String secondQuestUser = "";
    private String thirdQuestUser = "";

    private final NPCCharacter character;

    public NPC(NpcDetails npcDetails) {
        this.location = npcDetails.getSpawnPoint();
        this.npcDetails = npcDetails;
        this.name = npcDetails.getName();

        this.favorites = npcDetails.getFavorites();
        this.requests = npcDetails.getRequests();
        this.rewards = npcDetails.getRewards();
        this.appearance = npcDetails.getAppearance();

        CharacterType characterType = npcDetails.getCharacterType();
        Vector2 spawnPoint = City.pointToWorld(npcDetails.getHomePoint());
        this.character = new NPCCharacter(characterType, spawnPoint, characterType.getName());
    }

    public NpcDetails getNpcDetails() {
        return npcDetails;
    }

    public boolean isNearPlayer(Point playerLocation)
    {
        int dx = Math.abs(location.getX() - playerLocation.getX());
        int dy = Math.abs(location.getY() - playerLocation.getY());

        return (dx <= 1 && dy <= 1) && !(dx == 0 && dy == 0);
    }

    public String talk()
    {
        Time currentTime = App.getCurrentGame().getCurrentTime();
        return this.getNpcDetails().getDialogue(currentTime.getSeason(), currentTime.getTimeOfDay());
    }

    public String getAppearance()
    {
        return appearance;
    }

    public String getName()
    {
        return name;
    }

    public boolean isFavorite(GameObjectType type)
    {
        return favorites.contains(type);
    }

    public boolean isFirstQuestAvailable()
    {
        return !firstQuestDone;
    }

    public boolean isSecondQuestAvailable()
    {
        if (!secondQuestDone)
        {
            Player player = App.getCurrentGame().getCurrentPlayer();
            FriendshipWithNpcData friendship = player.getNpcFriendship(this);
            return friendship.getLevel() >= 1;
        }
        return false;
    }

    public boolean isThirdQuestAvailable()
    {
        if(!thirdQuestDone)
        {
            Time time = App.getCurrentGame().getCurrentTime();
            return (time.getTotalDaysPassed() >= npcDetails.getDaysUntilQuestUnlocked());
        }
        return false;
    }

    public String getQuestDescription(int index)
    {
        StringBuilder output = new StringBuilder();

        GameObject request = npcDetails.getQuestRequest(index);
        output.append(" request: ").append(request.getObjectType().toString()).append(" x").append(request.getNumber()).append("\n");

        GameObject reward = npcDetails.getQuestReward(index);
        output.append(" reward: ").append(reward.getObjectType().toString()).append(" x").append(reward.getNumber()).append("\n");

        return output.toString();
    }

    public void firstQuestDone(String username)
    {
        firstQuestDone = true;
        firstQuestUser = username;
    }

    public void secondQuestDone(String username)
    {
        secondQuestDone = true;
        secondQuestUser = username;
    }

    public void thirdQuestDone(String username)
    {
        thirdQuestDone = true;
        thirdQuestUser = username;
    }

    public NPCCharacter getCharacter()
    {
        return character;
    }

    public Point getLocation()
    {
        Vector2 pos = character.getPosition();
        return App.getCurrentGame().getCity().worldToTile(pos.x, pos.y);
    }

    public String getFirstQuestUser()
    {
        return firstQuestUser;
    }

    public String getSecondQuestUser()
    {
        return secondQuestUser;
    }

    public String getThirdQuestUser()
    {
        return thirdQuestUser;
    }

    public void setFirstQuestDone(boolean firstQuestDone)
    {
        this.firstQuestDone = firstQuestDone;
    }

    public void setSecondQuestDone(boolean secondQuestDone)
    {
        this.secondQuestDone = secondQuestDone;
    }

    public void setThirdQuestDone(boolean thirdQuestDone)
    {
        this.thirdQuestDone = thirdQuestDone;
    }

    public void setFirstQuestUser(String firstQuestUser)
    {
        this.firstQuestUser = firstQuestUser;
    }

    public void setSecondQuestUser(String secondQuestUser)
    {
        this.secondQuestUser = secondQuestUser;
    }

    public void setThirdQuestUser(String thirdQuestUser)
    {
        this.thirdQuestUser = thirdQuestUser;
    }

    public void setLocation(Point location)
    {
        character.setPosition(City.tileToWorldCity(new Point(location.getX(), location.getY())));
    }
}
