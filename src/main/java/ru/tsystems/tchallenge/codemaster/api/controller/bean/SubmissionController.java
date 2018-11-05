package ru.tsystems.tchallenge.codemaster.api.controller.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tsystems.tchallenge.codemaster.api.controller.SubmissionsApi;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResultWithSubmissionResult;
import ru.tsystems.tchallenge.codemaster.api.model.SubmissionInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.SubmissionResult;
import ru.tsystems.tchallenge.codemaster.service.SubmissionResultService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SubmissionController extends GenericApiControllerBean implements SubmissionsApi {

    private final SubmissionResultService submissionResultService;

    @Override
    public ResponseEntity<OperationResultWithSubmissionResult> createSubmission(@Valid @RequestBody SubmissionInvoice body) {
        return created()
                .content(submissionResultService.createSubmission(body))
                .type(OperationResultWithSubmissionResult.class)
                .response();
    }


    @Override
    public ResponseEntity<OperationResultWithSubmissionResult> getSubmissionResult(
            @PathVariable String id,
            @Valid @RequestParam(value = "withLang", required = false) Boolean withLang,
            @Valid @RequestParam(value = "withSource", required = false) Boolean withSource) {
        withLang = withLang == null ? false : withLang;
        withSource = withSource == null ? false : withSource;
        return retrieved()
                .content(submissionResultService
                        .getSubmission(id, withLang, withSource))
                .type(OperationResultWithSubmissionResult.class)
                .response();
    }

}
