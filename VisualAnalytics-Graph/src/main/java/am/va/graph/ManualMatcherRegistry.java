package am.va.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import am.matcher.Combination.CombinationMatcher;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcherWeighted;
import am.matcher.asm.AdvancedSimilarityMatcher;
import am.matcher.bsm.BaseSimilarityMatcher;
import am.matcher.dsi.DescendantsSimilarityInheritanceMatcher;
import am.matcher.multiWords.MultiWordsMatcher;
import am.matcher.oaei.oaei2009.OAEI2009matcher;
import am.matcher.oaei.oaei2010.OAEI2010Matcher;
import am.matcher.oaei.oaei2011.OAEI2011Matcher;
import am.matcher.parametricStringMatcher.ParametricStringMatcher;
import am.matcher.pra.PRAMatcher.OldPRAMatcher;
import am.matcher.pra.PRAMatcher.PRAMatcher;
import am.matcher.pra.PRAMatcher.PRAMatcher2;
import am.matcher.pra.PRAintegration.PRAintegrationMatcher;

public class ManualMatcherRegistry extends MatcherRegistry {

	List<AbstractMatcher> matchers = new LinkedList<>();
	List<SelectionAlgorithm> selectors = new LinkedList<>();
	
	public ManualMatcherRegistry() {
		// AgreementMaker-Core
		matchers.add(new AllOneMatcher());
		matchers.add(new AllZeroMatcher());
		matchers.add(new BasicStructuralSelectorMatcher());
		matchers.add(new CopyMatcher());
		matchers.add(new EqualsMatcher());
		
		// AgreementMaker-Matchers
		matchers.add(new CombinationMatcher());
		matchers.add(new ParametricStringMatcher());
		matchers.add(new MultiWordsMatcher());
		matchers.add(new LexicalSynonymMatcher());
		matchers.add(new LexicalSynonymMatcherWeighted());
		matchers.add(new DescendantsSimilarityInheritanceMatcher());
		matchers.add(new IterativeInstanceStructuralMatcher());
		
		// Matcher-AdvancedSimilarity
		matchers.add(new AdvancedSimilarityMatcher());
		
		// Matcher-BaseSimilarity
		matchers.add(new BaseSimilarityMatcher());
		
		// Matcher-OAEI
		matchers.add(new OAEI2009matcher());
		matchers.add(new OAEI2010Matcher());
		matchers.add(new OAEI2011Matcher());
		
		// Matcher-PRA
		matchers.add(new PRAintegrationMatcher());
		matchers.add(new PRAMatcher());
		matchers.add(new PRAMatcher2());
		matchers.add(new OldPRAMatcher());
		
		// AgreementMaker-Core
		selectors = new ArrayList<SelectionAlgorithm>();
		selectors.add(new MwbmSelection());
		selectors.add(new AlignmentMergerSelection());
	}
	
	@Override
	public List<AbstractMatcher> getMatchers() {
		// return a new list so that our list is never modified
		List<AbstractMatcher> matcherList = new LinkedList<>();
		matcherList.addAll(matchers);
		return matcherList;
	}

	@Override
	public List<SelectionAlgorithm> getSelectors() {
		// return a new list so that our list is never modified
		List<SelectionAlgorithm> selectorsList = new LinkedList<>();
		selectorsList.addAll(selectors);
		return selectorsList;
	}

}
