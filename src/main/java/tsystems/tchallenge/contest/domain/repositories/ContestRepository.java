package tsystems.tchallenge.contest.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tsystems.tchallenge.contest.domain.models.Contest;

public interface  ContestRepository extends MongoRepository<Contest, String> {
}
