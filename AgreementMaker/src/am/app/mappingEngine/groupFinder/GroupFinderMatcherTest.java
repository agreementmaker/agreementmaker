package am.app.mappingEngine.groupFinder;


import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class GroupFinderMatcherTest extends AbstractMatcherTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new GroupFinderMatcher();
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
