package ru.tsystems.tchallenge.contest.codemaster.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.SubmissionResultDto;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.SubmissionResult;

@Mapper(componentModel = "spring")
@Service
public interface SubmissionResultConverter {

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "languageName", ignore = true)
    @Mapping(target = "sourceCode", ignore = true)
    SubmissionResultDto toDto(SubmissionResult data);

}
