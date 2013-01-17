package am.matcher.oaei;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import am.app.mappingEngine.AbstractMatcher;
import am.matcher.oaei2009.OAEI2009matcher;
import am.matcher.oaei2010.OAEI2010Matcher;
import am.matcher.oaei2011.OAEI2011Matcher;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServiceRegistration<AbstractMatcher> regOAEI2009;
	private ServiceRegistration<AbstractMatcher> regOAEI2010;
	private ServiceRegistration<AbstractMatcher> regOAEI2011;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println(bundleContext.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " starting...");
		regOAEI2009 = bundleContext.registerService(AbstractMatcher.class, new OAEI2009matcher(), new Hashtable<String,String>());
		regOAEI2010 = bundleContext.registerService(AbstractMatcher.class, new OAEI2010Matcher(), new Hashtable<String,String>());
		regOAEI2011 = bundleContext.registerService(AbstractMatcher.class, new OAEI2011Matcher(), new Hashtable<String,String>());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		regOAEI2009.unregister();
		regOAEI2010.unregister();
		regOAEI2011.unregister();
		System.out.println(bundleContext.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " stopping...");
	}

}
