package am.app.mappingEngine.persistance;

import org.osgi.framework.Constants;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

public class TestPersistanceUtility extends AbstractConfigurableBundleCreatorTests {

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
	}
}
