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
public class OSGiSampleTest {
 
    @Inject
    private BundleContext context;
 
    @Configuration
    public Option[] config() {
 
        return options(
        	mavenBundle("edu.uic.cs.advis.am", "AgreementMaker-Core", "0.3.0-SNAPSHOT"),
            junitBundles()
            );
    }
 
    @Test
    public void getHelloService() {
        assertNotNull(context);
    }
}