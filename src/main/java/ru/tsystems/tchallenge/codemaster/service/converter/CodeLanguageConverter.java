package ru.tsystems.tchallenge.codemaster.service.converter;

import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.Language;
import ru.tsystems.tchallenge.codemaster.api.model.LanguageInfo;
import ru.tsystems.tchallenge.codemaster.domain.models.CodeLanguage;

@Service
public class CodeLanguageConverter {
    public LanguageInfo toInfo(CodeLanguage lang) {
        return new LanguageInfo()
                .language(Language.valueOf(lang.toString()))
                .name(lang.name)
                .notes(lang.notes);
    }


}
