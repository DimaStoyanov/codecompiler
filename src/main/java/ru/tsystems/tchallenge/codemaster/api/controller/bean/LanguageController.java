package ru.tsystems.tchallenge.codemaster.api.controller.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.tsystems.tchallenge.codemaster.api.controller.LanguagesApi;
import ru.tsystems.tchallenge.codemaster.api.model.LanguageInfoList;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResultWithLanguageInfoList;
import ru.tsystems.tchallenge.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.codemaster.service.converter.CodeLanguageConverter;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class LanguageController extends GenericApiControllerBean implements LanguagesApi {
    private final CodeLanguageConverter codeLanguageConverter;

    @Override
    public ResponseEntity<OperationResultWithLanguageInfoList> getAvailableLanguages() {
        var languages = Arrays.stream(CodeLanguage.values())
                .map(codeLanguageConverter::toInfo)
                .collect(LanguageInfoList::new, ArrayList::add, ArrayList::addAll);
        return retrieved()
                .content(languages)
                .type(OperationResultWithLanguageInfoList.class)
                .response();
    }


}
