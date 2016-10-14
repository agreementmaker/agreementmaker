package am.app.similarity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EditDistanceMeasureTest {
    private EditDistanceMeasure measure;

    @Before
    public void setUp() {
        measure = new EditDistanceMeasure();
    }

    @Test(expected = NullPointerException.class)
    public void null_first_arg() {
        measure.calculate(null,"s2");
    }

    @Test(expected = NullPointerException.class)
    public void null_second_arg() {
        measure.calculate("s1",null);
    }

    @Test(expected = NullPointerException.class)
    public void null_both_args() {
        measure.calculate(null,null);
    }

    @Test
    public void equivalent_strings() {
        assertEquals(0, measure.calculate("", ""));
        assertEquals(0, measure.calculate("a", "a"));
        assertEquals(0, measure.calculate("abcdefghijk", "abcdefghijk"));
    }

    @Test
    public void one_addition() {
        assertEquals(1, measure.calculate("", "a"));
        assertEquals(1, measure.calculate("a", "ab"));
        assertEquals(1, measure.calculate("ab", "abc"));
    }

    @Test
    public void one_deletion() {
        assertEquals(1, measure.calculate("abc", "ab"));
        assertEquals(1, measure.calculate("ab", "a"));
        assertEquals(1, measure.calculate("a", ""));
    }

    @Test
    public void one_change() {
        assertEquals(1, measure.calculate("a", "b"));
        assertEquals(1, measure.calculate("abc", "adc"));
    }

    @Test
    public void one_change_one_addition() {
        assertEquals(2, measure.calculate("a", "bd"));
        assertEquals(2, measure.calculate("abc", "adce"));
    }

    @Test
    public void one_change_one_deletion() {
        assertEquals(2, measure.calculate("bd", "a"));
        assertEquals(2, measure.calculate("adce", "abc"));
    }

    @Test
    public void two_changes() {
        assertEquals(2, measure.calculate("ab", "cd"));
        assertEquals(2, measure.calculate("abcd", "abef"));
    }

    @Test
    public void two_changes_one_addition() {
        assertEquals(3, measure.calculate("ab", "cde"));
        assertEquals(3, measure.calculate("abcd", "abefg"));
    }
}