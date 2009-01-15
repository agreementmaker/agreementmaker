package agreementMaker.application.mappingEngine;

import agreementMaker.application.mappingEngine.Combination.CombinationMatcher;
import agreementMaker.application.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import agreementMaker.application.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import agreementMaker.application.mappingEngine.manualMatcher.UserManualMatcher;
import agreementMaker.application.mappingEngine.multiWords.MultiWordsMatcher;
import agreementMaker.application.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import agreementMaker.application.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import agreementMaker.application.mappingEngine.ssc.SiblingsSimilarityContributionMatcher;
import agreementMaker.application.mappingEngine.testMatchers.AllOneMatcher;
import agreementMaker.application.mappingEngine.testMatchers.AllZeroMatcher;
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
	
	//OFFICIAL MATCHERS
	BaseSimilarity		( "Base Similarity", BaseSimilarityMatcher.class ),
	 ParametricString ( "Parametric String Matcher", ParametricStringMatcher.class ),
	 MultiWords       ("Multi Words Matcher", MultiWordsMatcher.class),
	DSI					( "Descendant's Similarity Inheritance (DSI)", DescendantsSimilarityInheritanceMatcher.class ),
	SSC					( "Sibling's Similarity Contribution (SSC)", SiblingsSimilarityContributionMatcher.class ),
	Combination	( "Mathematical Weighted Combination", CombinationMatcher.class ),
	

	//WORK IN PROGRESS
	
	//MATCHERS USED BY THE SYSTEM, usually not shown
	UserManual			( "User Manual Matching", UserManualMatcher.class, false),
	ImportAlignment	( "Import Alignments", ReferenceAlignmentMatcher.class, false),
	
	//TEST MATCHERS 
	Equals 				( "Local Name Equivalence Comparison", EqualsMatcher.class , false),
	AllOne 				( "All ONE Similarities", AllOneMatcher.class, true ),
	AllZero			( "All Zero Similarities", AllZeroMatcher.class, true ),
	Copy				( "Copy Matcher", CopyMatcher.class,false ),
	Random 				( "RSM", RandomMatcher.class, true );
	
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
