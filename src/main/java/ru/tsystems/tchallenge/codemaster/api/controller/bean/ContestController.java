package ru.tsystems.tchallenge.codemaster.api.controller.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tsystems.tchallenge.codemaster.api.controller.ContestsApi;
import ru.tsystems.tchallenge.codemaster.api.model.Contest;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResultWithContest;
import ru.tsystems.tchallenge.codemaster.service.ContestService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ContestController extends GenericApiControllerBean implements ContestsApi {

    private final ContestService contestService;

    @Override
    public ResponseEntity<OperationResultWithContest> createContest(@Valid @RequestBody  Contest body) {
        return created()
                .content(contestService.save(body))
                .type(OperationResultWithContest.class)
                .response();
    }

}
