package tsystems.tchallenge.contest.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tsystems.tchallenge.contest.domain.models.CodeCompilationResult;

public interface CodeCompilationResultRepository extends MongoRepository<CodeCompilationResult, String> {
}
