package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunStatus;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionStatus;

@Service
@Mapper(componentModel = "spring")
public interface SubmissionStatusConverter {

    SubmissionStatus fromCodeRunStatus(CodeRunStatus status);
}
