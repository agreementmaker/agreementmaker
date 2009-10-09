package am.app.mappingEngine;

import java.awt.Color;
import java.util.EnumSet;

import am.userInterface.Colors;

public class MatcherFactory {
	
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
		
		Class<?> matcherClass = null;
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
