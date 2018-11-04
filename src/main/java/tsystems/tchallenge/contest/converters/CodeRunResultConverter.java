package tsystems.tchallenge.contest.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.api.dto.CodeRunResultDto;
import tsystems.tchallenge.contest.domain.models.CodeRunResult;

@Mapper(componentModel = "spring")
@Service
public interface CodeRunResultConverter {

    @Mapping(target = "input", ignore = true)
    @Mapping(target = "output", ignore = true)
    CodeRunResultDto toDto(CodeRunResult result);
}
