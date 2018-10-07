package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;

@Mapper(componentModel = "spring")
@Service
public interface CodeCompilationResultConverter {

    CodeCompilationResultDto toDto(CodeCompilationResult result);

}
