package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;

public class EqualsMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -1720265494288012598L;
	
	public EqualsMatcher() {
		super();
		
		setName("Equals Matcher");
		setCategory(MatcherCategory.UTILITY);
	}
	
	/**
	 * The simplest matcher known to man.
	 * Everything is inherited from AbstractMatcher.
	 */

}
