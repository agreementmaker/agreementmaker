package multiwordsmatcher.bundle;

import multiwordsmatcher.internal.MultiWordsMatcher;
import multiwordsmatcher.internal.MultiWordsMatcherPairWise;
import multiwordsmatcher.internal.newVMM.NewMultiWordsMatcher;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import am.app.mappingEngine.AbstractMatcher;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		context.registerService(AbstractMatcher.class, new MultiWordsMatcher(), null);
		context.registerService(AbstractMatcher.class, new MultiWordsMatcherPairWise(), null);
		context.registerService(AbstractMatcher.class, new NewMultiWordsMatcher(), null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
