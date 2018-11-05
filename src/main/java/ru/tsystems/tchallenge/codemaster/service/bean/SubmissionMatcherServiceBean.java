package ru.tsystems.tchallenge.codemaster.service.bean;

import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.service.SubmissionMatcherService;

import java.util.Objects;

@Service
public class SubmissionMatcherServiceBean implements SubmissionMatcherService {
    public boolean matches(String expected, String actual) {
        return Objects.equals(expected, actual);
    }
}
