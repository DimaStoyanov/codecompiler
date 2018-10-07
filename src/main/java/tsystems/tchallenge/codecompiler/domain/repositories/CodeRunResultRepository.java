package tsystems.tchallenge.codecompiler.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunResult;

@Service
public interface CodeRunResultRepository extends MongoRepository<CodeRunResult, String> {
}
