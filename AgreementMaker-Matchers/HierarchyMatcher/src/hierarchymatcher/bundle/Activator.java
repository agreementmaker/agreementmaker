package hierarchymatcher.bundle;

import hierarchymatcher.internal.HierarchyMatcher;
import hierarchymatcher.internal.HierarchyMatcherModified;
import hierarchymatcher.internal.WordnetSubclassMatcher;

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
		context.registerService(AbstractMatcher.class, new HierarchyMatcher(), null);
		context.registerService(AbstractMatcher.class, new HierarchyMatcherModified(), null);
		context.registerService(AbstractMatcher.class, new WordnetSubclassMatcher(), null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
