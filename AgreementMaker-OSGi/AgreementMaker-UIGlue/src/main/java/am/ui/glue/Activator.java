package am.ui.glue;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import am.ui.api.AMVisualizationComponent;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private ServiceRegistration<AMVisualizationComponent> regBatchModeMenuItem;
	private ServiceRegistration<AMVisualizationComponent> regLODMenuItem;
	private ServiceRegistration<AMVisualizationComponent> regUFLBMMenuItem;
	
	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		regBatchModeMenuItem = 
				bundleContext.registerService(
						AMVisualizationComponent.class, new BatchModeMenuItem(), null);
		
		regLODMenuItem =
				bundleContext.registerService(
						AMVisualizationComponent.class, new LODMenuItem(), null);
		
		regUFLBMMenuItem =
				bundleContext.registerService(
						AMVisualizationComponent.class, new UFLMenuItem(), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		regBatchModeMenuItem.unregister();
		regLODMenuItem.unregister();
		regUFLBMMenuItem.unregister();
		
		Activator.context = null;
	}

}
