package am.app.osgi;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Keep track of all MatchingAlgorithms that are registered in the OSGi
 * framework.
 * 
 * @author cosmin
 * 
 */
public class MatcherRegistryImpl extends MatcherRegistry {
	
	private List<AbstractMatcher> matcherList;

	private List<SelectionAlgorithm> selectionList;
	
	public MatcherRegistryImpl() {
		//create the arraylist for the matchers
		matcherList = new ArrayList<AbstractMatcher>();
		
		matcherList.add(new AllOneMatcher());
		matcherList.add(new AllZeroMatcher());
		matcherList.add(new BasicStructuralSelectorMatcher());
		matcherList.add(new CopyMatcher());
		matcherList.add(new EqualsMatcher());

		selectionList = new ArrayList<SelectionAlgorithm>();
		selectionList.add(new MwbmSelection());
		selectionList.add(new AlignmentMergerSelection());
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
