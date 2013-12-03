package am.ui.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import am.ui.UICore;
import am.ui.api.AMMenuItem;
import am.ui.api.AMTab;
import am.ui.api.AMVisualizationComponent;

public class VisualizationComponentTrackerCustomizer 
	implements ServiceTrackerCustomizer<AMVisualizationComponent, AMVisualizationComponent> {

	private final BundleContext context;
	
	public VisualizationComponentTrackerCustomizer(BundleContext context) {
		this.context = context;
	}
	
	@Override
	public AMVisualizationComponent addingService(
			ServiceReference<AMVisualizationComponent> svcr) {

		AMVisualizationComponent component = context.getService(svcr);
		
		if( component instanceof AMTab ) {
			UICore.getUI().addTab( (AMTab)component );
		}
		
		if( component instanceof AMMenuItem ) {
			UICore.getUI().addMenuItem( (AMMenuItem)component );
		}

		return component;
	}

	@Override
	public void modifiedService(
			ServiceReference<AMVisualizationComponent> arg0,
			AMVisualizationComponent arg1) {
		// FIXME: Implement this!
		System.err.println("TODO: Implement VisualizationComponentServiceTracker#modifiedService");
	}

	@Override
	public void removedService(ServiceReference<AMVisualizationComponent> arg0,
			AMVisualizationComponent arg1) {
		// FIXME: Implement this!
		System.err.println("TODO: Implement VisualizationComponentServiceTracker#removedService");
	}

}
