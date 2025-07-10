package ap.project.model.App;

public record Result(boolean isSuccessful, String message)
{
    @Override
    public String toString()
    {
        return message;
    }
}
