package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunResult;

@Mapper(componentModel = "spring")
@Service
public interface CodeRunResultConverter {

    @Mapping(target = "input", ignore = true)
    @Mapping(target = "output", ignore = true)
    CodeRunResultDto toDto(CodeRunResult result);
}
