package ru.tsystems.tchallenge.codemaster.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionResultEntity;

public interface SubmissionResultRepository extends MongoRepository<SubmissionResultEntity, String> {
}
