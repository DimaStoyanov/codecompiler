package ru.tsystems.tchallenge.contest.codemaster.api.controller.bean;

import org.springframework.web.bind.annotation.*;
import ru.tsystems.tchallenge.contest.codemaster.api.controller.CompilationApi;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CodeCompilationResultDto;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CompileSubmissionInvoice;
import ru.tsystems.tchallenge.contest.codemaster.managers.compilation.CodeCompilationManager;

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
