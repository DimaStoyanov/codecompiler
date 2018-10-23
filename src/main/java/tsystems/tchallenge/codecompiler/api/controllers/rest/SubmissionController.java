package tsystems.tchallenge.codecompiler.api.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tsystems.tchallenge.codecompiler.api.dto.IdAware;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionInvoice;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionResultDto;
import tsystems.tchallenge.codecompiler.managers.contest.SubmissionResultManager;

@RestController
@RequestMapping("/submissions/")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionResultManager submissionManager;

    @PostMapping
    public IdAware createSubmission(@RequestBody SubmissionInvoice invoice) {
        return submissionManager.createSubmission(invoice);
    }

    @GetMapping("{id}")
    public SubmissionResultDto getSubmission(@PathVariable String id,
                                             @RequestParam(value = "withLang", defaultValue = "true")
                                                     Boolean withLang,
                                             @RequestParam(value = "withSource", defaultValue = "false")
                                                     Boolean withSource) {
        return submissionManager.getResult(id, withLang, withSource);
    }
}
