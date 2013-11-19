package am.ui.glue;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import am.ui.api.AMVisualizationComponent;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private ServiceRegistration<AMVisualizationComponent> regBatchModeMenuItem;
	
	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		regBatchModeMenuItem = 
				bundleContext.registerService(
						AMVisualizationComponent.class, new BatchModeMenuItem(), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		regBatchModeMenuItem.unregister();
		
		Activator.context = null;
	}

}
