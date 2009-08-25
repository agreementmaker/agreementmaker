package am.application.mappingEngine;

import am.application.mappingEngine.Combination.CombinationMatcher;
import am.application.mappingEngine.LexicalMatcherJWNL.LexicalMatcherJWNL;
import am.application.mappingEngine.PRAMatcher.PRAMatcher;
//import am.application.mappingEngine.LexicalMatcherUMLS.LexicalMatcherUMLS;
import am.application.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.application.mappingEngine.conceptMatcher.ConceptMatcher;
import am.application.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.application.mappingEngine.dsi.OldDescendantsSimilarityInheritanceMatcher;
import am.application.mappingEngine.manualMatcher.UserManualMatcher;
import am.application.mappingEngine.multiWords.MultiWordsMatcher;
import am.application.mappingEngine.oaei2009.OAEI2009matcher;
import am.application.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.application.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.application.mappingEngine.ssc.SiblingsSimilarityContributionMatcher;
import am.application.mappingEngine.testMatchers.AllOneMatcher;
import am.application.mappingEngine.testMatchers.AllZeroMatcher;
import am.application.mappingEngine.testMatchers.CopyMatcher;
import am.application.mappingEngine.testMatchers.EqualsMatcher;
import am.application.mappingEngine.testMatchers.RandomMatcher;

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
	BaseSimilarity		( "Base Similarity Matcher (BSM)", BaseSimilarityMatcher.class ),
	 ParametricString ( "Parametric String Matcher (PSM)", ParametricStringMatcher.class ),
	 MultiWords       ("Vector-based Multi-Words Matcher (VMM)", MultiWordsMatcher.class),
	DSI					( "Descendant's Similarity Inheritance (DSI)", DescendantsSimilarityInheritanceMatcher.class ),
	SSC					( "Sibling's Similarity Contribution (SSC)", SiblingsSimilarityContributionMatcher.class ),
	Combination	( "Linear Weighted Combination (LWC)", CombinationMatcher.class ),
	ConceptSimilarity   ( "Concept Similarity", ConceptMatcher.class, false),
	DSI2					( "OLD Descendant's Similarity Inheritance (DSI)", OldDescendantsSimilarityInheritanceMatcher.class, false ),
	OAEI2009   ( "OAEI2009 Matcher", OAEI2009matcher.class),
	WordNetLexical		("Lexical Matcher: WordNet", LexicalMatcherJWNL.class),
	PRAMatcher			("PRA Matcher", PRAMatcher.class),
	//UMLSKSLexical		("Lexical Matcher: UMLSKS", LexicalMatcherUMLS.class),

	//WORK IN PROGRESS
	
	//MATCHERS USED BY THE SYSTEM, usually not shown
	UserManual			( "User Manual Matching", UserManualMatcher.class, false),
	ImportAlignment	( "Import Alignments", ReferenceAlignmentMatcher.class, false),
	
	//TEST MATCHERS 
	Equals 				( "Local Name Equivalence Comparison", EqualsMatcher.class , false),
	AllOne 				( "(Test) All One Similarities", AllOneMatcher.class, true ),
	AllZero			( "(Test) All Zero Similarities", AllZeroMatcher.class, true ),
	Copy				( "Copy Matcher", CopyMatcher.class,false ),
	Random 				( "(Test) Random Similarities", RandomMatcher.class, true );
	
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
