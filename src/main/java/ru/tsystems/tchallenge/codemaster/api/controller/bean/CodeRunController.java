package ru.tsystems.tchallenge.codemaster.api.controller.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tsystems.tchallenge.codemaster.api.controller.RunApi;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResultWithRunResult;
import ru.tsystems.tchallenge.codemaster.api.model.RunInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.RunResult;
import ru.tsystems.tchallenge.codemaster.service.CodeRunService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class CodeRunController extends GenericApiControllerBean implements RunApi {
    private final CodeRunService codeRunningManager;

    @Override
    public ResponseEntity<OperationResultWithRunResult> createRunTask(@Valid @RequestBody RunInvoice body) {
        return created()
                .content(codeRunningManager.runCode(body))
                .type(OperationResultWithRunResult.class)
                .response();
    }

    @Override
    public ResponseEntity<OperationResultWithRunResult> getRunResult(@PathVariable String id) {
        return retrieved()
                .content(codeRunningManager.getResult(id))
                .type(OperationResultWithRunResult.class)
                .response();
    }

}
