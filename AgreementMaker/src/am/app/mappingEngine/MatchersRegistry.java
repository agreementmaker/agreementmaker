package am.app.mappingEngine;

import am.app.feedback.FeedbackLoop;
import am.app.feedback.InitialMatchers;
import am.app.mappingEngine.Combination.CombinationMatcher;
import am.app.mappingEngine.LexicalMatcherJWNL.LexicalMatcherJWNL;
import am.app.mappingEngine.LexicalMatcherUMLS.LexicalMatcherUMLS;
import am.app.mappingEngine.PRAMatcher.OldPRAMatcher;
import am.app.mappingEngine.PRAMatcher.PRAMatcher;
import am.app.mappingEngine.PRAMatcher.PRAMatcher2;
import am.app.mappingEngine.PRAintegration.PRAintegrationMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.conceptMatcher.ConceptMatcher;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.app.mappingEngine.dsi.OldDescendantsSimilarityInheritanceMatcher;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.multiWords.MultiWordsMatcher;
import am.app.mappingEngine.oaei2009.OAEI2009matcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.ssc.SiblingsSimilarityContributionMatcher;
import am.app.mappingEngine.testMatchers.AllOneMatcher;
import am.app.mappingEngine.testMatchers.AllZeroMatcher;
import am.app.mappingEngine.testMatchers.CopyMatcher;
import am.app.mappingEngine.testMatchers.EqualsMatcher;
import am.app.mappingEngine.testMatchers.RandomMatcher;
//import am.app.mappingEngine.LexicalMatcherUMLS.LexicalMatcherUMLS;

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
	WordNetLexical		("Lexical Matcher: WordNet", LexicalMatcherJWNL.class),
	DSI					( "Descendant's Similarity Inheritance (DSI)", DescendantsSimilarityInheritanceMatcher.class ),
	SSC					( "Sibling's Similarity Contribution (SSC)", SiblingsSimilarityContributionMatcher.class ),
	Combination	( "Linear Weighted Combination (LWC)", CombinationMatcher.class ),
	ConceptSimilarity   ( "Concept Similarity", ConceptMatcher.class, false),
	OAEI2009   ( "OAEI2009 Matcher", OAEI2009matcher.class),
	UMLSKSLexical		("Lexical Matcher: UMLSKS", LexicalMatcherUMLS.class, false), //it requires internet connection and the IP to be registered
	
	//Auxiliary matchers created for specific purposes
	InitialMatcher      ("Initial Matcher: LWC (PSM+VMM+BSM)", InitialMatchers.class, false),
	PRAintegration   ( "PRA Integration", PRAintegrationMatcher.class, false), //this works fine
	PRAMatcher			("PRA Matcher", PRAMatcher.class, false),
	PRAMatcher2			("PRA Matcher2", PRAMatcher2.class, false),
	OldPRAMAtcher		("Old PRA Matcher", OldPRAMatcher.class, false),
	
	//WORK IN PROGRESS
	
	//MATCHERS USED BY THE SYSTEM, usually not shown
	UserManual			( "User Manual Matching", UserManualMatcher.class, false),
	UniqueMatchings		( "Unique Matchings", ReferenceAlignmentMatcher.class, false), // this is used by the "Remove Duplicate Alignments" UIMenu entry
	ImportAlignment	( "Import Alignments", ReferenceAlignmentMatcher.class, true),
	
	//TEST MATCHERS 
	Equals 				( "Local Name Equivalence Comparison", EqualsMatcher.class , false),
	AllOne 				( "(Test) All One Similarities", AllOneMatcher.class, true ),
	AllZero			( "(Test) All Zero Similarities", AllZeroMatcher.class, true ),
	Copy				( "Copy Matcher", CopyMatcher.class,false ),
	Random 				( "(Test) Random Similarities", RandomMatcher.class, true ),
	DSI2					( "OLD Descendant's Similarity Inheritance (DSI)", OldDescendantsSimilarityInheritanceMatcher.class, false ),
	UserFeedBackLoop ("User Feedback Loop", FeedbackLoop.class, false );
	
	/* Don't change anything below this line .. unless you intend to. */
	private boolean showInControlPanel;
	private String name;
	private String className;
	
	MatchersRegistry( String n, Class<?> matcherClass ) { name = n; className = matcherClass.getName(); showInControlPanel = true;}
	MatchersRegistry( String n, Class<?> matcherClass, boolean shown) { name = n; className = matcherClass.getName(); showInControlPanel = shown; }
	public String getMatcherName() { return name; }
	public String getMatcherClass() { return className; }
	public boolean isShown() { return showInControlPanel; }
	
}
