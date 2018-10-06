package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DockerCompilationResult {
    private String stdout;
    private String stderr;
    private Long exitCode;

}
