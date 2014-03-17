package am.app.mappingEngine.qualityEvaluation;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.utility.parameters.HasParameters;

/**
 * This quality metric produces a quality for a single mapping.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public interface LocalQualityMetric extends HasParameters {
	
	public double getQuality(alignType type, int i, int j);
	
}
