package agreementMaker.application.mappingEngine;

import java.awt.Color;
import java.util.EnumSet;

import agreementMaker.application.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import agreementMaker.application.mappingEngine.dsi.DescendantsSimilarityInheritanceMatcher;
import agreementMaker.application.mappingEngine.manualMatcher.EmptyMatcher;
import agreementMaker.application.mappingEngine.manualMatcher.UserManualMatcher;
import agreementMaker.application.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import agreementMaker.application.mappingEngine.ssc.SiblingsSimilarityContributionMatcher;
import agreementMaker.application.mappingEngine.testMatchers.AllOneMatcher;
import agreementMaker.application.mappingEngine.testMatchers.CopyMatcher;
import agreementMaker.application.mappingEngine.testMatchers.EqualsMatcher;
import agreementMaker.application.mappingEngine.testMatchers.RandomMatcher;
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
	public final static int EMPTYMATCHER = ALLUNOMATCHER+1;				// 4
	public final static int COPYMATCHER = EMPTYMATCHER+1;					// 5
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
		EnumSet<MatchersRegistry> matchers = EnumSet.allOf(MatchersRegistry.class);
		
		Object[] matchersArray = matchers.toArray();
		String[] matchersList = new String[matchersArray.length];
		for( int i = 0; i < matchersArray.length; i++ ) {
			matchersList[i] = ((MatchersRegistry) matchersArray[i]).getMatcherName();
		}
		
		return matchersList;
	}

	/**
	 * When adding a matcher add the line names[NEWINDEX] = "My name"; Name shouldn't be too long but at the same time should be a user clear name;
	 * @return the list of matchers names ordered by the indexes of each matcher, this is the same list shown in the AgreementMaker combo box, so the selectedIndex of the combobox must correspond to a valid matcher
	 */
	public static String[] getMatcherComboList() {
		EnumSet<MatchersRegistry> matchers = EnumSet.allOf(MatchersRegistry.class);

		Object[] matchersArray = matchers.toArray();

		int visibleMatchers = 0;
		for( int i = 0; i < matchersArray.length; i++ ) {
			if( ((MatchersRegistry) matchersArray[i]).isShown() ) {
				visibleMatchers++;
			}
		}
		

		int j = 0;
		String[] matchersList = new String[visibleMatchers];
		for( int i = 0; i < matchersArray.length; i++ ) {
			if( ((MatchersRegistry) matchersArray[i]).isShown() ) {
				matchersList[j] = ((MatchersRegistry) matchersArray[i]).getMatcherName();
				j++;
			}
			
		}
		
		return matchersList;
	}
	
	
	/**Return the real istance of the matcher given the selected nameindex
	 * the instanceIndex is the unique identifier of this algorithm, is the unique parameter of the constructor and is the identifier of the matcher instance in the run matchers list (the table of the AM)
	 * */
	public static AbstractMatcher getMatcherInstance(MatchersRegistry name, int instanceIndex) {
		
		Class matcherClass = null;
		try {
			matcherClass = Class.forName( name.getMatcherClass() );
		} catch (ClassNotFoundException e) {
			System.out.println("DEVELOPER: You have entered a wrong class name in the MatcherRegistry");
			e.printStackTrace();
		}
		
		AbstractMatcher a = null;
		try {
			a = (AbstractMatcher) matcherClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Set the Index in the Control Panel
		a.setIndex(instanceIndex);
		a.setName(name);
		
		// Set the color of the matcher
		Color color = getColorFromIndex(instanceIndex);
		a.setColor(color);
		
		return a;
	}

	public static MatchersRegistry getMatchersRegistryEntry( String name ) {
		
		
		EnumSet<MatchersRegistry> matchers = EnumSet.allOf(MatchersRegistry.class);
		
		Object[] matchersArray = matchers.toArray();

		for( int i = 0; i < matchersArray.length; i++ ) {
			if( ((MatchersRegistry) matchersArray[i]).getMatcherName() == name  ) {
				return (MatchersRegistry) matchersArray[i];
			}
		}
		
		return (MatchersRegistry) matchersArray[0];
		
	}
	
	private static Color getColorFromIndex(int instanceIndex) {
		// TODO there should be an array of predefined colors
		int arrayIndex = (int) (instanceIndex % Colors.matchersColors.length); //this is the module operation, we need to do this because we may have more matchers then the possible colors in the array
		return Colors.matchersColors[arrayIndex];
	}

	public static boolean isTheUserMatcher(AbstractMatcher toBeDeleted) {
		return toBeDeleted.getName() == MatchersRegistry.UserManual;
	}
	
	

}
