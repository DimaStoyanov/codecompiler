package tsystems.tchallenge.contest.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.contest.domain.models.CodeCompilationResult;

@Mapper(componentModel = "spring")
@Service
public interface CodeCompilationResultConverter {

    @Mapping(source = "language.name", target = "languageName")
    CodeCompilationResultDto toDto(CodeCompilationResult result);

}
