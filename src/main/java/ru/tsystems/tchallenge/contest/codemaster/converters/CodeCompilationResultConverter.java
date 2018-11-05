package ru.tsystems.tchallenge.contest.codemaster.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CodeCompilationResultDto;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeCompilationResult;

@Mapper(componentModel = "spring")
@Service
public interface CodeCompilationResultConverter {

    @Mapping(source = "language.name", target = "languageName")
    CodeCompilationResultDto toDto(CodeCompilationResult result);

}
