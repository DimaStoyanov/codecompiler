package ru.tsystems.tchallenge.contest.codemaster.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.ContestInvoice;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.TestInvoice;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.Contest;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.Test;

@Mapper(componentModel = "spring")
@Service
public interface ContestConverter {

    @Mapping(target = "id", ignore = true)
    Contest toContest(ContestInvoice invoice);

    Test toTest(TestInvoice invoice);
}
