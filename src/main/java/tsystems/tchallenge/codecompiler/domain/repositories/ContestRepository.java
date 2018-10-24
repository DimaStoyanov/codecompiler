package tsystems.tchallenge.codecompiler.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tsystems.tchallenge.codecompiler.domain.models.Contest;

public interface  ContestRepository extends MongoRepository<Contest, String> {
}
