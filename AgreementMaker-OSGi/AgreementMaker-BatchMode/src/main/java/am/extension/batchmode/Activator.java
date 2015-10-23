package am.extension.batchmode;

import am.extension.batchmode.api.BatchModeFileReader;
import am.extension.batchmode.api.BatchModeRunner;
import am.extension.batchmode.internal.BatchModeFileReaderImpl;
import am.extension.batchmode.simpleBatchMode.SimpleBatchModeRunner;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private static BundleContext context;
    private ServiceRegistration<BatchModeFileReader> fileReaderServiceRegistration;
    private ServiceRegistration<BatchModeRunner> runnerServiceRegistration;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
        fileReaderServiceRegistration = context.registerService(BatchModeFileReader.class, new BatchModeFileReaderImpl(), null);
        runnerServiceRegistration = context.registerService(BatchModeRunner.class, new SimpleBatchModeRunner(), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
        fileReaderServiceRegistration.unregister();
        runnerServiceRegistration.unregister();
		Activator.context = null;
	}

}
