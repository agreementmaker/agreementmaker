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
		
		HierarchyMatcher hMatcher=new HierarchyMatcher();
		hMatcher.setName("Hierarchy Matcher");
		context.registerService(AbstractMatcher.class.getName(), hMatcher, null);
		
		HierarchyMatcherModified hmMatcher=new HierarchyMatcherModified();
		hmMatcher.setName("Hierarchy Matcher Modified");
		context.registerService(AbstractMatcher.class.getName(), hmMatcher, null);
		
		WordnetSubclassMatcher wMatcher=new WordnetSubclassMatcher();
		wMatcher.setName("Wordnet Subclass Matcher");
		context.registerService(AbstractMatcher.class.getName(), wMatcher, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
