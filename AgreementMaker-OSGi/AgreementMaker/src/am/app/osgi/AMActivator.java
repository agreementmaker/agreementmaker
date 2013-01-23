package am.app.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import am.app.Core;
import am.userInterface.UI;

public class AMActivator implements BundleActivator {

	private BundleContext context;
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.context=context;
		System.out.println("AgreementMaker Activator started...");
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "true");
		
		Core.getInstance().initializeOSGiRegistry(context);
		
		Thread mainUI = new Thread("UI") {
				public void run() {
					Core.setUI( new UI() );
				} 
		};
		
		mainUI.start();
		
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
