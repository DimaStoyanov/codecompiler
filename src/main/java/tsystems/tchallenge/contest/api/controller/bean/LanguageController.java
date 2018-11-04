package tsystems.tchallenge.contest.api.controller.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsystems.tchallenge.contest.api.dto.CodeLanguageInfo;
import tsystems.tchallenge.contest.converters.CodeLanguageConverter;
import tsystems.tchallenge.contest.domain.models.CodeLanguage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("languages/")
public class LanguageController {

    private final CodeLanguageConverter codeLanguageConverter;

    @Autowired
    public LanguageController(CodeLanguageConverter codeLanguageConverter) {
        this.codeLanguageConverter = codeLanguageConverter;
    }

    @GetMapping
    public List<CodeLanguageInfo> availableLanguages() {
        return Arrays.stream(CodeLanguage.values())
                .map(codeLanguageConverter::toInfo)
                .collect(Collectors.toList());
    }

}
