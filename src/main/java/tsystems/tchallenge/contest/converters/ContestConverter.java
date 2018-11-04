package tsystems.tchallenge.contest.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.api.dto.ContestInvoice;
import tsystems.tchallenge.contest.api.dto.TestInvoice;
import tsystems.tchallenge.contest.domain.models.Contest;
import tsystems.tchallenge.contest.domain.models.Test;

@Mapper(componentModel = "spring")
@Service
public interface ContestConverter {

    @Mapping(target = "id", ignore = true)
    Contest toContest(ContestInvoice invoice);

    Test toTest(TestInvoice invoice);
}
