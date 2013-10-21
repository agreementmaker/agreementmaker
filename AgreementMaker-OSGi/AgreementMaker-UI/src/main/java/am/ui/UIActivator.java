package am.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class UIActivator implements BundleActivator {

	private static BundleContext context;

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
			}
			
		});
		ui_start.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		UIActivator.context = null;
	}

}
