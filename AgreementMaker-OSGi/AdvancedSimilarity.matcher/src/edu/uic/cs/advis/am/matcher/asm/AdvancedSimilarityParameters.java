package edu.uic.cs.advis.am.matcher.asm;

import edu.uic.cs.advis.am.matcher.BaseSimilarityParameters;

/**
 * Parameters for the ASM.
 * 
 * @author Michele Caci
 *
 */
public class AdvancedSimilarityParameters extends BaseSimilarityParameters {

	private static final long serialVersionUID = -3660751356533812476L;
	
	public boolean useLabels = false;

	public AdvancedSimilarityParameters() { super(); }
	public AdvancedSimilarityParameters(double th, int s, int t) { super(th, s, t); }

}
