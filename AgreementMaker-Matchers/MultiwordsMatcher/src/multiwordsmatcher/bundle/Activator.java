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
		
		MultiWordsMatcher mwMatcher= new MultiWordsMatcher();
		mwMatcher.setName("Vector-based Multi-Words Matcher");
		context.registerService(AbstractMatcher.class.getName(), mwMatcher, null);
		
		MultiWordsMatcherPairWise mwpMatcher=new MultiWordsMatcherPairWise();
		mwpMatcher.setName("VMM PairWise");
		context.registerService(AbstractMatcher.class.getName(), mwpMatcher, null);
		
		NewMultiWordsMatcher nmwMatcher=new NewMultiWordsMatcher();
		nmwMatcher.setName("New VMM");
		context.registerService(AbstractMatcher.class.getName(), nmwMatcher, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
