package ru.tsystems.tchallenge.codemaster.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.tsystems.tchallenge.codemaster.domain.models.RunResultEntity;

@Repository
public interface CodeRunResultRepository extends MongoRepository<RunResultEntity, String> {
}
