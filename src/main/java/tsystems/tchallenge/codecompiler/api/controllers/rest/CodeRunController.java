package tsystems.tchallenge.codecompiler.api.controllers.rest;

import org.springframework.web.bind.annotation.*;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunInvoice;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.managers.running.CodeRunningManager;

@RestController
@RequestMapping("submissions/")
public class CodeRunController {


    private final CodeRunningManager codeRunningManager;

    public CodeRunController(CodeRunningManager codeRunningManager) {
        this.codeRunningManager = codeRunningManager;
    }

    @PostMapping
    public CodeRunResultDto runCode(CodeRunInvoice invoice) {
        return codeRunningManager.runCode(invoice);
    }

    @GetMapping("{id}")
    public CodeRunResultDto getRunCodeResult(@PathVariable String id) {
        return codeRunningManager.getRunCodeResult(id);
    }
}
