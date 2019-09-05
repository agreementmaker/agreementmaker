package am.extension.userfeedback.common;

import static org.junit.Assert.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import org.junit.Test;

public class ExperimentIterationTest {

	private static final double DELTA = 0.000000001;
	
	@Test
	public void testObvious() {
		ExperimentIteration iteration = new ExperimentIteration(0.5, 0.3443, 2);
		assertEquals(0.5d, iteration.getPrecision(), DELTA);
		assertEquals(0.3443d, iteration.getRecall(), DELTA);
		assertEquals(2, iteration.getDelta());
	}
	
	@Test
	public void testFMeasureCalculation() {
		ExperimentIteration iteration = new ExperimentIteration(0.6, 0.7, 0);
		assertEquals(0.646153846, iteration.getFMeasure(), DELTA);
		
		ExperimentIteration iteration2 = new ExperimentIteration(0.0, 0.0, 0);
		assertEquals(0.0, iteration2.getFMeasure(), DELTA);
		
		ExperimentIteration iteration3 = new ExperimentIteration(0.0000001, 0.0, 0);
		assertEquals(0.0, iteration3.getFMeasure(), DELTA);
		
		ExperimentIteration iteration4 = new ExperimentIteration(0.0000001, 0.0000001, 0);
		assertEquals(0.0000001, iteration4.getFMeasure(), DELTA);
		
		ExperimentIteration iteration5 = new ExperimentIteration(0.0, 1.0, 0);
		assertEquals(0.0, iteration5.getFMeasure(), DELTA);
		
		ExperimentIteration iteration6 = new ExperimentIteration(1.0, 1.0, 0);
		assertEquals(1.0, iteration6.getFMeasure(), DELTA);
		
		ExperimentIteration iteration7 = new ExperimentIteration(1.0, 0.1, 0);
		assertEquals(0.181818182, iteration7.getFMeasure(), DELTA);
		
		ExperimentIteration iteration8 = new ExperimentIteration(0.1, 1.0, 0);
		assertEquals(0.181818182, iteration8.getFMeasure(), DELTA);
		
		ExperimentIteration iteration9 = new ExperimentIteration(1.1, 0.1, 0);
		assertEquals(-1, iteration9.getFMeasure(), DELTA);
		
		ExperimentIteration iteration10 = new ExperimentIteration(0.1, 1.1, 0);
		assertEquals(-1, iteration10.getFMeasure(), DELTA);
		
		ExperimentIteration iteration11 = new ExperimentIteration(1.1, -0.1, 0);
		assertEquals(-1, iteration11.getFMeasure(), DELTA);
		
		ExperimentIteration iteration12 = new ExperimentIteration(-0.1, 1.1, 0);
		assertEquals(-1, iteration12.getFMeasure(), DELTA);
	}
	
	@Test
	public void testEquals() {
		ExperimentIteration iter1 = new ExperimentIteration(0.123456890, 0.2345678901, 12345);
		ExperimentIteration iter2 = new ExperimentIteration(0.123456890, 0.2345678901, 12345);
		ExperimentIteration iter3 = new ExperimentIteration(0.456890,    0.2345678901, 12345);
		ExperimentIteration iter4 = new ExperimentIteration(0.123456890, 0.278901    , 12345);
		ExperimentIteration iter5 = new ExperimentIteration(0.123456890, 0.2345678901, 0);
		
		assertThat(iter1, is(equalTo(iter2)));
		assertThat(iter1, is(not(equalTo(iter3))));
		assertThat(iter1, is(not(equalTo(iter4))));
		assertThat(iter1, is(not(equalTo(iter5))));
		
		assertThat(iter3, is(not(equalTo(iter4))));
		assertThat(iter3, is(not(equalTo(iter5))));
		
		assertThat(iter4, is(not(equalTo(iter5))));

		assertThat(iter3, is(equalTo(iter3)));
		assertThat(iter4, is(equalTo(iter4)));
		assertThat(iter5, is(equalTo(iter5)));
	}
}
