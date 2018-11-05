package ru.tsystems.tchallenge.contest.codemaster.converters;

import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CodeLanguageInfo;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;

@Service
public class CodeLanguageConverter {
    public CodeLanguageInfo toInfo(CodeLanguage lang) {
        return CodeLanguageInfo.builder()
                .language(lang)
                .languageName(lang.name)
                .sourceFileExt(lang.ext)
                .compiledFileExt(lang.compiledExt)
                .notes(lang.notes)
                .build();
    }


}
