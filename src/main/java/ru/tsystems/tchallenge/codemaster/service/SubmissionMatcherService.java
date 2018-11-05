package ru.tsystems.tchallenge.codemaster.service;

import java.util.Objects;

public interface SubmissionMatcherService {
    /**
     * @param expected Expected program output
     * @param actual Test answer
     * @return true if actual value matches to expected value
     */
    boolean matches(String expected, String actual);
}
