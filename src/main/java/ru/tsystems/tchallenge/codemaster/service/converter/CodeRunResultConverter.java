package ru.tsystems.tchallenge.codemaster.service.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.RunResult;
import ru.tsystems.tchallenge.codemaster.domain.models.RunResultEntity;

@Mapper(componentModel = "spring")
@Service
public interface CodeRunResultConverter {

    @Mapping(target = "input", ignore = true)
    @Mapping(target = "output", ignore = true)
    RunResult toDto(RunResultEntity result);
}
