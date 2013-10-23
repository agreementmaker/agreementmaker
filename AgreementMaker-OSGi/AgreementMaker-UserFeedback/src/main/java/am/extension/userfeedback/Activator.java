package am.extension.userfeedback;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import am.app.mappingEngine.AbstractMatcher;
import am.extension.userfeedback.ui.UFLMenuItem;
import am.ui.api.AMVisualizationComponent;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServiceRegistration<AMVisualizationComponent> reg;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println(bundleContext.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " starting...");
		
		reg = bundleContext.registerService(AMVisualizationComponent.class, new UFLMenuItem(), new Hashtable<String,String>());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		reg.unregister();
		Activator.context = null;
		System.out.println(bundleContext.getBundle().getHeaders().get
				(Constants.BUNDLE_NAME) + " stopping...");
	}

}
