package parametricstringmatcher.external;


import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class ParametricStringMatcherTest extends AbstractMatcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new ParametricStringMatcher();
	}
	
	@Test
	public void testMatch() {
		super.testMatch();
	}

}
