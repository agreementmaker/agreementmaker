package am.app.mappingEngine;

import java.util.EnumSet;
import java.util.List;

import am.app.Core;
import am.app.osgi.MatcherNotFoundException;

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
	public static AbstractMatcher[] getMatcherComboList() {
		List<AbstractMatcher> matchers = Core.getInstance().getRegistry().getMatchers();
		return matchers.toArray(new AbstractMatcher[0]);
	}
	
	
	/**
	 * @param matcherClass The matcher's class name.
	 */
	public static AbstractMatcher getMatcherInstance( String matcherClass ) throws MatcherNotFoundException {
		return Core.getInstance().getRegistry().getMatcherByClass(matcherClass);
	}
	
	/**
	 * The reason this method is deprecated is because to call it you need to
	 * import the class of the matcher you're looking for, which does not allow
	 * decoupling of dependencies, and defeats the purpose of this
	 * MatcherFactory in the first place.
	 * 
	 * @deprecated Use {@link #getMatcherInstance(String)}
	 */
	@Deprecated
	public static AbstractMatcher getMatcherInstance( Class<? extends AbstractMatcher> clazz ) throws MatcherNotFoundException {
		return Core.getInstance().getRegistry().getMatcherByClass(clazz.getName());
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

	public static boolean isTheUserMatcher(AbstractMatcher toBeDeleted) {
		return toBeDeleted.getRegistryEntry() == MatchersRegistry.UserManual && toBeDeleted.getIndex() == 0;
	}
	
	

}
