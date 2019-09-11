package am.app.mappingEngine.registry;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherRegistry;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.mappingEngine.basicStructureSelector.BasicStructuralSelectorMatcher;
import am.app.mappingEngine.instance.IterativeMatcher;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.testMatchers.AllOneMatcher;
import am.app.mappingEngine.testMatchers.AllZeroMatcher;
import am.app.mappingEngine.testMatchers.CopyMatcher;
import am.app.mappingEngine.testMatchers.EqualsMatcher;
import am.app.mappingEngine.utility.AlignmentMergerSelection;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.matcher.asm.AdvancedSimilarityMatcher;
import am.matcher.bsm.BaseSimilarityMatcher;
import am.matcher.conceptMatcher.ConceptMatcher;
import am.matcher.dsi.DescendantsSimilarityInheritanceMatcher;
import am.matcher.mediatingMatcher.MediatingMatcher;
import am.matcher.multiWords.MultiWordsMatcher;
import am.matcher.multiWords.MultiWordsMatcherPairWise;
import am.matcher.multiWords.newMW.NewMultiWordsMatcher;
import am.matcher.oaei.oaei2009.OAEI2009matcher;
import am.matcher.oaei.oaei2010.OAEI2010Matcher;
import am.matcher.oaei.oaei2011.OAEI2011Matcher;
import am.matcher.parametricStringMatcher.ParametricStringMatcher;
import am.matcher.pra.PRAMatcher.PRAMatcher;

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

		matcherList.add(new BaseSimilarityMatcher());
		matcherList.add(new AdvancedSimilarityMatcher());
		matcherList.add(new ParametricStringMatcher());
		matcherList.add(new DescendantsSimilarityInheritanceMatcher());
		matcherList.add(new MultiWordsMatcher());
		matcherList.add(new MultiWordsMatcherPairWise());
		matcherList.add(new CombinationMatcher());
		matcherList.add(new ConceptMatcher());
		matcherList.add(new MediatingMatcher());
		matcherList.add(new IterativeMatcher());
		matcherList.add(new IterativeInstanceStructuralMatcher());
		matcherList.add(new PRAMatcher());
		matcherList.add(new OAEI2009matcher());
		matcherList.add(new OAEI2010Matcher());
		matcherList.add(new OAEI2011Matcher());
		matcherList.add(new ReferenceAlignmentMatcher());
		matcherList.add(new UserManualMatcher());
		matcherList.add(new LexicalSynonymMatcher());

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
