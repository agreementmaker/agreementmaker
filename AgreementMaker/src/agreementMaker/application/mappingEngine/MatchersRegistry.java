package agreementMaker.application.mappingEngine;

import agreementMaker.application.mappingEngine.Matchers.BaseSimilarityMatcher;
import agreementMaker.application.mappingEngine.Matchers.DescendantsSimilarityInheritanceMatcher;
import agreementMaker.application.mappingEngine.Matchers.SiblingsSimilarityContributionMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.AllOneMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.AllZeroMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.CopyMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.EqualsMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.RandomMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.UserManualMatcher;

/**
 * Enum for keeping the current list of matchers in the system, and their class references
 */
public enum MatchersRegistry {
	UserManual		( "User Manual Matching", UserManualMatcher.class, false),
	Equals 			( "Local Name Equivalence Comparison", EqualsMatcher.class ),
	Random 			( "Random Similarity Matcher", RandomMatcher.class ),
	AllOne 			( "All ONE Similarities", AllOneMatcher.class ),
	AllZero			( "All ZERO Similarities", AllZeroMatcher.class ),
	Copy			( "Copy Matcher", CopyMatcher.class ),
	BaseSimilarity	( "Base Similarity", BaseSimilarityMatcher.class ),
	DSI				( "Descendant's Similarity Inheritance (DSI)", DescendantsSimilarityInheritanceMatcher.class ),
	SSC				( "Sibling's Similarity Contribution (SSC)", SiblingsSimilarityContributionMatcher.class );
	
	private boolean showInControlPanel;
	private String name;
	private String className;
	
	MatchersRegistry( String n, Class matcherClass ) { name = n; className = matcherClass.getName(); showInControlPanel = true;}
	MatchersRegistry( String n, Class matcherClass, boolean shown) { name = n; className = matcherClass.getName(); showInControlPanel = shown; }
	public String getMatcherName() { return name; }
	public String getMatcherClass() { return className; }
	public boolean isShown() { return showInControlPanel; }
	
}
