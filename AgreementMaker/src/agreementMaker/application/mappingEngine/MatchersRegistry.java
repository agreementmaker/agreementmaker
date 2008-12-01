package agreementMaker.application.mappingEngine;

import agreementMaker.application.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import agreementMaker.application.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import agreementMaker.application.mappingEngine.manualCombination.ManualCombinationMatcher;
import agreementMaker.application.mappingEngine.manualMatcher.EmptyMatcher;
import agreementMaker.application.mappingEngine.manualMatcher.UserManualMatcher;
import agreementMaker.application.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import agreementMaker.application.mappingEngine.ssc.SiblingsSimilarityContributionMatcher;
import agreementMaker.application.mappingEngine.testMatchers.AllOneMatcher;
import agreementMaker.application.mappingEngine.testMatchers.CopyMatcher;
import agreementMaker.application.mappingEngine.testMatchers.EqualsMatcher;
import agreementMaker.application.mappingEngine.testMatchers.RandomMatcher;

/**
 * Enum for keeping the current list of matchers in the system, and their class references
 */
public enum MatchersRegistry {
	
	/**
	 * This is where you add your own MATCHER.
	 * 
	 * To add your matcher, add a definition to the enum, using this format
	 * 
	 * 		EnumName	( "Short Name", MatcherClass.class )
	 * 
	 * For example, to add MySuperMatcher, you would add something like this (assuming the class name is MySuperMatcher):
	 *  
	 * 		SuperMatcher   ( "My Super Matcher", MySuperMatcher.class ),
	 * 
	 * And so, if your matcher is has no code errors, it will be incorporated into the AgreementMaker.  - Cosmin
	 */
	
	UserManual			( "User Manual Matching", UserManualMatcher.class, false),
	Equals 				( "Local Name Equivalence Comparison", EqualsMatcher.class ),
	Random 				( "Random Similarity Matcher", RandomMatcher.class ),
	AllOne 				( "All ONE Similarities", AllOneMatcher.class ),
	EmptyMatcher		( "Empty Matcher", EmptyMatcher.class ),
	Copy				( "Copy Matcher", CopyMatcher.class ),
	BaseSimilarity		( "Base Similarity", BaseSimilarityMatcher.class ),
	DSI					( "Descendant's Similarity Inheritance (DSI)", DescendantsSimilarityInheritanceMatcher.class ),
	SSC					( "Sibling's Similarity Contribution (SSC)", SiblingsSimilarityContributionMatcher.class ),
	ReferenceAlignment	( "Reference Alignment", ReferenceAlignmentMatcher.class ),
	ManualCombination	( "Manual Combination", ManualCombinationMatcher.class );
	
	
	/* Don't change anything below this line .. unless you intend to. */
	private boolean showInControlPanel;
	private String name;
	private String className;
	
	MatchersRegistry( String n, Class matcherClass ) { name = n; className = matcherClass.getName(); showInControlPanel = true;}
	MatchersRegistry( String n, Class matcherClass, boolean shown) { name = n; className = matcherClass.getName(); showInControlPanel = shown; }
	public String getMatcherName() { return name; }
	public String getMatcherClass() { return className; }
	public boolean isShown() { return showInControlPanel; }
	
}
