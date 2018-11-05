package ru.tsystems.tchallenge.contest.codemaster.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeRunResult;

@Repository
public interface CodeRunResultRepository extends MongoRepository<CodeRunResult, String> {
}
