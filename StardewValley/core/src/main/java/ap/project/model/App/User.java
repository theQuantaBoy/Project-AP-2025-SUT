package ap.project.model.App;

import ap.project.model.game.Game;
import ap.project.model.enums.Gender;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class User
{
    private String username;
    private String password;
    private String nickname;
    private String email;
    private Gender gender;
    private String question;
    private String answer;

    private Texture avatar;

    private int numberOfGames = 0;
    private Game currentGame = null;
    private ArrayList<Integer> gameMoney = new ArrayList<>();

    private final int HASH_DIGIT_COUNT = 6;
    private final int hashId;

    private int characterChoice = 0;
    private int mapChoice = 0;

    public User(String username, String nickname, int id, int avatarChoice, int mapChoice)
    {
        this.username = username;
        this.nickname = nickname;
        this.hashId = id;
        this.characterChoice = avatarChoice;
        this.mapChoice = mapChoice;
    }

    public User(String username, String nickname, Gender gender, int hashId)
    {
        this.username = username;
        this.nickname = nickname;
        this.gender = gender;
        this.hashId = hashId;
    }

    public User(String name)
    {
        this.username = name;
        this.nickname = name;

        if (Math.random() < 0.5)
        {
            this.gender = Gender.MALE;
        } else
        {
            this.gender = Gender.FEMALE;
        }

        this.hashId = SHA256Hasher.randomizedHashInt(name,  HASH_DIGIT_COUNT);
    }

    public User(String username, String password, String nickname, String email, Gender gender, String secQ, String secA) {
        this.username = username;
        this.password = SHA256Hasher.hash(password);
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.question = secQ;
        this.answer = secA;
        this.avatar = getRandomAvatar();

        this.hashId = SHA256Hasher.randomizedHashInt(username,  HASH_DIGIT_COUNT);
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getNumberOfGames()
    {
        return numberOfGames;
    }

    public boolean hasCurrentGame()
    {
        return (currentGame != null);
    }

    public ArrayList<Integer> getGameMoney()
    {
        return gameMoney;
    }

    public void setCurrentGame(Game game)
    {
        this.currentGame = game;
    }

    public void addToNumberOfGames()
    {
        this.numberOfGames += 1;
    }

    public void addMoneyGame(int amount)
    {
        this.gameMoney.add(amount);
    }

    public int getMaxMoney()
    {
        if (gameMoney.isEmpty())
        {
            return 0;
        }

        return Collections.max(gameMoney);
    }

    public Game getCurrentGame()
    {
        return currentGame;
    }

    private Texture getRandomAvatar() {
        return GameAssetsManager.getGameAssetsManager().getAvatars().random().texture;
    }

    public Texture getAvatar() {
        return avatar;
    }

    public void setAvatar(Texture avatar) {
        this.avatar = avatar;
    }

    public int getHashId()
    {
        return hashId;
    }

    public int getCharacterChoice()
    {
        return characterChoice;
    }

    public int getMapChoice()
    {
        return mapChoice;
    }

    public void setCharacterChoice(int characterChoice)
    {
        this.characterChoice = characterChoice;
    }

    public void setMapChoice(int mapChoice)
    {
        this.mapChoice = mapChoice;
    }

    public User(String username, String password, String nickname, String email, Gender gender, String question, String answer, int numberOfGames, int hashId, int characterChoice, int mapChoice)
    {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.question = question;
        this.answer = answer;
        this.numberOfGames = numberOfGames;
        this.hashId = hashId;
        this.characterChoice = characterChoice;
        this.mapChoice = mapChoice;
    }
}
