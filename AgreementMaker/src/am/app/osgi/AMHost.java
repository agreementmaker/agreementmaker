package am.app.osgi;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;

public class AMHost {
	private AMActivator m_activator = null;
    private Felix m_felix = null;
    
    public AMHost()
    {
        // Create a configuration property map.
        Map<String, Object> configMap = new HashMap<String, Object>();
        // Create host activator;
        m_activator = new AMActivator();
        List<BundleActivator> list = new ArrayList<BundleActivator>();
        list.add(m_activator);
        configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);
        configMap.put(FelixConstants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
                "am.app.mappingEngine,am.app.");

        File bundles[] = new File("plugins/").listFiles();

        configMap.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERY, AutoProcessor.AUTO_DEPLOY_INSTALL_VALUE);
        configMap.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY, "plugins/");
        try
        {
            // Now create an instance of the framework with
            // our configuration properties.
            m_felix = new Felix(configMap);
            // Now start Felix instance.
            m_felix.init();
            
            AutoProcessor.process(configMap, m_felix.getBundleContext());
            
            m_felix.start();
        }
        catch (Exception ex)
        {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
        }
    }

    public Bundle[] getInstalledBundles()
    {
        // Use the system bundle activator to gain external
        // access to the set of installed bundles.
        return m_activator.getBundles();
    }

    public void shutdownApplication()
    {
        // Shut down the felix framework when stopping the
        // host application.
        try {
			m_felix.stop();
			m_felix.waitForStop(0);
		} catch (Exception e) {e.printStackTrace();}
        
    }
}
