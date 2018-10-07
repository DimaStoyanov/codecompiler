package tsystems.tchallenge.codecompiler.api.controllers.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;

@RestController
@RequestMapping("languages/")
public class LanguageController {

    @GetMapping
    public CodeLanguage[] availableLanguages() {
        return CodeLanguage.values();
    }

}
