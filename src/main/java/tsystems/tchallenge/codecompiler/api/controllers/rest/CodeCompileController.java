package tsystems.tchallenge.codecompiler.api.controllers.rest;

import org.springframework.web.bind.annotation.*;
import tsystems.tchallenge.codecompiler.api.dto.CodeSubmissionInvoice;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;
import tsystems.tchallenge.codecompiler.managers.compilation.CodeCompilationManager;

@RestController
@RequestMapping("compile-submissions/")
public class CodeCompileController {

    private final CodeCompilationManager codeCompilationManager;

    public CodeCompileController(CodeCompilationManager codeCompilationManager) {
        this.codeCompilationManager = codeCompilationManager;
    }

    // TODO: change from collection to dto
    @PostMapping
    public CodeCompilationResult compileCode(@RequestBody CodeSubmissionInvoice invoice) {
        return codeCompilationManager.compileFile(invoice);
    }

    @GetMapping("{id}")
    public CodeCompilationResult getCompilationResult(@PathVariable String id) {
        return codeCompilationManager.getCompilationResult(id);
    }


}
