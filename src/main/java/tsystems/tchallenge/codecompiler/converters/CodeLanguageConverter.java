package tsystems.tchallenge.codecompiler.converters;

import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeLanguageInfo;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;

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
