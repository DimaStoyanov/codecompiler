package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunResult;

@Mapper(componentModel = "spring")
@Service
public interface CodeRunResultConverter {

    CodeRunResultDto toDto(CodeRunResult result);
}
