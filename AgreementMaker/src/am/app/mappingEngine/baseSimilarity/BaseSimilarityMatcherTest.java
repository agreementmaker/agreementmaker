package am.app.mappingEngine.baseSimilarity;

import java.lang.annotation.Annotation;

import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

public class BaseSimilarityMatcherTest extends AbstractMatcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new BaseSimilarityMatcher();
	}
	
	@Test
	public void testMatch() {
		super.testMatch();
	}

}
