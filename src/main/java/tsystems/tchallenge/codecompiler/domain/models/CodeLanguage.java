package tsystems.tchallenge.codecompiler.domain.models;

public enum CodeLanguage {
    JAVA("java", "class", "Java 8u171",
            "Submission should be in 1 file (1 public class).\n" +
            "Public class should have name \"Main\".\n" +
            "No external dependencies (via maven, gradle, e.t.c) supported (at least now)");

    public final String ext;
    public final String compiledExt;
    public final String name;
    public final String notes;

    CodeLanguage(String ext, String compiledExt, String name, String notes) {
        this.ext = ext;
        this.compiledExt = compiledExt;
        this.name = name;
        this.notes = notes;
    }
}
