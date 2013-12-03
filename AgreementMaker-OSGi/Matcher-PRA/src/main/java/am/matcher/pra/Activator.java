package am.matcher.pra;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import am.app.mappingEngine.AbstractMatcher;
import am.matcher.pra.PRAMatcher.OldPRAMatcher;
import am.matcher.pra.PRAMatcher.PRAMatcher;
import am.matcher.pra.PRAMatcher.PRAMatcher2;
import am.matcher.pra.PRAintegration.PRAintegrationMatcher;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServiceRegistration<AbstractMatcher> prai;
	private ServiceRegistration<AbstractMatcher> pra;
	private ServiceRegistration<AbstractMatcher> pra2;
	private ServiceRegistration<AbstractMatcher> oldpra;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println(bundleContext.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " starting...");
		
		prai = bundleContext.registerService(AbstractMatcher.class, new PRAintegrationMatcher(), null);
		pra = bundleContext.registerService(AbstractMatcher.class, new PRAMatcher(), null);
		pra2 = bundleContext.registerService(AbstractMatcher.class, new PRAMatcher2(), null);
		oldpra = bundleContext.registerService(AbstractMatcher.class, new OldPRAMatcher(), null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		
		prai.unregister();
		pra.unregister();
		pra2.unregister();
		oldpra.unregister();
		
		System.out.println(bundleContext.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " stopping...");
	}

}
