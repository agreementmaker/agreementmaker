package am.app.mappingEngine;

import java.awt.Color;
import java.util.EnumSet;

import am.app.Core;
import am.app.osgi.MatcherNotFoundException;
import am.app.osgi.OSGiRegistry;
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
	
	
	public static AbstractMatcher getMatcherInstance( String matcherName ) {
		try {
			return Core.getInstance().getFramework().getRegistry().getMatcherByName(matcherName);
		}
		catch (MatcherNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Return the real istance of the matcher given the selected nameindex
	 * the instanceIndex is the unique identifier of this algorithm, is the unique parameter of the constructor and is the identifier of the matcher instance in the run matchers list (the table of the AM)
	 *
	 * @deprecated Moving to OSGi. Use {@link OSGiRegistry#getMatcherByName(String)}
	 */
	public static AbstractMatcher getMatcherInstance(MatchersRegistry name, int instanceIndex) {
		
		Class<?> matcherClass = null;
		try {
			matcherClass = Class.forName( name.getMatcherClass() );
		} catch (ClassNotFoundException e) {
			System.out.println("DEVELOPER: You have entered a wrong class name in the MatcherRegistry");
			e.printStackTrace();
			return null;
		}
		
		AbstractMatcher a = null;
		try {
			a = (AbstractMatcher) matcherClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		// Set the Index in the Control Panel
		a.setIndex(instanceIndex);
		a.setID( Core.getInstance().getNextMatcherID() );  // used globally
		a.setRegistryEntry(name);
		
		// Set the color of the matcher
		//Color color = getColorFromIndex(instanceIndex);
		//a.setColor(color);
		
		return a;
	}

	
	/**
	 * Returns the MatchersRegistry entry of the matcher with the given name.
	 * @param name The String object representing the name of the matcher.
	 * @return The MatchersRegistry entry for the matcher.  If no matchers are found to equal the name, it returns the first one in the list.
	 */
	public static MatchersRegistry getMatchersRegistryEntry( String name ) {

		EnumSet<MatchersRegistry> matchers = EnumSet.allOf(MatchersRegistry.class);
		
		// Alternate suggestion: Do this with an iterator() instead of toArray and a for-loop.
		Object[] matchersArray = matchers.toArray();
		for( int i = 0; i < matchersArray.length; i++ ) {
			if( ((MatchersRegistry) matchersArray[i]).getMatcherName().equals(name)  ) {
				return (MatchersRegistry) matchersArray[i];
			}
		}
		return (MatchersRegistry) matchersArray[0];
		
	}
	
	/**
	 * Returns the MatchersRegistry entry of the matcher with the given class.
	 * @param name The Class object representing the Class of the matcher.
	 * @return The MatchersRegistry entry for the matcher.  If no matchers are found to equal the name, it returns null.
	 */
	public static MatchersRegistry getMatchersRegistryEntry( Class<? extends AbstractMatcher> cls ) {

		EnumSet<MatchersRegistry> matchers = EnumSet.allOf(MatchersRegistry.class);
		
		// Alternate suggestion: Do this with an iterator() instead of toArray and a for-loop.
		Object[] matchersArray = matchers.toArray();
		for( int i = 0; i < matchersArray.length; i++ ) {
			if( ((MatchersRegistry) matchersArray[i]).getMatcherClass().equals(cls.getName())  ) {
				return (MatchersRegistry) matchersArray[i];
			}
		}
		return null;
	}
	
	private static Color getColorFromIndex(int instanceIndex) {
		// TODO there should be an array of predefined colors
		int arrayIndex = (int) (instanceIndex % Colors.matchersColors.length); //this is the module operation, we need to do this because we may have more matchers then the possible colors in the array
		return Colors.matchersColors[arrayIndex];
	}

	public static boolean isTheUserMatcher(AbstractMatcher toBeDeleted) {
		return toBeDeleted.getRegistryEntry() == MatchersRegistry.UserManual && toBeDeleted.getIndex() == 0;
	}
	
	

}
