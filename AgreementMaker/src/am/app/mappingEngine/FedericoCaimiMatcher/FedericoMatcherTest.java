package am.app.mappingEngine.FedericoCaimiMatcher;


import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class FedericoMatcherTest extends AbstractMatcherTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new FedericoMatcher();
		AbstractMatcherTest.inputMatchersNeeded = true;
		maxInputs = 1;
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Test
	public void testMatch() {
		super.testMatch();
	}

}
