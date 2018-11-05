package ru.tsystems.tchallenge.codemaster.service.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.SubmissionResult;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionResultEntity;

@Mapper(componentModel = "spring")
@Service
public interface SubmissionResultConverter {

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "languageName", ignore = true)
    @Mapping(target = "sourceCode", ignore = true)
    SubmissionResult toDto(SubmissionResultEntity data);

}
