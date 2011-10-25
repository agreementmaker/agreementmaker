package am.app.mappingEngine.baseSimilarity.advancedSimilarity;

import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;

public class AdvancedSimilarityParameters extends BaseSimilarityParameters {

	public boolean useLabels = false;

	public AdvancedSimilarityParameters() { super(); }
	public AdvancedSimilarityParameters(double th, int s, int t) { super(th, s, t); }

}
