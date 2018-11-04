package tsystems.tchallenge.contest.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tsystems.tchallenge.contest.domain.models.CodeRunResult;

@Repository
public interface CodeRunResultRepository extends MongoRepository<CodeRunResult, String> {
}
