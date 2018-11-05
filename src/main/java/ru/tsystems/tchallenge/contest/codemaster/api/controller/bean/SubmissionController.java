package ru.tsystems.tchallenge.contest.codemaster.api.controller.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.IdAware;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.SubmissionInvoice;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.SubmissionResultDto;
import ru.tsystems.tchallenge.contest.codemaster.managers.contest.SubmissionResultManager;

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
