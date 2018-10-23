package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionResultDto;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionResult;

@Mapper(componentModel = "spring")
@Service
public interface SubmissionResultConverter {

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "languageName", ignore = true)
    @Mapping(target = "sourceCode", ignore = true)
    SubmissionResultDto toDto(SubmissionResult data);

}
