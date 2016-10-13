package am.app.similarity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubstringSimTest {
    private SubstringSim metric;

    @Before
    public void setUp() {
        metric = new SubstringSim();
    }

    @Test(expected = NullPointerException.class)
    public void does_not_accept_null() {
        metric.getSimilarity(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void does_not_accept_null_left_side() {
        metric.getSimilarity(null, "");
    }

    @Test(expected = NullPointerException.class)
    public void does_not_accept_null_right_side() {
        metric.getSimilarity("", null);
    }

    @Test
    public void equal_strings_have_exact_similarity() {
        assertEquals(1d, metric.getSimilarity("", ""), 0.000_000_001);
        assertEquals(1d, metric.getSimilarity("abcdefg", "abcdefg"), 0.000_000_001);
        assertEquals(1d, metric.getSimilarity("12345", "12345"), 0.000_000_001);
    }
}