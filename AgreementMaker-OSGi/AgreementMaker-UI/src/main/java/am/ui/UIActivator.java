package am.ui;

import javax.swing.JFrame;

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
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("apple.awt.brushMetalLook", "true");
				
				UI newUI = new UI();
				UICore.setUI(newUI);
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
