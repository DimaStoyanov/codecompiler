package ru.tsystems.tchallenge.contest.codemaster.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeCompilationResult;

public interface CodeCompilationResultRepository extends MongoRepository<CodeCompilationResult, String> {
}
