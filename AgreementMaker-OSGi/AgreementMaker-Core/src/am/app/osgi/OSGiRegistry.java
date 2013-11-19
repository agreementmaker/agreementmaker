package am.app.osgi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.mappingEngine.basicStructureSelector.BasicStructuralSelectorMatcher;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.testMatchers.AllOneMatcher;
import am.app.mappingEngine.testMatchers.AllZeroMatcher;
import am.app.mappingEngine.testMatchers.CopyMatcher;
import am.app.mappingEngine.testMatchers.EqualsMatcher;
import am.app.mappingEngine.utility.AlignmentMergerSelection;

public class OSGiRegistry {
	
	private List<AbstractMatcher> matcherList;
	private ServiceTracker<AbstractMatcher, AbstractMatcher> matcherTracker;
	private BundleContext context;

	private List<SelectionAlgorithm> selectionList;
	
	public OSGiRegistry(BundleContext bundleContext){
		//save the context
		context = bundleContext;
		//create the arraylist for the matchers
		matcherList = new ArrayList<AbstractMatcher>();
		
		matcherList.add(new AllOneMatcher());
		matcherList.add(new AllZeroMatcher());
		matcherList.add(new BasicStructuralSelectorMatcher());
		matcherList.add(new CopyMatcher());
		matcherList.add(new EqualsMatcher());
		
		//start the service tracker
		startMatcherTracker();
		
		selectionList = new ArrayList<SelectionAlgorithm>();
		selectionList.add(new MwbmSelection());
		selectionList.add(new AlignmentMergerSelection());
	}

	private void startMatcherTracker(){
		ServiceTrackerCustomizer<AbstractMatcher, AbstractMatcher> customizer = 
				new ServiceTrackerCustomizer<AbstractMatcher, AbstractMatcher>() {
			
			@Override
			public AbstractMatcher addingService(ServiceReference<AbstractMatcher> reference) {
				AbstractMatcher matcher = (AbstractMatcher) context.getService(reference);
				matcherList.add(matcher);
				return matcher;
			}
			@Override
			public void modifiedService(ServiceReference<AbstractMatcher> reference, AbstractMatcher service) {
				matcherList.remove(service);
				matcherList.add((AbstractMatcher) context.getService(reference));
			}
			@Override
			public void removedService(ServiceReference<AbstractMatcher> reference, AbstractMatcher service) {
				matcherList.remove(service);
			}

		};
		matcherTracker = new ServiceTracker<AbstractMatcher, AbstractMatcher>(context, AbstractMatcher.class, customizer);
		matcherTracker.open();
	}
	
	public List<String> getMatcherNames(){
		List<String> matcherNames=new ArrayList<String>();
		for(AbstractMatcher m: matcherList)
			matcherNames.add(m.getName());
		return matcherNames;
	}
	
	public AbstractMatcher getMatcherByClass(Class<? extends AbstractMatcher> clazz) throws MatcherNotFoundException {
		return getMatcherByClass(clazz.getName());		
	}

	public AbstractMatcher getMatcherByClass(String clazz) throws MatcherNotFoundException {
		for(AbstractMatcher m : matcherList){
			if(m.getClass().getName().equals(clazz)){
				try {
					AbstractMatcher newM = m.getClass().newInstance();
					newM.setID(Core.getInstance().getNextMatcherID());
					return newM;
				} catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		throw new MatcherNotFoundException("'" + clazz + "' is not a valid class name in the system.");
	}
	
	/**
	 * Return a list of matchers currently registered as bundles in the system.
	 * @return An empty list if no bundles are loaded into the system.
	 */
	public List<AbstractMatcher> getMatchers() {
		List<AbstractMatcher> list = new LinkedList<AbstractMatcher>();
		list.addAll(matcherList);
		return list;
	}

	public List<SelectionAlgorithm> getSelectors() {
		List<SelectionAlgorithm> list = new LinkedList<SelectionAlgorithm>();
		list.addAll(selectionList);
		return list;
	}
	
	public Bundle[] getInstalledBundles() {
		return context.getBundles();
	}
	
	public void initializeShutdown() {
		try {
			//context.getBundle(0).stop();
			EclipseStarter.shutdown();
			
			while( context.getBundles() != null ) {
				Thread.sleep(100);
			}
			
			
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
