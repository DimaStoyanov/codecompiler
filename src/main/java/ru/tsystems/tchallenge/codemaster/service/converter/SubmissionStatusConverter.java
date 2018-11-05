package ru.tsystems.tchallenge.codemaster.service.converter;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.RunStatus;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionStatus;

@Service
@Mapper(componentModel = "spring")
public interface SubmissionStatusConverter {

    SubmissionStatus fromCodeRunStatus(RunStatus status);

}