package tsystems.tchallenge.codecompiler.converters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.ContestInvoice;
import tsystems.tchallenge.codecompiler.api.dto.TestInvoice;
import tsystems.tchallenge.codecompiler.domain.models.Contest;
import tsystems.tchallenge.codecompiler.domain.models.Test;

@Mapper(componentModel = "spring")
@Service
public interface ContestConverter {

    @Mapping(target = "id", ignore = true)
    Contest toContest(ContestInvoice invoice);

    Test toTest(TestInvoice invoice);
}
