package am.app.osgi;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.userInterface.UI;

public class AMActivator implements BundleActivator {

	private BundleContext context;
	
	private ServiceRegistration<AbstractMatcher> regUserManualMatcher;
	private ServiceRegistration<AbstractMatcher> regReferenceAlignmentMatcher;
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.context=context;
		System.out.println("AgreementMaker Core started...");
		
		//System.setProperty("apple.laf.useScreenMenuBar", "true");
		//System.setProperty("apple.awt.brushMetalLook", "true");
		
		regUserManualMatcher = context.registerService(AbstractMatcher.class, new UserManualMatcher(), new Hashtable<String,String>());
		regReferenceAlignmentMatcher = context.registerService(AbstractMatcher.class, new ReferenceAlignmentMatcher(), new Hashtable<String,String>());
		
		Core.getInstance().initializeOSGiRegistry(context);
		
		Core.setUI( new UI() );
		
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		regUserManualMatcher.unregister();
		regReferenceAlignmentMatcher.unregister();
		this.context=null;
		System.out.println("AgreementMaker Core stopped...");
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
