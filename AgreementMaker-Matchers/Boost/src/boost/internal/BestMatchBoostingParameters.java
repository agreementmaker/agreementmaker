package boost.internal;

import am.app.mappingEngine.DefaultMatcherParameters;

/**
 * @author cpesquita
 * @date Sept 22, 2011
 */
public class BestMatchBoostingParameters extends DefaultMatcherParameters{
	
	private static final long serialVersionUID = 194591157273240997L;
	
	public double boostPercent = 1.1; // the multiplicative boost factor that similarities will be modified by.
	public boolean deepCopy = false;  // deep copy of similarity matrix. if false, operate on the input matcher's similarity matrix.

	public BestMatchBoostingParameters() { super(); }
	public BestMatchBoostingParameters(double threshold, int maxSourceAlign, int maxTargetAlign) {
		super(threshold, maxSourceAlign, maxTargetAlign);
	}
}
