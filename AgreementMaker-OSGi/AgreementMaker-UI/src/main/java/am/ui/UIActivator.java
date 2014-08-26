package am.ui;

import javax.swing.JFrame;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import am.ui.api.AMVisualizationComponent;
import am.ui.osgi.VisualizationComponentTrackerCustomizer;

public class UIActivator implements BundleActivator {

	private static BundleContext context;

	private ServiceTracker<AMVisualizationComponent, AMVisualizationComponent> visTracker;
	
	private ServiceRegistration<UI> reg;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		UIActivator.context = bundleContext;
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "true");
		
		UI newUI = new UI();
		UICore.setUI(newUI);
		
		reg = context.registerService(UI.class, newUI, null);
		
		final JFrame uiFrame = newUI.getUIFrame();
		
		java.awt.EventQueue.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        uiFrame.toFront();
		        uiFrame.repaint();
		    }
		});
		
		// start the visualization tracker
		visTracker = new ServiceTracker<AMVisualizationComponent, AMVisualizationComponent>(context, 
				AMVisualizationComponent.class, new VisualizationComponentTrackerCustomizer(context));
		visTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		reg.unregister();
		reg = null;
		visTracker.close();
		visTracker = null;
		UIActivator.context = null;
	}

}
