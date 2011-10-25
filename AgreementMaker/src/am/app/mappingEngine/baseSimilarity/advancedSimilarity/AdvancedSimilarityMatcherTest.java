package am.app.mappingEngine.baseSimilarity.advancedSimilarity;


import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class AdvancedSimilarityMatcherTest extends AbstractMatcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new AdvancedSimilarityMatcher();
	}
	
	@Test
	public void testMatch() {
		super.testMatch();
	}

}
