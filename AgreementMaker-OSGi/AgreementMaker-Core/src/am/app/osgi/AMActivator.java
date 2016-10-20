package am.app.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import am.app.Core;

public class AMActivator implements BundleActivator {

	private BundleContext context;

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		Core.getInstance().initializeOSGiRegistry(context);
		System.out.println("AgreementMaker Core started...");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		this.context = null;
		System.out.println("AgreementMaker Core stopped...");
	}
}
