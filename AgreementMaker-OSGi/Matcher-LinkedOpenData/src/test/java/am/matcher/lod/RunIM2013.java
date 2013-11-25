package am.matcher.lod;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

@RunWith( PaxExam.class)
public class RunIM2013 {

	@Configuration
	public Option[] config() {
		
		return options(
			mavenBundle("edu.uic.cs.advis.am", "AgreementMaker-Core", "0.3.0-SNAPSHOT")
		);
		
	}
	
	@Test
	public void testOSGi() {
		//assertTrue(true);
		fail();
	}
}
