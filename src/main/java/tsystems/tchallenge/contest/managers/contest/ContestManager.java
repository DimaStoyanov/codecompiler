package tsystems.tchallenge.contest.managers.contest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.api.dto.ContestInvoice;
import tsystems.tchallenge.contest.api.dto.IdAware;
import tsystems.tchallenge.contest.converters.ContestConverter;
import tsystems.tchallenge.contest.domain.models.Contest;
import tsystems.tchallenge.contest.domain.repositories.ContestRepository;

@Service
@Log4j2
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
        setDefaultIfMissing(invoice);
        Contest contest = contestConverter.toContest(invoice);
        contestRepository.save(contest);
        log.info("Create contest with id "  + contest.getId());
        return contest.justId();
    }

    private void setDefaultIfMissing(ContestInvoice invoice) {
        if (invoice.getMemoryLimit() == null) {
            invoice.setMemoryLimit(256 * 1024 * 1024L);
        }

        if (invoice.getTimeLimit() == null) {
            invoice.setTimeLimit(2_000L);
        }
    }
}
