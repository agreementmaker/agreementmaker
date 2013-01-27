package am.matcher.Combination;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.mappingEngine.utility.OAEI_Track;

public class CombinationParameters extends DefaultMatcherParameters {

	/**Available math operations to combine similarity values, all these operations are weighted, if user select no weights then weights will be 1 for all matchers*/
	public final static String MAXCOMB = "Max similarity";
	public final static String MINCOMB = "Min similarity";
	public final static String AVERAGECOMB = "Average of similarities";
	public final static String SIGMOIDAVERAGECOMB = "Sigmoid average";
	
	public String combinationType = AVERAGECOMB; //selected math operation.
	
	/**If the user select "Not weighted" or "manual weights" then this is false, and weights are taken from the parameters panel
	 * else if it's true weights are defined thorugh quality evaluation
	 */
	public boolean qualityEvaluation  = true;
	public boolean manualWeighted = false;
	
	public QualityMetricRegistry quality = QualityMetricRegistry.LOCAL_CONFIDENCE; //selected quality measure to be used to define weights
	
	//for each matcher there is an array of local weights for each node
	//when weights are manually assigned, each matcher as an array with the same value for all nodes. like if it is a global weight not local
	public double[] matchersWeights;

	public CombinationParameters() { super(); }
	public CombinationParameters(double th, int s, int t) { super(th, s, t); }
	
	
	public void initForOAEI2009() {
		combinationType = AVERAGECOMB;
		qualityEvaluation = true;
		manualWeighted = false;
		quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
		
	} 
	
	/**
	 * 
	 * @param t
	 * @param layer Which LWC are we configuring.
	 * @throws Exception 
	 */
	public CombinationParameters initForOAEI2010(OAEI_Track t, boolean firstLWC) throws Exception {
		
		switch(t) {
		case Benchmarks:
			if( firstLWC ) {
				combinationType = AVERAGECOMB;
				qualityEvaluation = true;
				manualWeighted = false;
				quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			} else {
				combinationType = MAXCOMB;
				qualityEvaluation = true;
				manualWeighted = false;
				quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			}
			break;
		case Anatomy:
			if( firstLWC ) {
				combinationType = AVERAGECOMB;
				qualityEvaluation = true;
				manualWeighted = false;
				quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			}
			else {
				throw new Exception("Don't know what to do here.");
			}
			break;
		case Conference:
			combinationType = AVERAGECOMB;
			qualityEvaluation = true;
			manualWeighted = false;
			quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			break;
		default:
			combinationType = AVERAGECOMB;
			qualityEvaluation = true;
			manualWeighted = false;
			quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
		}
		
		return this;
	}
		

}
