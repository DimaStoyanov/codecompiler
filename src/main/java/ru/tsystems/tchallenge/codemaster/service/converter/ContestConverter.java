package ru.tsystems.tchallenge.codemaster.service.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.Contest;
import ru.tsystems.tchallenge.codemaster.domain.models.ContestEntity;

@Mapper(componentModel = "spring")
@Service
public interface ContestConverter {

    @Mapping(target = "id", ignore = true)
    ContestEntity toEntity(Contest invoice);

    Contest toDto(ContestEntity entity);

}
