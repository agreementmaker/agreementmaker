package am.app.mappingEngine.persistance;

import org.osgi.framework.Constants;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
/**
 * 
 * <a href="http://www.eclipse.org/gemini/blueprint/documentation/reference/1.0.2.RELEASE/html/testing.html">OSGi Testing</a>
 * 
 * @author cosmin
 *
 */
public class TestPersistanceUtility extends AbstractConfigurableBundleCreatorTests {

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
	}
}
