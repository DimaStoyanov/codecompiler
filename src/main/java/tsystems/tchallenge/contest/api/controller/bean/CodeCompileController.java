package tsystems.tchallenge.contest.api.controller.bean;

import org.springframework.web.bind.annotation.*;
import tsystems.tchallenge.contest.api.controller.CompilationApi;
import tsystems.tchallenge.contest.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.contest.api.dto.CompileSubmissionInvoice;
import tsystems.tchallenge.contest.managers.compilation.CodeCompilationManager;

@RestController
@RequestMapping("compile-submissions/")
public class CodeCompileController implements CompilationApi {

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
