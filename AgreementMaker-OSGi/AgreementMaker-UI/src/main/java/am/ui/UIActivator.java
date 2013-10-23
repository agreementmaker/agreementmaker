package am.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import am.ui.api.AMVisualizationComponent;
import am.ui.osgi.VisualizationComponentTrackerCustomizer;

public class UIActivator implements BundleActivator {

	private static BundleContext context;

	private ServiceTracker<AMVisualizationComponent, AMVisualizationComponent> visTracker;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		UIActivator.context = bundleContext;
		
		Thread ui_start = new Thread(new Runnable() {

			@Override
			public void run() {
				UICore.setUI(new UI());
				
				// start the visualization tracker
				visTracker = new ServiceTracker<AMVisualizationComponent, AMVisualizationComponent>(context, 
						AMVisualizationComponent.class, new VisualizationComponentTrackerCustomizer(context));
				visTracker.open();
			}
			
		});
		ui_start.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		visTracker.close();
		UIActivator.context = null;
	}

}
