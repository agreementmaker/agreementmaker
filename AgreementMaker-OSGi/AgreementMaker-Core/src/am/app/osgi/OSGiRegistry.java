package am.app.osgi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherRegistry;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.mappingEngine.basicStructureSelector.BasicStructuralSelectorMatcher;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.testMatchers.AllOneMatcher;
import am.app.mappingEngine.testMatchers.AllZeroMatcher;
import am.app.mappingEngine.testMatchers.CopyMatcher;
import am.app.mappingEngine.testMatchers.EqualsMatcher;
import am.app.mappingEngine.utility.AlignmentMergerSelection;

/**
 * Keep track of all MatchingAlgorithms that are registered in the OSGi
 * framework.
 * 
 * @author cosmin
 * 
 */
public class OSGiRegistry extends MatcherRegistry {
	
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

	/**
	 * We use an OSGi matcher tracker to keep track of all the matching algorithms in the system.
	 */
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
	
	/**
	 * Return a list of matchers currently registered as bundles in the system.
	 * @return An empty list if no bundles are loaded into the system.
	 */
	@Override
	public List<AbstractMatcher> getMatchers() {
		List<AbstractMatcher> list = new LinkedList<AbstractMatcher>();
		list.addAll(matcherList);
		return list;
	}

	@Override
	public List<SelectionAlgorithm> getSelectors() {
		List<SelectionAlgorithm> list = new LinkedList<SelectionAlgorithm>();
		list.addAll(selectionList);
		return list;
	}
}
