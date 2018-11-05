package ru.tsystems.tchallenge.codemaster.api.controller.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tsystems.tchallenge.codemaster.api.controller.CompilationApi;
import ru.tsystems.tchallenge.codemaster.api.model.CompileInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.CompileResult;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResultWithCompileResult;
import ru.tsystems.tchallenge.codemaster.service.CodeCompilationService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class CodeCompileController extends GenericApiControllerBean implements CompilationApi {

    private final CodeCompilationService codeCompilationService;

    @Override
    public ResponseEntity<OperationResultWithCompileResult> createCompileTask(@RequestBody @Valid  CompileInvoice body) {
        return created()
                .content(codeCompilationService.compileFile(body))
                .type(OperationResultWithCompileResult.class)
                .response();
    }

    @Override
    public ResponseEntity<OperationResultWithCompileResult> getCompileResult(@PathVariable  String id) {
        return retrieved()
                .content(codeCompilationService.getCompilationResult(id))
                .type(OperationResultWithCompileResult.class)
                .response();
    }
}
