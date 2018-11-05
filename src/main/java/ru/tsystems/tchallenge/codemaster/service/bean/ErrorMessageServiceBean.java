package ru.tsystems.tchallenge.codemaster.service.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.service.ErrorMessageService;

import java.util.Locale;

@RequiredArgsConstructor
@Service
public class ErrorMessageServiceBean implements ErrorMessageService {
    private final MessageSource messageSource;

    public String getLocalizedMessage (String messageKey) {

        return messageSource.getMessage(messageKey, null,
                Locale.getDefault());
    }
}
