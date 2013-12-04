package am.app.mappingEngine;

import java.util.List;

import am.app.Core;
import am.app.osgi.MatcherNotFoundException;

/**
 * A matcher registry returns a list of matchers currently available to the
 * system.
 * 
 * @author cosmin
 * 
 */
public abstract class MatcherRegistry {

	/**
	 * @return A list of the matching algorithms currently available to the system.
	 * 
	 * <p>
	 * TODO: Change this to return a List&lt;MatchingAlgorithm&gt; ?
	 * </p>
	 */
	public abstract List<AbstractMatcher> getMatchers();
	
	/**
	 * @return A list of the selection 
	 */
	public abstract List<SelectionAlgorithm> getSelectors();
	
	/**
	 * This is only a helper method.
	 * 
	 * @param clazz
	 *            The fully qualified class name of the matching algorithm.
	 * @return The AbstractMatcher that matches the class name.
	 * @throws MatcherNotFoundException
	 *             Rather than return null, we throw an exception.
	 */
	public AbstractMatcher getMatcherByClass(String clazz) throws MatcherNotFoundException {
		for(AbstractMatcher m : getMatchers()){
			if(m.getClass().getName().equals(clazz)){
				try {
					AbstractMatcher newM = m.getClass().newInstance();
					newM.setID(Core.getInstance().getNextMatcherID());
					return newM;
				} catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		throw new MatcherNotFoundException("'" + clazz + "' is not a valid class name in the system.");
	}
}
