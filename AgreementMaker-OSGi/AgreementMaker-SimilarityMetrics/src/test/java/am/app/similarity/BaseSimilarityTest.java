package am.app.similarity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BaseSimilarityTest {
    private BaseSimilarity bsim;

    private void assertEq(double expected, double actual) {
        assertEquals(expected, actual, 0.000_000_001);
    }

    @Before
    public void setUp() {
        bsim = new BaseSimilarity();
    }

    @Test
    public void first() {
        assertEq(1.0, bsim.getSimilarity("a","a"));
    }
}