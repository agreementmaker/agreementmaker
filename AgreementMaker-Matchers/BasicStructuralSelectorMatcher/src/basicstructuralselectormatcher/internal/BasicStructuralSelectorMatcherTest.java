/**
 * 
 */
package basicstructuralselectormatcher.internal;

import org.junit.BeforeClass;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcherTest;

/**
 * @author nikiforos
 *
 */
public class BasicStructuralSelectorMatcherTest extends AbstractMatcherTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractMatcherTest.setUpBeforeClass();
		testMatcher = new BasicStructuralSelectorMatcher();
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
