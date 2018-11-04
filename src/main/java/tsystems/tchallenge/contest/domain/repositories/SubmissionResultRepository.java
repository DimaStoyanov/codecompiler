package tsystems.tchallenge.contest.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tsystems.tchallenge.contest.domain.models.SubmissionResult;

public interface SubmissionResultRepository extends MongoRepository<SubmissionResult, String> {
}
