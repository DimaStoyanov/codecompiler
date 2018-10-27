package tsystems.tchallenge.codecompiler.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;

public interface CodeCompilationResultRepository extends MongoRepository<CodeCompilationResult, String> {
}
