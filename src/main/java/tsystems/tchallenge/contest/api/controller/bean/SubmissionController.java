package tsystems.tchallenge.contest.api.controller.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tsystems.tchallenge.contest.api.dto.IdAware;
import tsystems.tchallenge.contest.api.dto.SubmissionInvoice;
import tsystems.tchallenge.contest.api.dto.SubmissionResultDto;
import tsystems.tchallenge.contest.managers.contest.SubmissionResultManager;

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
