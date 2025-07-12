package ap.project.model.enums;

public enum SecurityQuestionType
{
    ANIMAL("What is your favorite animal?"),
    GAME("What is your favorite game?"),
    TA("Who is your least favorite TA?"),
    ;

    private final String question;

    SecurityQuestionType(String question)
    {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

}
