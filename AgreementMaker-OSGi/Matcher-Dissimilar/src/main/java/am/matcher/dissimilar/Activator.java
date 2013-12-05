package am.matcher.dissimilar;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import am.app.mappingEngine.AbstractMatcher;



public class Activator implements BundleActivator {
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServiceRegistration<AbstractMatcher> reg;

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		System.out.println(context.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " starting...");
		reg = context.registerService(AbstractMatcher.class, new DissimilarMatcher(), new Hashtable<String,String>());
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.context = null;
		reg.unregister();
		
		System.out.println(context.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " stopping...");
		
	}
	
	
}
