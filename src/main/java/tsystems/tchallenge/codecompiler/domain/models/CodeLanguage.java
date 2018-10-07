package tsystems.tchallenge.codecompiler.domain.models;

public enum CodeLanguage {
    JAVA("java", "class");

    public final String ext;
    public final String compiledExt;

    CodeLanguage(String ext, String compiledExt) {
        this.ext = ext;
        this.compiledExt = compiledExt;

    }
}
