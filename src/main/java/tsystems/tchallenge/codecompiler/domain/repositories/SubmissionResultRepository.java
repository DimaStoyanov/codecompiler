package tsystems.tchallenge.codecompiler.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionResult;

public interface SubmissionResultRepository extends MongoRepository<SubmissionResult, String> {
}
