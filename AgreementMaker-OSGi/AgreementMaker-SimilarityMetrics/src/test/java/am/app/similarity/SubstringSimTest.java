package am.app.similarity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubstringSimTest {
    private static final double PRECISION = 0.000_000_001;
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
        assertEquals(1d, metric.getSimilarity("", ""), PRECISION);
        assertEquals(1d, metric.getSimilarity("abcdefg", "abcdefg"), PRECISION);
        assertEquals(1d, metric.getSimilarity("12345", "12345"), PRECISION);
    }

    @Test
    public void exclude_range() {
        String input = "aninputstring";
        assertEquals("astring", SubstringSim.excludeRange(input, 1, 7));
    }

    @Test
    public void no_substring() {
        assertEquals(0d, metric.getSimilarity("a", ""), PRECISION);
        assertEquals(0d, metric.getSimilarity("", "b"), PRECISION);
        assertEquals(0d, metric.getSimilarity("a", "b"), PRECISION);
        assertEquals(0d, metric.getSimilarity("a", "bcdefghijk"), PRECISION);
        assertEquals(0d, metric.getSimilarity("abcdefghijk", "l"), PRECISION);
        assertEquals(0d, metric.getSimilarity("abcdefghijk", "lmnopqrst"), PRECISION);
    }

    @Test
    public void single_substring() {
        assertEquals(12d/14, metric.getSimilarity("astring", "bstring"), PRECISION);
        assertEquals(12d/14, metric.getSimilarity("stringa", "bstring"), PRECISION);
    }

    @Test
    public void single_character_is_not_considered_a_substring_match() {
        assertEquals(0d, metric.getSimilarity("a", "astring"), PRECISION);
        assertEquals(0d, metric.getSimilarity("a", "straing"), PRECISION);
        assertEquals(0d, metric.getSimilarity("a", "stringa"), PRECISION);
        assertEquals(0d, metric.getSimilarity("astring", "a"), PRECISION);
        assertEquals(0d, metric.getSimilarity("straing", "a"), PRECISION);
        assertEquals(0d, metric.getSimilarity("stringa", "a"), PRECISION);
    }

    @Test
    public void two_character_substring_is_not_considered_a_substring_match() {
        assertEquals(0d, metric.getSimilarity("as", "astring"), PRECISION);
        assertEquals(0d, metric.getSimilarity("as", "tringas"), PRECISION);
        assertEquals(0d, metric.getSimilarity("as", "triasng"), PRECISION);
        assertEquals(0d, metric.getSimilarity("astring", "as"), PRECISION);
        assertEquals(0d, metric.getSimilarity("tringas", "as"), PRECISION);
        assertEquals(0d, metric.getSimilarity("triasng", "as"), PRECISION);
    }

    @Test
    public void three_character_substring() {
        assertEquals(6d/10, metric.getSimilarity("ast", "astring"), PRECISION);
        assertEquals(6d/10, metric.getSimilarity("ast", "ringast"), PRECISION);
        assertEquals(6d/10, metric.getSimilarity("ast", "riastng"), PRECISION);
        assertEquals(6d/10, metric.getSimilarity("astring", "ast"), PRECISION);
        assertEquals(6d/10, metric.getSimilarity("ringast", "ast"), PRECISION);
        assertEquals(6d/10, metric.getSimilarity("riastng", "ast"), PRECISION);

        assertEquals(6d/14, metric.getSimilarity("riastng", "asterix"), PRECISION);
        assertEquals(6d/14, metric.getSimilarity("asterix", "riastng"), PRECISION);
    }

    @Test
    public void two_separate_substrings() {
        assertEquals(12d/14, metric.getSimilarity("asterix", "astwrix"), PRECISION);
        assertEquals(12d/14, metric.getSimilarity("astwrix", "asterix"), PRECISION);
    }

    @Test
    public void repeated_substrings_twice() {
        assertEquals(6d/10, metric.getSimilarity("ast", "astwast"), PRECISION);
        assertEquals(6d/10, metric.getSimilarity("astwast", "ast"), PRECISION);
    }

    @Test
    public void repeated_substrings_thrice() {
        assertEquals(6d/14, metric.getSimilarity("ast", "astwastwast"), PRECISION);
        assertEquals(6d/14, metric.getSimilarity("astwastwast", "ast"), PRECISION);
    }
}