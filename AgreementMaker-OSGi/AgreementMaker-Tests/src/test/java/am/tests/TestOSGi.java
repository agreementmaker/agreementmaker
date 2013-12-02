package am.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class TestOSGi {

	@Inject
	private BundleContext bundleContext;

	@Configuration
	public Option[] config() {
		return new Option[]{ 
				karafDistributionConfiguration().frameworkUrl(
						maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").version("2.3.2"))
							.karafVersion("2.3.2").name("Apache Karaf")};
	}

	@Test
	public void initialization() {
		assertTrue(true);
	}
	
	@Test
	public void checkBundleContext() {
		assertNotNull(bundleContext);
	}

}
