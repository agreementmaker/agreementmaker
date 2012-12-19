package am.app.osgi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.mappingEngine.Combination.CombinationMatcher;
import am.app.mappingEngine.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher;
import am.app.mappingEngine.basicStructureSelector.BasicStructuralSelectorMatcher;
import am.app.mappingEngine.conceptMatcher.ConceptMatcher;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.app.mappingEngine.groupFinder.GroupFinderMatcher;
import am.app.mappingEngine.hierarchy.HierarchyMatcher;
import am.app.mappingEngine.mediatingMatcher.MediatingMatcher;
import am.app.mappingEngine.multiWords.MultiWordsMatcher;
import am.app.mappingEngine.multiWords.MultiWordsMatcherPairWise;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011Matcher;
import am.app.mappingEngine.oaei2009.OAEI2009matcher;
import am.app.mappingEngine.oaei2010.OAEI2010Matcher;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.ssc.SiblingsSimilarityContributionMatcher;
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
		matcherList.add(new BaseSimilarityMatcher());
		matcherList.add(new AdvancedSimilarityMatcher());
		matcherList.add(new BasicStructuralSelectorMatcher());
		matcherList.add(new CombinationMatcher());
		matcherList.add(new ConceptMatcher());
		matcherList.add(new CopyMatcher());
		matcherList.add(new DescendantsSimilarityInheritanceMatcher());
		matcherList.add(new EqualsMatcher());
		matcherList.add(new GroupFinderMatcher());
		matcherList.add(new HierarchyMatcher());
		matcherList.add(new IterativeInstanceStructuralMatcher());
		matcherList.add(new LexicalSynonymMatcher());
		matcherList.add(new MediatingMatcher());
		matcherList.add(new MultiWordsMatcher());
		matcherList.add(new MultiWordsMatcherPairWise());
		matcherList.add(new OAEI2009matcher());
		matcherList.add(new OAEI2010Matcher());
		matcherList.add(new OAEI2011Matcher());
		matcherList.add(new ParametricStringMatcher());
		matcherList.add(new SiblingsSimilarityContributionMatcher());
		
		//start the service tracker
		startMatcherTracker();
		
		selectionList = new ArrayList<SelectionAlgorithm>();
		selectionList.add(new MwbmSelection());
		selectionList.add(new AlignmentMergerSelection());
	}

	private void startMatcherTracker(){
		ServiceTrackerCustomizer<AbstractMatcher, AbstractMatcher> customizer = new ServiceTrackerCustomizer<AbstractMatcher,AbstractMatcher>() {
			
			@Override
			public AbstractMatcher addingService(ServiceReference<AbstractMatcher> reference) {
				AbstractMatcher matcher=context.getService(reference);
				matcherList.add(matcher);
				return matcher;
			}
			@Override
			public void modifiedService(ServiceReference<AbstractMatcher> reference,AbstractMatcher service) {
				matcherList.remove(service);
				matcherList.add(context.getService(reference));
			}
			@Override
			public void removedService(ServiceReference<AbstractMatcher> reference,AbstractMatcher service) {
				matcherList.remove(service);
			}
		};
		matcherTracker = new ServiceTracker<AbstractMatcher,AbstractMatcher>(context, AbstractMatcher.class, customizer);
		matcherTracker.open();
	}
	
	public List<String> getMatcherNames(){
		List<String> matcherNames=new ArrayList<String>();
		for(AbstractMatcher m: matcherList)
			matcherNames.add(m.getName());
		return matcherNames;
	}
	
	public AbstractMatcher getMatcherByName(String matcherName) throws MatcherNotFoundException {
		for(AbstractMatcher m : matcherList){
			if(m.getName().equals(matcherName)){
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
		throw new MatcherNotFoundException(matcherName+" is not in the system.");
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
}
