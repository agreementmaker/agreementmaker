package am.matcher.oaei.imei2013;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static Logger LOG = Logger.getLogger(Activator.class);
	
	private BundleContext context;
	
	@Override
	public void start(BundleContext context) throws Exception {
		LOG.info("Starting " + context.getBundle().getSymbolicName() + " ...");
		this.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOG.info("Stopping " + context.getBundle().getSymbolicName() + " ...");
		this.context = null;
	}

}
