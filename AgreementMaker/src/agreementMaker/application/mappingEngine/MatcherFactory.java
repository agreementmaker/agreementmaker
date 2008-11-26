package agreementMaker.application.mappingEngine;

import java.awt.Color;

import agreementMaker.application.mappingEngine.Matchers.BaseSimilarityMatcher;
import agreementMaker.application.mappingEngine.Matchers.DescendantsSimilarityInheritanceMatcher;
import agreementMaker.application.mappingEngine.Matchers.SiblingsSimilarityContributionMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.AllOneMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.AllZeroMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.CopyMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.EqualsMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.RandomMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.ReferenceAlignmentMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.UserManualMatcher;
import agreementMaker.userInterface.Colors;

public class MatcherFactory {
	
	
	
	/**List of indexes of matchers methods, each matcher has his own index different from all the others
	 * Indexes must be between 0 and numMatcher -1
	 * ADDING A NEW ALGORITHM:
	 * increase global variable numMatchers by one
	 * add a new final static int index, it is suggested to use the last index+1, so that deleting will be easier
	 * also add the matcher to getMatcherNames() using the index selected, example: names[MYFINALINDEX] = "my name";
	 * also add the matcher to getMatcherInstance() using the index
	 * REMOVING AN ALGORITHM: 
	 * decrease numMatchers by one, 
	 * remove that matcher index and adjust the list,
	 *  also remove the matcher from getMatcherNames(), just removing that line
	 *  remove it from the getInstance() method
	 * ALWAYS CHECK that numMatchers, the list of indexes and MatcherNames are consistent when applying a change
	 */
	// TODO: ENUM! Look below to the next todo.								// COUNT
	public final static int EQUALSMATCHER = 0;								// 1
	public final static int RANDOMMATCHER = EQUALSMATCHER+1;				// 2
	public final static int ALLUNOMATCHER = RANDOMMATCHER+1;				// 3
	public final static int ALLZEROMATCHER = ALLUNOMATCHER+1;				// 4
	public final static int COPYMATCHER = ALLZEROMATCHER+1;					// 5
	public final static int BASESIMILARITYMATCHER = COPYMATCHER+1;			// 6
	public final static int DSIMATCHER = BASESIMILARITYMATCHER+1;			// 7
	public final static int SSCMATCHER = DSIMATCHER+1;						// 8
	public final static int REFERENCEMATCHER = SSCMATCHER+1; 				// 9
	/**Total number of matching algorithm that will be visualized in the agreememtmaker
	 * Remember to modify this value when adding and removing an algorithm
	 * */
	public final static int numMatchers = 9;  // look at COUNT above
	
	/**
	 * When adding a matcher add the line names[NEWINDEX] = "My name"; Name shouldn't be too long but at the same time should be a user clear name;
	 * @return the list of matchers names ordered by the indexes of each matcher, this is the same list shown in the AgreementMaker combo box, so the selectedIndex of the combobox must correspond to a valid matcher
	 */
	public static String[] getMatcherNames() {
		String[] names = new String[numMatchers];
		names[EQUALSMATCHER] = "Local Name equivalence comparison";
		names[RANDOMMATCHER] = "Random Similarity matcher";
		names [ALLUNOMATCHER] = "All ONE similarities";
		names[ALLZEROMATCHER] = "All ZERO similarities";
		names[COPYMATCHER] = "Copy Matcher";
		names[BASESIMILARITYMATCHER] = "Base Similarity";
		names[DSIMATCHER] = "Descendant's Similarity Inheritance (DSI)";
		names[SSCMATCHER] = "Sibling's Similarity Contribution (SSC)";
		names[REFERENCEMATCHER] = "Reference Alignment";
		return names;
	}
	
	/**
	 * Enum for keeping the current list of matchers in the system.
	 */
	/** TODO: I will start this sometime .... @author cosmin @date Nov 26, 2008 */
	
	
	
	/**Return the real istance of the matcher given the selected nameindex
	 * the instanceIndex is the unique identifier of this algorithm, is the unique parameter of the constructor and is the identifier of the matcher instance in the run matchers list (the table of the AM)
	 * */
	public static AbstractMatcher getMatcherInstance(int nameIndex, int instanceIndex) {
		AbstractMatcher a = null;
		String[] names = getMatcherNames();
		String name = names[nameIndex];
		if(nameIndex == EQUALSMATCHER) {
			a = new EqualsMatcher(instanceIndex, name);
		}
		else if(nameIndex == RANDOMMATCHER) {
			a = new RandomMatcher(instanceIndex,name);
		}
		else if(nameIndex == ALLUNOMATCHER) {
			a = new AllOneMatcher(instanceIndex,name);
		}
		else if(nameIndex == ALLZEROMATCHER) {
			a = new AllZeroMatcher(instanceIndex,name);
		}
		else if(nameIndex == COPYMATCHER) {
			a = new CopyMatcher(instanceIndex,name);
		}
		else if(nameIndex == BASESIMILARITYMATCHER) {
			a = new BaseSimilarityMatcher(instanceIndex, name);
		}
		else if( nameIndex == DSIMATCHER ) {
			a = new DescendantsSimilarityInheritanceMatcher(instanceIndex, name);
		}
		else if( nameIndex == SSCMATCHER ) {
			a = new SiblingsSimilarityContributionMatcher(instanceIndex, name);
		}
		else if( nameIndex == REFERENCEMATCHER ) {
			a = new ReferenceAlignmentMatcher(instanceIndex, name);
		}
		else {
			throw new RuntimeException("DEVELOPMENT ERROR: there is a matcher in the list with no corrisponding index in getMatcherInstance");
		}
		Color color = getColorFromIndex(instanceIndex);
		a.setColor(color);
		return a;
	}

	private static Color getColorFromIndex(int instanceIndex) {
		// TODO there should be an array of predefined colors
		int arrayIndex = (int) (instanceIndex % Colors.matchersColors.length); //this is the module operation, we need to do this because we may have more matchers then the possible colors in the array
		return Colors.matchersColors[arrayIndex];
	}

	public static boolean isTheUserMatcher(AbstractMatcher toBeDeleted) {
		return toBeDeleted.getName().equals(UserManualMatcher.USERMANUALMATCHINGNAME);
	}
	
	

}
