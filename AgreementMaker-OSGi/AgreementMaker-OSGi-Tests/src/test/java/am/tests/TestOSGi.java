package am.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class TestOSGi {

	@Inject
	private BundleContext bundleContext;

	@Configuration
	public Option[] config() {

		return options(
				junitBundles()
		);

	}

	@Test
	public void checkBundleContext() {
		assertNotNull(bundleContext);
	}

}
