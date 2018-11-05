package ru.tsystems.tchallenge.codemaster.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.tsystems.tchallenge.codemaster.domain.models.ContestEntity;

public interface  ContestRepository extends MongoRepository<ContestEntity, String> {
}
