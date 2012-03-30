package lecxicalsynonymmatcher.external;


import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class LexicalSynonymMatcherTest extends AbstractMatcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new LexicalSynonymMatcher();
	}
	
	@Test
	public void testMatch() {
		super.testMatch();
	}

}
