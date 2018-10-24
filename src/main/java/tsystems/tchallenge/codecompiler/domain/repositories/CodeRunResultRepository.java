package tsystems.tchallenge.codecompiler.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunResult;

@Repository
public interface CodeRunResultRepository extends MongoRepository<CodeRunResult, String> {
}
