package ru.tsystems.tchallenge.contest.codemaster.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.SubmissionResult;

public interface SubmissionResultRepository extends MongoRepository<SubmissionResult, String> {
}
