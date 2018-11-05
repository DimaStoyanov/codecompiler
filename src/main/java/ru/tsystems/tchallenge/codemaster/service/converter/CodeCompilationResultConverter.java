package ru.tsystems.tchallenge.codemaster.service.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.CompileResult;
import ru.tsystems.tchallenge.codemaster.domain.models.CompileResultEntity;

@Mapper(componentModel = "spring")
@Service
public interface CodeCompilationResultConverter {
    @Mapping(source = "language.name", target = "languageName")
    CompileResult toDto(CompileResultEntity result);
}

