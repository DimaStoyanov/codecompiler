package ru.tsystems.tchallenge.contest.codemaster.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CodeRunResultDto;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeRunResult;

@Mapper(componentModel = "spring")
@Service
public interface CodeRunResultConverter {

    @Mapping(target = "input", ignore = true)
    @Mapping(target = "output", ignore = true)
    CodeRunResultDto toDto(CodeRunResult result);
}
