package am.app.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AMActivator implements BundleActivator {

	private BundleContext context;
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.context=context;
		System.out.println("AgreementMaker Activator started...");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		this.context=null;
		System.out.println("AgreementMaker Activator stopped...");
	}
	
    public Bundle[] getBundles()
    {
        if (context != null)
        {
            return context.getBundles();
        }
        return null;
    } 

}
