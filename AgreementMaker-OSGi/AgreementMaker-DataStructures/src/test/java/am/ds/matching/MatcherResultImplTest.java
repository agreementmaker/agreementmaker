package am.ds.matching;

import am.api.matching.MatcherResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class MatcherResultImplTest {
    @Test
    public void should_accept_null() {
        MatcherResult result = new MatcherResultImpl(null, null, null);
        assertFalse(result.getClasses().isPresent());
        assertFalse(result.getProperties().isPresent());
        assertFalse(result.getInstances().isPresent());
    }
}