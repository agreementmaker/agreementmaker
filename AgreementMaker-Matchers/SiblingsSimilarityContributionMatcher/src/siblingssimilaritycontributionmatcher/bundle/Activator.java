package siblingssimilaritycontributionmatcher.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import siblingssimilaritycontributionmatcher.internal.SiblingsSimilarityContributionMatcher;

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
		SiblingsSimilarityContributionMatcher matcher=new SiblingsSimilarityContributionMatcher();
		matcher.setName("Sibling's Similarity Contribution");
		context.registerService(AbstractMatcher.class.getName(), matcher, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
