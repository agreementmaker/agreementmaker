package agreementMaker.application.mappingEngine.manualCombination;

import agreementMaker.application.mappingEngine.AbstractParameters;

public class ManualCombinationParameters extends AbstractParameters {

	final static String MAXCOMB = "Max similarity";
	final static String MINCOMB = "Min similarity";
	final static String AVERAGECOMB = "Average of similarities";
	final static String WEIGHTAVERAGE = "Weighted Average of similarities";
	
	public String combinationType = AVERAGECOMB;
	public double[] weights; 
		

}
