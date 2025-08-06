package ap.project.network.shared.DTO;

import ap.project.model.App.User;
import ap.project.model.enums.Gender;

public class UserDTO
{
    public boolean initialized = false;

    public String username;
    public String password;
    public String nickname;
    public String email;
    public Gender gender;
    public String question;
    public String answer;

    public int numberOfGames;
    public int hashID;

    public int characterChoice;
    public int mapChoice;

    public UserDTO() {}

    public UserDTO(User user)
    {
        if (user != null)
        {
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.nickname = user.getNickname();
            this.email = user.getEmail();
            this.gender = user.getGender();
            this.question = user.getQuestion();
            this.answer = user.getAnswer();

            this.numberOfGames = user.getNumberOfGames();
            this.hashID = user.getHashId();

            this.characterChoice = user.getCharacterChoice();
            this.mapChoice = user.getMapChoice();

            initialized = true;
        }
    }
}
