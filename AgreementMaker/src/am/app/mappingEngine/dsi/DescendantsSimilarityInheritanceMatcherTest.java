package am.app.mappingEngine.dsi;


import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class DescendantsSimilarityInheritanceMatcherTest extends AbstractMatcherTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new DescendantsSimilarityInheritanceMatcher();
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
