package ap.project.model.App;

public class TerminalEntry
{
    public final String prompt;
    public final String input;
    private final StringBuilder output;

    public TerminalEntry(String input)
    {
        this.prompt = "user@root:~$ ";
        this.input = input;
        this.output = new StringBuilder();
    }

    public TerminalEntry(String prompt, String input)
    {
        this.prompt = prompt;
        this.input = input;
        this.output = new StringBuilder();
    }

    public void appendOutput(String line)
    {
        output.append(line);
    }

    public void appendOutputLn(String line)
    {
        output.append(line).append("\n");
    }

    public String getOutput()
    {
        return output.toString();
    }
}

