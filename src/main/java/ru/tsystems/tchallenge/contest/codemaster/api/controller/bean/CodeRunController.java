package ru.tsystems.tchallenge.contest.codemaster.api.controller.bean;

import org.springframework.web.bind.annotation.*;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CodeRunInvoice;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CodeRunResultDto;
import ru.tsystems.tchallenge.contest.codemaster.managers.running.CodeRunningManager;

@RestController
@RequestMapping("run-submissions/")
public class CodeRunController {


    private final CodeRunningManager codeRunningManager;

    public CodeRunController(CodeRunningManager codeRunningManager) {
        this.codeRunningManager = codeRunningManager;
    }

    @PostMapping
    public CodeRunResultDto runCode(@RequestBody CodeRunInvoice invoice) {
        return codeRunningManager.runCode(invoice);
    }

    @GetMapping("{id}")
    public CodeRunResultDto getRunCodeResult(@PathVariable String id) {
        return codeRunningManager.getRunCodeResult(id);
    }
}
