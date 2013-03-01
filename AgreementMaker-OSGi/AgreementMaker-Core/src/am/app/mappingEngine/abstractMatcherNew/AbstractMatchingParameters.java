package am.app.mappingEngine.abstractMatcherNew;

public class AbstractMatchingParameters {

	/**
	 * True if the algorithm needs additional parameters for the matching phase</br>
	 * in this case the developer must develop a JFrame to let the user define them
	 */
	protected boolean needsParam;
	
	/**
	 * Minimum and maximum number of input matchers</br>
	 * a generic matcher which doesn't need any inputs should have 0, 0
	 */
	protected int minInputMatchers;
	protected int maxInputMatchers;

	
	public int getMinInputMatchers() {
		return minInputMatchers;
	}
	public void setMinInputMatchers(int minInputMatchers) {
		this.minInputMatchers = minInputMatchers;
	}
	public int getMaxInputMatchers() {
		return maxInputMatchers;
	}
	public void setMaxInputMatchers(int maxInputMatchers) {
		this.maxInputMatchers = maxInputMatchers;
	}
	
}
