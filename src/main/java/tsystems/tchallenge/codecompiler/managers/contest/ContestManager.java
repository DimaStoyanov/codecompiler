package tsystems.tchallenge.codecompiler.managers.contest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.ContestInvoice;
import tsystems.tchallenge.codecompiler.api.dto.IdAware;
import tsystems.tchallenge.codecompiler.converters.ContestConverter;
import tsystems.tchallenge.codecompiler.domain.models.Contest;
import tsystems.tchallenge.codecompiler.domain.repositories.ContestRepository;

@Service
public class ContestManager {

    private final ContestRepository contestRepository;
    private final ContestConverter contestConverter;

    @Autowired
    public ContestManager(ContestRepository contestRepository, ContestConverter contestConverter) {
        this.contestRepository = contestRepository;
        this.contestConverter = contestConverter;
    }

    public IdAware save(ContestInvoice invoice) {
        invoice.validate();
        Contest contest = contestConverter.toContest(invoice);
        contestRepository.save(contest);
        return contest.justId();
    }
}
