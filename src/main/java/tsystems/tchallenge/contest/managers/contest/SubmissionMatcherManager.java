package tsystems.tchallenge.contest.managers.contest;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SubmissionMatcherManager {

    /**
     * There can be more complex matcher
     * @param expected Expected program output
     * @param actual Test answer
     * @return true if actual value matches to expected value
     */
    public boolean matches(String expected, String actual) {
        return Objects.equals(expected, actual);
    }
}
