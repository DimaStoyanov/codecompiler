package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionResultDto;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionResult;

@Mapper(componentModel = "spring")
@Service
public interface SubmissionResultConverter {

    SubmissionResultDto toDto(SubmissionResult data);
}
