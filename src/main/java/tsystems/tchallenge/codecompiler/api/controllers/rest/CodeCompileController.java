package tsystems.tchallenge.codecompiler.api.controllers.rest;

import org.springframework.web.bind.annotation.*;
import tsystems.tchallenge.codecompiler.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.codecompiler.api.dto.CompileSubmissionInvoice;
import tsystems.tchallenge.codecompiler.managers.compilation.CodeCompilationManager;

@RestController
@RequestMapping("compile-submissions/")
public class CodeCompileController {

    private final CodeCompilationManager codeCompilationManager;

    public CodeCompileController(CodeCompilationManager codeCompilationManager) {
        this.codeCompilationManager = codeCompilationManager;
    }

    @PostMapping
    public CodeCompilationResultDto compileCode(@RequestBody CompileSubmissionInvoice invoice) {
        return codeCompilationManager.compileFile(invoice);
    }

    @GetMapping("{id}")
    public CodeCompilationResultDto getCompilationResult(@PathVariable String id) {
        return codeCompilationManager.getCompilationResult(id);
    }


}
