package ru.tsystems.tchallenge.codemaster.service.bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.Contest;
import ru.tsystems.tchallenge.codemaster.domain.models.ContestEntity;
import ru.tsystems.tchallenge.codemaster.domain.repositories.ContestRepository;
import ru.tsystems.tchallenge.codemaster.service.ContestService;
import ru.tsystems.tchallenge.codemaster.service.converter.ContestConverter;

@Service
@RequiredArgsConstructor
@Log4j2
public class ContestServiceBean implements ContestService {

    private final ContestRepository contestRepository;
    private final ContestConverter contestConverter;

    @Override
    public Contest save(Contest invoice) {
        setDefaultIfMissing(invoice);
        ContestEntity contest = contestConverter.toEntity(invoice);
        ContestEntity contestEntity = contestRepository.save(contest);
        log.info("Create codemaster with id " + contest.getId());
        return contestConverter.toDto(contestEntity);
    }


    private void setDefaultIfMissing(Contest invoice) {
        if (invoice.getMemoryLimit() == null) {
            invoice.setMemoryLimit(256 * 1024 * 1024);
        }

        if (invoice.getTimeLimit() == null) {
            invoice.setTimeLimit(2_000);
        }
    }
}
