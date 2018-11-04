package tsystems.tchallenge.contest.converters;

import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.api.dto.CodeLanguageInfo;
import tsystems.tchallenge.contest.domain.models.CodeLanguage;

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
