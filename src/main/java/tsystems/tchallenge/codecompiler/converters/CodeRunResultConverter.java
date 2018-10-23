package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder.internal;

@Mapper(componentModel = "spring")
@Service
public interface CodeRunResultConverter {

    @Mapping(target = "input", ignore = true)
    @Mapping(target = "output", ignore = true)
    CodeRunResultDto toDto(CodeRunResult result);

    @AfterMapping
    default void readFiles(CodeRunResult from, @MappingTarget CodeRunResultDto to) {
        to.setInput(readFile(from.getInputPath()));
        to.setOutput(readFile(from.getOutputPath()));
    }

    default String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw internal(e);
        }
    }
}
