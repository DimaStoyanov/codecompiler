package tsystems.tchallenge.contest.api.controller.bean;

import org.springframework.web.bind.annotation.*;
import tsystems.tchallenge.contest.api.dto.CodeRunInvoice;
import tsystems.tchallenge.contest.api.dto.CodeRunResultDto;
import tsystems.tchallenge.contest.managers.running.CodeRunningManager;

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
