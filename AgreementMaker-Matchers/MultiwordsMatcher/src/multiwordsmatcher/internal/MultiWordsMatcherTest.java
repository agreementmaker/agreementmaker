package multiwordsmatcher.internal;


import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class MultiWordsMatcherTest extends AbstractMatcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new MultiWordsMatcher();
	}
	
	@Test
	public void testMatch() {
		super.testMatch();
	}

}
