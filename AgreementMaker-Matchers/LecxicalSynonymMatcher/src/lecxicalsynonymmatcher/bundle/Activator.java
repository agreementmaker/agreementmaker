package lecxicalsynonymmatcher.bundle;

import lecxicalsynonymmatcher.external.LexicalSynonymMatcher;
import lecxicalsynonymmatcher.external.LexicalSynonymMatcherWeighted;

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
		
		LexicalSynonymMatcher lmMatcher=new LexicalSynonymMatcher();
		lmMatcher.setName("Lexical Synonym Matcher");
		context.registerService(AbstractMatcher.class.getName(), lmMatcher, null);
		
		LexicalSynonymMatcherWeighted lmwMatcher=new LexicalSynonymMatcherWeighted();
		lmwMatcher.setName("LSM Weighted");
		context.registerService(AbstractMatcher.class.getName(), lmwMatcher, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
