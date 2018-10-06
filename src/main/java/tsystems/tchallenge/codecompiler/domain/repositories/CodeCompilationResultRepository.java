package tsystems.tchallenge.codecompiler.domain.repositories;

import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CodeCompilationResultRepository extends MongoRepository<CodeCompilationResult, String> {
}
