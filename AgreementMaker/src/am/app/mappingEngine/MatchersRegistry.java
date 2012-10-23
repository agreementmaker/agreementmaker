package am.app.mappingEngine;

import am.app.feedback.FeedbackLoop;
import am.app.feedback.InitialMatchers;
import am.app.mappingEngine.Combination.CombinationMatcher;
import am.app.mappingEngine.LexicalMatcherJAWS.LexicalMatcherJAWS;
import am.app.mappingEngine.LexicalMatcherJWNL.LexicalMatcherJWNL;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcherWeighted;
import am.app.mappingEngine.MachineLearning.machineLearningMatcher;
import am.app.mappingEngine.PRAMatcher.OldPRAMatcher;
import am.app.mappingEngine.PRAMatcher.PRAMatcher;
import am.app.mappingEngine.PRAMatcher.PRAMatcher2;
import am.app.mappingEngine.PRAintegration.PRAintegrationMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.basicStructureSelector.BasicStructuralSelectorMatcher;
import am.app.mappingEngine.boosting.BestMatchBoosting;
import am.app.mappingEngine.conceptMatcher.ConceptMatcher;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import am.app.mappingEngine.dsi.OldDescendantsSimilarityInheritanceMatcher;
import am.app.mappingEngine.hierarchy.HierarchyMatcher;
import am.app.mappingEngine.hierarchy.HierarchyMatcherModified;
import am.app.mappingEngine.hierarchy.HierarchyStructureMatcher;
import am.app.mappingEngine.hierarchy.WordnetSubclassMatcher;
import am.app.mappingEngine.instanceMatchers.InstanceMatcherFedeNew;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.mediatingMatcher.MediatingMatcher;
import am.app.mappingEngine.multiWords.MultiWordsMatcher;
import am.app.mappingEngine.multiWords.MultiWordsMatcherPairWise;
import am.app.mappingEngine.multiWords.newMW.NewMultiWordsMatcher;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011Matcher;
import am.app.mappingEngine.oaei2009.OAEI2009matcher;
import am.app.mappingEngine.oaei2010.OAEI2010Matcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.ssc.SiblingsSimilarityContributionMatcher;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.PartialGraphMatcher;
import am.app.mappingEngine.testMatchers.AllOneMatcher;
import am.app.mappingEngine.testMatchers.AllZeroMatcher;
import am.app.mappingEngine.testMatchers.CopyMatcher;
import am.app.mappingEngine.testMatchers.EqualsMatcher;
import am.app.mappingEngine.testMatchers.RandomMatcher;
import am.app.mappingEngine.wikipedia.WikiMatcher;
import am.extension.MyTestMatcher;


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
	//
	HierStructSim		( "HSSM",  "Hierarchy Structure Similarity Matcher", HierarchyStructureMatcher.class, MatcherCategory.STRUCTURAL),
	Boost				( "Boost", "Best Match Boosting", BestMatchBoosting.class, MatcherCategory.LEXICAL ), 
	LSMWeighted			( "LSMW", "LSM Weighted", LexicalSynonymMatcherWeighted.class, MatcherCategory.LEXICAL),
	VMMPairwise			( "VMM-PW", "VMM PairWise", MultiWordsMatcherPairWise.class, MatcherCategory.SYNTACTIC ),
	NewVMM				( "NVMM", "New VMM", NewMultiWordsMatcher.class, MatcherCategory.SYNTACTIC ),
	BridgeMatcher		( "MEDM", "Mediating Matcher", MediatingMatcher.class, MatcherCategory.HYBRID ), 
	MyTestMatch			( "MTM", "Synonym Term Counting Matcher", MyTestMatcher.class, MatcherCategory.USER ),
	PartialGraph		( "PGM", "Partial Graph Matcher", PartialGraphMatcher.class, MatcherCategory.STRUCTURAL ),
	SimilarityFlooding  ( "SFM", "Similarity Flooding Matcher", am.app.mappingEngine.structuralMatchers.similarityFlooding.sfm.IndipendentSimilarityFlooding.class ),
	AnchorFlood			( "AFM", "AnchorFlood Matcher", am.app.mappingEngine.structuralMatchers.similarityFlooding.anchorFlood.AnchorFloodMatcher.class ),
	OAEI2009   			( "OAEI-09", "OAEI 2009 Matcher", OAEI2009matcher.class, MatcherCategory.HYBRID),
	OAEI2010			( "OAEI-10", "OAEI 2010 Matcher", OAEI2010Matcher.class, MatcherCategory.HYBRID),
	OAEI2011			( "OAEI-11", "OAEI 2011 Matcher", OAEI2011Matcher.class, MatcherCategory.HYBRID),
	IterativeMatcher	( "IISM", "Instance-based Iterator", am.app.mappingEngine.instance.IterativeMatcher.class),
	AdvancedSimilarity  ( "ASM", "Advanced Similarity Matcher", am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher.class, MatcherCategory.SYNTACTIC),
	GroupFinder			( "GFM", "Group Finder Matcher", am.app.mappingEngine.groupFinder.GroupFinderMatcher.class),
	IISM				( "IISM", "Iterative Instance and Structural Matcher", am.app.mappingEngine.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher.class, MatcherCategory.STRUCTURAL),
	LSM					( "LSM", "Lexical Synonym Matcher", LexicalSynonymMatcher.class, MatcherCategory.LEXICAL ),
	WSM			( "WSC", "Wordnet Subclass Matcher", WordnetSubclassMatcher.class, MatcherCategory.LEXICAL),
	//OFFICIAL MATCHERS
	LexicalJAWS			( "JAWS", "Lexical Matcher: JAWS", LexicalMatcherJAWS.class, MatcherCategory.LEXICAL ),
	BaseSimilarity		( "BSM", "Base Similarity Matcher", BaseSimilarityMatcher.class, MatcherCategory.SYNTACTIC ),
	ParametricString 	( "PSM", "Parametric String Matcher",	 ParametricStringMatcher.class, MatcherCategory.SYNTACTIC ),
	MultiWords       	( "VMM", "Vector-based Multi-Words Matcher", MultiWordsMatcher.class, MatcherCategory.SYNTACTIC),
	WordNetLexical		( "LM-WN", "Lexical Matcher: WordNet", LexicalMatcherJWNL.class, MatcherCategory.LEXICAL),
	DSI					( "DSI", "Descendant's Similarity Inheritance", DescendantsSimilarityInheritanceMatcher.class, MatcherCategory.STRUCTURAL ),
	BSS					( "BSS", "Basic Structure Selector Matcher", BasicStructuralSelectorMatcher.class ),
	SSC					( "SSC", "Sibling's Similarity Contribution", SiblingsSimilarityContributionMatcher.class, MatcherCategory.STRUCTURAL ),
	Combination			( "LWC", "Linear Weighted Combination", CombinationMatcher.class, MatcherCategory.COMBINATION ),
	mlm					( "mlm", "Machine Learning Matcher", machineLearningMatcher.class, MatcherCategory.COMBINATION ),
	ConceptSimilarity   ( "Concept Similarity", ConceptMatcher.class, false),
	//UMLSKSLexical		("Lexical Matcher: UMLSKS", LexicalMatcherUMLS.class, false), //it requires internet connection and the IP to be registered
	
	//Auxiliary matchers created for specific purposes
	InitialMatcher      ( "Initial Matcher: LWC (PSM+VMM+BSM)", InitialMatchers.class, true),
	PRAintegration   	( "PRA Integration", PRAintegrationMatcher.class, false), //this works fine
	PRAMatcher			( "PRA Matcher", PRAMatcher.class, false),
	PRAMatcher2			( "PRA Matcher2", PRAMatcher2.class, false),
	OldPRAMAtcher		( "Old PRA Matcher", OldPRAMatcher.class, false),
	
	//WORK IN PROGRESS
	HierarchyMatcher	("Hierarchy Matcher", HierarchyMatcher.class, true), 
	HierarchyMatcherModified	("Hierarchy Matcher Modified", HierarchyMatcherModified.class, true),
	WikipediaMatcher	("Wikipedia Matcher", WikiMatcher.class, true),
	
	//MATCHERS USED BY THE SYSTEM, usually not shown
	UserManual			( "USER", "User Manual Matching", UserManualMatcher.class, false),
	UniqueMatchings		( "Unique Matchings", ReferenceAlignmentMatcher.class, false), // this is used by the "Remove Duplicate Alignments" UIMenu entry
	ImportAlignment		( "IMPORT", "Import Alignments", ReferenceAlignmentMatcher.class, true),
	
	//TEST MATCHERS 
	Equals 				( "Local Name Equivalence Comparison", EqualsMatcher.class , false),
	AllOne 				( "(Test) All One Similarities", AllOneMatcher.class, MatcherCategory.UTILITY, true ),
	AllZero				( "(Test) All Zero Similarities", AllZeroMatcher.class, MatcherCategory.UTILITY, true ),
	Copy				( "Copy Matcher", CopyMatcher.class, MatcherCategory.UTILITY, false ),
	Random 				( "(Test) Random Similarities", RandomMatcher.class, MatcherCategory.UTILITY, true ),
	DSI2				( "OLD Descendant's Similarity Inheritance (DSI)", OldDescendantsSimilarityInheritanceMatcher.class, MatcherCategory.STRUCTURAL, false ),
	UserFeedBackLoop 	("User Feedback Loop", FeedbackLoop.class, false ),
	
	//INSTANCE MATCHERS
	MyInstMatch			( "MIM", "My Instance Matcher", InstanceMatcherFedeNew.class, MatcherCategory.USER );
	
	
	/* Don't change anything below this line .. unless you intend to. */
	private boolean showInControlPanel;
	private String name;
	private String shortName;
	private Class<? extends AbstractMatcher> className;
	private MatcherCategory category;
	
	/* 	Constructors */
	
	MatchersRegistry( String sn, String n, Class<? extends AbstractMatcher> matcherClass ) { 
		shortName = sn; name = n; className = matcherClass; showInControlPanel = true; category = MatcherCategory.UNCATEGORIZED;
	}
	
	MatchersRegistry( String sn, String n, Class<? extends AbstractMatcher> matcherClass, MatcherCategory c ) { 
		shortName = sn; name = n; className = matcherClass; showInControlPanel = true; category = c;
	}
	
	MatchersRegistry( String n, Class<? extends AbstractMatcher> matcherClass ) { 
		shortName = ""; name = n; className = matcherClass; showInControlPanel = true; category = MatcherCategory.UNCATEGORIZED;
	}
	
	MatchersRegistry( String n, Class<? extends AbstractMatcher> matcherClass, MatcherCategory c ) { 
		shortName = ""; name = n; className = matcherClass; showInControlPanel = true; category = c;
	}
	
	MatchersRegistry( String n, Class<? extends AbstractMatcher> matcherClass, boolean shown) { 
		name = n; className = matcherClass; showInControlPanel = shown; category = MatcherCategory.UNCATEGORIZED; 
	}
	
	MatchersRegistry( String n, Class<? extends AbstractMatcher> matcherClass, MatcherCategory c, boolean shown) { 
		name = n; className = matcherClass; showInControlPanel = shown; category = c; 
	}
	
	MatchersRegistry( String sn, String n, Class<? extends AbstractMatcher> matcherClass, boolean shown) { 
		shortName = sn; name = n; className = matcherClass; showInControlPanel = shown; category = MatcherCategory.UNCATEGORIZED; 
	}
	
	MatchersRegistry( String sn, String n, Class<? extends AbstractMatcher> matcherClass, MatcherCategory c, boolean shown) { 
		shortName = sn; name = n; className = matcherClass; showInControlPanel = shown; category = c; 
	}
	
	/* Getters and setters */
	
	public String getMatcherName() { return name; }
	public String getMatcherShortName() { return shortName; }
	public String getMatcherClass() { return className.getName(); }
	public Class<? extends AbstractMatcher> getMatcher() { return className; }
	public MatcherCategory getCategory() { return category; }
	public boolean isShown() { return showInControlPanel; }
	public String toString() { return name; }
	
	/**
	 * Returns the matcher with the given name.
	 * @param matcherName The name of the matcher.
	 * @return The MatchersRegistry representation of the matcher (used with MatcherFactory).  
	 */
/*  // This method duplicates MatcherFactory.getMatchersRegistryEntry( matcherName )
	public static MatchersRegistry getMatcherByName( String matcherName ) {
		
		EnumSet<MatchersRegistry> matchers = EnumSet.allOf(MatchersRegistry.class);
		
		Iterator<MatchersRegistry> entryIter = matchers.iterator();
		while( entryIter.hasNext() ) {
			MatchersRegistry currentEntry = entryIter.next();
			if( currentEntry.getMatcherName().equals(matcherName) ) return currentEntry;
		}
		
		return null;
	}
*/
	
}
