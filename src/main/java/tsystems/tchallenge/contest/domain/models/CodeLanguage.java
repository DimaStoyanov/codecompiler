package tsystems.tchallenge.contest.domain.models;

public enum CodeLanguage {
    JAVA("Main", "java", "class", "Java 11.0.1",
            "Submission should be in 1 file (1 public class).\n" +
            "Public class should have name \"Main\".\n" +
                    "Main class must have main method with signature " +
                    "\"public static void main(String[] args)\" " +
            "No external dependencies (via maven, gradle, e.t.c) supported (at least now)");

    public final String ext;
    public final String fileBaseName;
    public final String compiledExt;
    public final String name;
    public final String notes;

    CodeLanguage(String fileBaseName, String ext, String compiledExt, String name, String notes) {
        this.ext = ext;
        this.compiledExt = compiledExt;
        this.name = name;
        this.notes = notes;
        this.fileBaseName = fileBaseName;
    }

    public String sourceFileName() {
        return fileBaseName + "." + ext;
    }

}
