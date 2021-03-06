package ru.tsystems.tchallenge.codemaster.domain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.tsystems.tchallenge.codemaster.domain.models.CompileResultEntity;

public interface CodeCompilationResultRepository extends MongoRepository<CompileResultEntity, String> {
}
