package agreementMaker.application.mappingEngine.Combination;

import java.util.ArrayList;

import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluationData;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluator;

public class CombinationParameters extends AbstractParameters {
	
	/**Available math operations to combine similarity values, all these operations are weighted, if user select no weights then weights will be 1 for all matchers*/
	final static String MAXCOMB = "Max similarity";
	final static String MINCOMB = "Min similarity";
	final static String AVERAGECOMB = "Average of similarities";
	final static String SIGMOIDAVERAGECOMB = "Sigmoid average";
	
	public String combinationType = AVERAGECOMB; //selected math operation.
	
	/**If the user select "Not weighted" or "manual weights" then this is false, and weights are taken from the parameters panel
	 * else if it's true weights are defined thorugh quality evaluation
	 */
	public boolean qualityEvaluation  = true;
	public String quality = QualityEvaluator.LOCALCONFIDENCE; //selected quality measure to be used to define weights
	
	//for each matcher there is an array of local weights for each node
	//when weights are manually assigned, each matcher as an array with the same value for all nodes. like if it is a global weight not local
	public double[] matchersWeights; 
		

}
